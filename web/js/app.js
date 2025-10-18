function updateAuthUI() {
    const authSection = document.querySelector('.auth-section');
    if (!authSection) return;

    if (api.isAuthenticated()) {
        const user = api.getUser();
        authSection.innerHTML = `
            <span style="color: var(--cream); font-weight: bold;">🏹 ${user.username}</span>
            <button class="btn btn-outline" onclick="logout()">Logout</button>
        `;
    } else {
        authSection.innerHTML = `
            <button class="btn btn-outline" onclick="showLoginModal()">Login</button>
            <button class="btn btn-primary" onclick="showRegisterModal()">Register</button>
        `;
    }
}

function showLoginModal() {
    document.getElementById('loginModal').classList.add('active');
}

function showRegisterModal() {
    document.getElementById('registerModal').classList.add('active');
}

function closeModal(modalId) {
    document.getElementById(modalId).classList.remove('active');
}

async function handleLogin(event) {
    event.preventDefault();
    const username = document.getElementById('loginUsername').value;
    const password = document.getElementById('loginPassword').value;
    const errorDiv = document.getElementById('loginError');

    try {
        await api.login(username, password);
        closeModal('loginModal');
        updateAuthUI();
        showAlert('Welcome back, warrior! 🏹', 'success');
        if (window.loadBounties) {
            loadBounties();
        }
    } catch (error) {
        errorDiv.textContent = error.message;
        errorDiv.classList.remove('hidden');
    }
}

async function handleRegister(event) {
    event.preventDefault();
    const username = document.getElementById('registerUsername').value;
    const email = document.getElementById('registerEmail').value;
    const password = document.getElementById('registerPassword').value;
    const errorDiv = document.getElementById('registerError');

    try {
        await api.register(username, email, password);
        closeModal('registerModal');
        showAlert('Registration successful! Now login to begin your journey.', 'success');
        showLoginModal();
    } catch (error) {
        errorDiv.textContent = error.message;
        errorDiv.classList.remove('hidden');
    }
}

function logout() {
    api.clearAuth();
    updateAuthUI();
    showAlert('May your path be blessed. See you soon! 🦅', 'success');
    if (window.loadBounties) {
        loadBounties();
    }
}

function showAlert(message, type = 'success') {
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type}`;
    alertDiv.textContent = message;
    alertDiv.style.position = 'fixed';
    alertDiv.style.top = '20px';
    alertDiv.style.right = '20px';
    alertDiv.style.zIndex = '1001';
    alertDiv.style.minWidth = '300px';
    document.body.appendChild(alertDiv);
    
    setTimeout(() => alertDiv.remove(), 4000);
}

document.addEventListener('DOMContentLoaded', () => {
    updateAuthUI();
    
    window.onclick = (event) => {
        if (event.target.classList.contains('modal')) {
            event.target.classList.remove('active');
        }
    };
});
