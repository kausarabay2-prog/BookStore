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
    }
}