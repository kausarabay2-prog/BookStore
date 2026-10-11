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
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ email, password })
        });

        const data = await response.json();

        if (!response.ok) {
            alert(data.message || data.error || 'Login failed');
            return;
        }

        const token = typeof data === 'string' ? data : data.token;

        if (!token) {
            console.error('Login response:', data);
            alert('Login response does not contain a token. Check the backend response.');
            return;
        }

        localStorage.setItem('token', token);
        localStorage.setItem('kitapUser', JSON.stringify(data));

        alert('Login successful!');
        history.replaceState({}, '', '/');
        route();

    } catch (error) {
        console.error('Login error:', error);
        alert('Could not connect to the server.');
    }

    const token = data.token || data.accessToken || data.jwt;

    if (!token) {
        throw new Error('Login response did not include a JWT token');
    }


    const response = await fetch('/api/admin/books', {
        headers: { Authorization: `Bearer ${token}` }
    });
    localStorage.setItem('token', data.token);
    headers: {
        Authorization: `Bearer ${localStorage.getItem('token')}`

        async function updateBook(id, bookData) {
            const token = localStorage.getItem("token");

            if (!token) {
                throw new Error("Алдымен жүйеге кіріңіз.");
            }

            const response = await fetch(`/api/admin/books/${id}`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify(bookData)
            });

            if (!response.ok) {
                const message = await response.text();
                throw new Error(`Кітап жаңартылмады (${response.status}): ${message}`);
            }

            return await response.json();
        }

        async function saveBookChanges(id) {
            try {
                const bookData = {
                    title: document.getElementById("title").value.trim(),
                    author: document.getElementById("author").value.trim(),
                    price: Number(document.getElementById("price").value),
                    description: document.getElementById("description").value.trim(),
                    imageUrl: document.getElementById("imageUrl").value.trim(),
                    categoryId: Number(document.getElementById("categoryId").value)
                };

                if (!bookData.title || !bookData.author) {
                    alert("Кітаптың атауы мен авторын толтыр.");
                    return;
                }

                if (!Number.isFinite(bookData.price) || bookData.price < 0) {
                    alert("Кітап бағасын дұрыс енгіз.");
                    return;
                }

                const updatedBook = await updateBook(id, bookData);

                alert("Кітап сәтті жаңартылды!");
                console.log("Updated book:", updatedBook);

                // Жобаңа сәйкес кітаптар тізімін қайта жүкте.
                // Мысалы: await loadBooks();

            } catch (error) {
                console.error(error);
                alert(error.message);
            }
        }

    }
}