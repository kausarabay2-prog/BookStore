function route() {
    let path = location.pathname;
    const loggedIn = Boolean(localStorage.getItem('kitapUser'));

    // Алдымен кірмеген адамға Login/Register көрсету
    if (!loggedIn && path !== '/login') {
        history.replaceState({}, '', '/login');
        path = '/login';
    }

    // Кірген адам Login бетінде қалмасын
    if (loggedIn && path === '/login') {
        history.replaceState({}, '', '/');
        path = '/';
    }

    document.querySelectorAll('.view').forEach(view => {
        view.classList.remove('active');
    });

    if (path === '/login') {
        $('#loginView').classList.add('active');
    } else if (path === '/cart') {
        $('#cartView').classList.add('active');
        loadCart();
    } else if (path === '/admin') {
        $('#adminView').classList.add('active');
        loadAdmin();
    } else if (path === '/details') {
        $('#detailsView').classList.add('active');
        loadDetails(new URLSearchParams(location.search).get('id'));
    } else {
        $('#storeView').classList.add('active');
        loadBooks();

        async function updateBook(id, bookData) {
            const token = localStorage.getItem("token");

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
                throw new Error(`Update failed (${response.status}): ${message}`);
            }

            return await response.json();
        }

    }
}