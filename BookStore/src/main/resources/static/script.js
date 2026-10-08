async function loadAdmin() {
    const tableBody = $('#adminRows') || $('#bookRows');

    if (!tableBody) {
        console.error('Book table body is missing: expected #adminRows or #bookRows');
        return;
    }

    try {
        const books = await api('/api/admin/books');

        tableBody.innerHTML = books.map(book => `
            <tr>
                <td>${esc(book.id)}</td>
                <td>${esc(book.title)}</td>
                <td>${esc(book.author)}</td>
                <td>${money(book.price)}</td>
                <td>
                    <button class="btn secondary small"
                        onclick="editBook(${Number(book.id)})">Edit</button>
                    <button class="btn danger small"
                        onclick="deleteBook(${Number(book.id)})">Delete</button>
                    <button class="btn small"
                        onclick="selectBook(${Number(book.id)})">Show Details</button>
                </td>
            </tr>
        `).join('');
    } catch (error) {
        tableBody.innerHTML =
            `<tr><td colspan="5">${esc(error.message)}</td></tr>`;
    }
}function route() {
    let path = location.pathname;
    const loggedIn = Boolean(localStorage.getItem('kitapUser'));

    if (!loggedIn && path !== '/login') {
        history.replaceState({}, '', '/login');
        path = '/login';
    }

    if (loggedIn && path === '/login') {
        history.replaceState({}, '', '/');
        path = '/';
    }

    document.querySelectorAll('.view').forEach(view => {
        view.classList.remove('active');
    });

    if (path === '/login') {
        document.querySelector('#loginView')?.classList.add('active');
    } else if (path === '/cart') {
        document.querySelector('#cartView')?.classList.add('active');
        loadCartItems();
    } else if (path === '/admin') {
        document.querySelector('#adminView')?.classList.add('active');
        loadAdmin?.();
    } else if (path === '/details') {
        document.querySelector('#detailsView')?.classList.add('active');
        loadDetails?.(new URLSearchParams(location.search).get('id'));
    } else {
        document.querySelector('#storeView')?.classList.add('active');
        loadBooks?.();
    }
}

async function handleLogin(event) {
    event.preventDefault();

    const email = document.getElementById('login-username').value.trim();
    const password = document.getElementById('login-password').value;

    try {
        const response = await fetch(`${API}/api/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });

        if (!response.ok) {
            const error = await response.text();
            alert(error || 'Login failed');
            return;
        }

        const user = await response.json();
        localStorage.setItem('kitapUser', JSON.stringify(user));

        alert('Login successful!');
        history.replaceState({}, '', '/');
        route();
    } catch (error) {
        alert('Could not connect to the server.');
        console.error(error);
    }
}

async function loadCartItems() {
    const cartId = localStorage.getItem('kitapCart');
    const cartItems = document.querySelector('#cartItems');

    if (!cartId) {
        if (cartItems) cartItems.innerHTML = '<p>Your cart is empty.</p>';
        const count = document.querySelector('#cartCount');
        if (count) count.textContent = '0';
        return [];
    }

    try {
        const response = await fetch(`${API}/api/cart/${cartId}`);
        if (!response.ok) throw new Error(`Could not load cart (${response.status})`);

        const items = await response.json();
        const count = document.querySelector('#cartCount');

        if (count) {
            count.textContent = items.reduce(
                (sum, item) => sum + Number(item.quanity || 0),
                0
            );
        }

        if (cartItems) {
            cartItems.innerHTML = items.length
                ? items.map(item => `
                    <div class="cart-row">
                        <span>${escapeHtml(item.bookTitle)} × ${Number(item.quanity || 0)}</span>
                        <b>${money(Number(item.price || 0) * Number(item.quanity || 0))}</b>
                    </div>
                  `).join('')
                : '<p>Your cart is empty.</p>';
        }

        const total = items.reduce(
            (sum, item) =>
                sum + Number(item.price || 0) * Number(item.quanity || 0),
            0
        );

        const totalElement = document.querySelector('#cartTotal');
        if (totalElement) totalElement.textContent = items.length ? `Total: ${money(total)}` : '';

        return items;
    } catch (error) {
        console.error('Failed to load cart items:', error);
        notify(error.message);
        return [];
    }
}

async function openCart() {
    await loadCartItems();
    document.querySelector('#cartModal')?.classList.add('show');
}

async function clearCart() {
    const cartId = localStorage.getItem('kitapCart');
    if (!cartId) return;

    try {
        const response = await fetch(`${API}/api/cart/${cartId}`, {
            method: 'DELETE'
        });

        if (!response.ok) throw new Error(`Could not clear cart (${response.status})`);

        await loadCartItems();
    } catch (error) {
        notify(error.message);
    }window.selectBook = async function(id) {
        try {
            const book = await api('/api/books/' + encodeURIComponent(id));
            const details = $('#bookDetails') || $('#detailsContent');

            if (!details) {
                notify('Add a details container with id="bookDetails" to the HTML.');
                return;
            }

            details.innerHTML = `
            <table class="details">
                <tr><td>Book ID</td><td>${esc(book.id)}</td></tr>
                <tr><td>Book Name</td><td>${esc(book.title)}</td></tr>
                <tr><td>Book Author</td><td>${esc(book.author)}</td></tr>
                <tr><td>Book Price</td><td>${money(book.price)}</td></tr>
                <tr><td>Category</td><td>${esc(book.categoryName || '')}</td></tr>
                <tr><td>Description</td><td>${esc(book.description || '')}</td></tr>
            </table>
            ${book.imageUrl
                ? `<img class="cover" src="${esc(book.imageUrl)}" alt="${esc(book.title)}">`
                : ''}
        `;
        } catch (error) {
            notify(error.message);
        }
    };
    
}