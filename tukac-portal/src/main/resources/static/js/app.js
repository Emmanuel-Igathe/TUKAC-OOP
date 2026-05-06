// TUK Ability Club Portal - Main Application JS

let currentUser = null;
let currentPage = 'home';

// Initialize application on load
document.addEventListener('DOMContentLoaded', function () {
    checkAuthStatus();
    navigateTo('home');
});

// Navigation function
async function navigateTo(page) {
    currentPage = page;
    const contentArea = document.getElementById('contentArea');

    // Update active nav link
    const navLinks = document.querySelectorAll('#mainNav .nav-link');
    navLinks.forEach(link => link.classList.remove('active'));
    const activeLink = Array.from(navLinks).find(link =>
        link.textContent.toLowerCase().includes(page.toLowerCase())
    );
    if (activeLink) activeLink.classList.add('active');

    try {
        switch (page) {
            case 'home':
                contentArea.innerHTML = loadHome();
                break;
            case 'events':
                contentArea.innerHTML = loadEvents();
                break;
            case 'blog':
                contentArea.innerHTML = loadBlog();
                break;
            case 'about':
                contentArea.innerHTML = loadAbout();
                break;
            case 'contact':
                contentArea.innerHTML = loadContact();
                break;
            case 'login':
                contentArea.innerHTML = loadLogin();
                break;
            case 'register':
                contentArea.innerHTML = loadRegister();
                break;
            case 'dashboard':
                contentArea.innerHTML = await loadDashboard();
                break;
            case 'profile':
                contentArea.innerHTML = await loadProfile();
                break;
            default:
                contentArea.innerHTML = loadHome();
        }
        window.scrollTo(0, 0);
    } catch (error) {
        console.error('Error navigating to', page, error);
        contentArea.innerHTML = '<div class="alert alert-danger">Error loading page</div>';
    }
}

// Check authentication status
async function checkAuthStatus() {
    try {
        const response = await fetch('/api/auth/check');
        if (response.ok) {
            currentUser = await response.json();
            updateUIForLoggedIn();
        } else {
            updateUIForLoggedOut();
        }
    } catch (error) {
        console.log('User not authenticated');
        updateUIForLoggedOut();
    }
}

// Update UI for logged-in user
function updateUIForLoggedIn() {
    const authButtons = document.getElementById('authButtons');
    const mainNav = document.getElementById('mainNav');

    // Add member nav items
    const memberItems = `
        <li class="nav-item">
            <a class="nav-link" href="#" onclick="navigateTo('dashboard')">Dashboard</a>
        </li>
        <li class="nav-item">
            <a class="nav-link" href="#" onclick="navigateTo('profile')">Profile</a>
        </li>
    `;

    // Replace auth buttons with user menu
    authButtons.innerHTML = `
        <div class="dropdown">
            <button class="btn btn-outline-light btn-sm dropdown-toggle" type="button" 
                    data-bs-toggle="dropdown" aria-expanded="false">
                <i class="fas fa-user"></i> ${currentUser.name}
            </button>
            <ul class="dropdown-menu dropdown-menu-end">
                <li><a class="dropdown-item" href="#" onclick="navigateTo('profile')">My Profile</a></li>
                <li><hr class="dropdown-divider"></li>
                <li><a class="dropdown-item" href="#" onclick="logout()">Sign Out</a></li>
            </ul>
        </div>
    `;
}

// Update UI for logged-out user
function updateUIForLoggedIn() {
    const authButtons = document.getElementById('authButtons');
    authButtons.innerHTML = `
        <button class="btn btn-outline-light btn-sm" onclick="navigateTo('login')">Sign In</button>
        <button class="btn btn-warning btn-sm" onclick="navigateTo('register')">Register</button>
    `;
}

function updateUIForLoggedOut() {
    const authButtons = document.getElementById('authButtons');
    authButtons.innerHTML = `
        <button class="btn btn-outline-light btn-sm" onclick="navigateTo('login')">Sign In</button>
        <button class="btn btn-warning btn-sm" onclick="navigateTo('register')">Register</button>
    `;
}

// PAGE CONTENT GENERATORS

function loadHome() {
    return `
        <div class="container">
            <div class="hero">
                <h1><i class="fas fa-heart"></i> Welcome to TUK Ability Club</h1>
                <p>Making inclusion possible through community engagement and meaningful connections</p>
                ${!currentUser ? `<button class="btn btn-light btn-lg" onclick="navigateTo('register')">Join Us Today</button>` : ''}
            </div>

            <div class="row">
                <div class="col-md-4 mb-4">
                    <div class="card">
                        <div class="card-body text-center">
                            <i class="fas fa-users fa-3x text-primary mb-3"></i>
                            <h5 class="card-title">Community</h5>
                            <p class="card-text">Connect with members and build meaningful relationships</p>
                        </div>
                    </div>
                </div>
                <div class="col-md-4 mb-4">
                    <div class="card">
                        <div class="card-body text-center">
                            <i class="fas fa-calendar fa-3x text-primary mb-3"></i>
                            <h5 class="card-title">Events</h5>
                            <p class="card-text">Participate in engaging activities and events</p>
                        </div>
                    </div>
                </div>
                <div class="col-md-4 mb-4">
                    <div class="card">
                        <div class="card-body text-center">
                            <i class="fas fa-share-alt fa-3x text-primary mb-3"></i>
                            <h5 class="card-title">Share</h5>
                            <p class="card-text">Share your stories and experiences with the club</p>
                        </div>
                    </div>
                </div>
            </div>

            <section class="panel mt-5">
                <h3 class="panel-title">About Our Club</h3>
                <p>The TUK Ability Club is committed to creating an inclusive environment where all members feel valued and supported. We organize events, workshops, and social gatherings to promote engagement and community building.</p>
                <a href="#" onclick="navigateTo('about')" class="btn btn-primary">Learn More</a>
            </section>
        </div>
    `;
}

function loadEvents() {
    return `
        <div class="container">
            <div class="panel">
                <h2 class="panel-title"><i class="fas fa-calendar-alt"></i> Events</h2>
                <div class="row" id="eventsList">
                    <div class="col-12">
                        <div class="alert alert-info">
                            <i class="fas fa-spinner fa-spin"></i> Loading events...
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `;
}

function loadBlog() {
    return `
        <div class="container">
            <div class="panel">
                <h2 class="panel-title"><i class="fas fa-blog"></i> Blog</h2>
                <div class="row" id="blogList">
                    <div class="col-12">
                        <div class="alert alert-info">
                            <i class="fas fa-spinner fa-spin"></i> Loading blog posts...
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `;
}

function loadAbout() {
    return `
        <div class="container">
            <div class="panel">
                <h2 class="panel-title"><i class="fas fa-info-circle"></i> About Us</h2>
                <p>The TUK Ability Club was founded with the mission to create an inclusive and supportive community for all members. We believe in the power of connection and mutual support.</p>
                
                <h4 class="mt-4">Our Mission</h4>
                <p>To empower individuals through community engagement, peer support, and inclusive programming.</p>
                
                <h4 class="mt-4">Our Values</h4>
                <ul>
                    <li><strong>Inclusion:</strong> Everyone belongs here</li>
                    <li><strong>Support:</strong> We help each other grow</li>
                    <li><strong>Respect:</strong> Diversity makes us stronger</li>
                    <li><strong>Connection:</strong> Together we thrive</li>
                </ul>
            </div>
        </div>
    `;
}

function loadContact() {
    return `
        <div class="container">
            <div class="panel">
                <h2 class="panel-title"><i class="fas fa-envelope"></i> Contact Us</h2>
                <form id="contactForm">
                    <div class="mb-3">
                        <label for="contactName" class="form-label">Name</label>
                        <input type="text" class="form-control" id="contactName" required>
                    </div>
                    <div class="mb-3">
                        <label for="contactEmail" class="form-label">Email</label>
                        <input type="email" class="form-control" id="contactEmail" required>
                    </div>
                    <div class="mb-3">
                        <label for="contactMessage" class="form-label">Message</label>
                        <textarea class="form-control" id="contactMessage" rows="5" required></textarea>
                    </div>
                    <button type="submit" class="btn btn-primary">Send Message</button>
                </form>
            </div>
        </div>
    `;
}

function loadLogin() {
    return `
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-md-6">
                    <div class="panel">
                        <h2 class="panel-title text-center"><i class="fas fa-sign-in-alt"></i> Sign In</h2>
                        <form id="loginForm" onsubmit="handleLogin(event)">
                            <div class="mb-3">
                                <label for="loginEmail" class="form-label">Email Address</label>
                                <input type="email" class="form-control" id="loginEmail" required>
                            </div>
                            <div class="mb-3">
                                <label for="loginPassword" class="form-label">Password</label>
                                <input type="password" class="form-control" id="loginPassword" required>
                            </div>
                            <button type="submit" class="btn btn-primary w-100">Sign In</button>
                            <div class="text-center mt-3">
                                <p>Don't have an account? <a href="#" onclick="navigateTo('register')">Register here</a></p>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    `;
}

function loadRegister() {
    return `
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-md-6">
                    <div class="panel">
                        <h2 class="panel-title text-center"><i class="fas fa-user-plus"></i> Register</h2>
                        <form id="registerForm" onsubmit="handleRegister(event)">
                            <div class="mb-3">
                                <label for="regName" class="form-label">Full Name</label>
                                <input type="text" class="form-control" id="regName" required>
                            </div>
                            <div class="mb-3">
                                <label for="regEmail" class="form-label">Email Address</label>
                                <input type="email" class="form-control" id="regEmail" required>
                            </div>
                            <div class="mb-3">
                                <label for="regPassword" class="form-label">Password</label>
                                <input type="password" class="form-control" id="regPassword" required>
                            </div>
                            <div class="mb-3">
                                <label for="regPhone" class="form-label">Phone Number</label>
                                <input type="tel" class="form-control" id="regPhone">
                            </div>
                            <button type="submit" class="btn btn-primary w-100">Create Account</button>
                            <div class="text-center mt-3">
                                <p>Already have an account? <a href="#" onclick="navigateTo('login')">Sign in here</a></p>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    `;
}

async function loadDashboard() {
    return `
        <div class="container">
            <div class="panel">
                <h2 class="panel-title"><i class="fas fa-chart-line"></i> Dashboard</h2>
                <div class="row">
                    <div class="col-md-3 mb-4">
                        <div class="card text-center">
                            <div class="card-body">
                                <h5 class="card-title">My Events</h5>
                                <h2 class="text-primary">0</h2>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-3 mb-4">
                        <div class="card text-center">
                            <div class="card-body">
                                <h5 class="card-title">RSVPs</h5>
                                <h2 class="text-primary">0</h2>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-3 mb-4">
                        <div class="card text-center">
                            <div class="card-body">
                                <h5 class="card-title">Friends</h5>
                                <h2 class="text-primary">0</h2>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-3 mb-4">
                        <div class="card text-center">
                            <div class="card-body">
                                <h5 class="card-title">Posts</h5>
                                <h2 class="text-primary">0</h2>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `;
}

async function loadProfile() {
    if (!currentUser) {
        navigateTo('login');
        return;
    }

    return `
        <div class="container">
            <div class="panel">
                <h2 class="panel-title"><i class="fas fa-user"></i> My Profile</h2>
                <div class="row">
                    <div class="col-md-4 text-center mb-4">
                        <div class="mb-3">
                            <i class="fas fa-user-circle fa-5x text-primary"></i>
                        </div>
                        <h3>${currentUser.name}</h3>
                        <p class="text-muted">${currentUser.email}</p>
                    </div>
                    <div class="col-md-8">
                        <form>
                            <div class="mb-3">
                                <label for="profileName" class="form-label">Full Name</label>
                                <input type="text" class="form-control" id="profileName" value="${currentUser.name}" disabled>
                            </div>
                            <div class="mb-3">
                                <label for="profileEmail" class="form-label">Email</label>
                                <input type="email" class="form-control" id="profileEmail" value="${currentUser.email}" disabled>
                            </div>
                            <div class="mb-3">
                                <label for="profilePhone" class="form-label">Phone</label>
                                <input type="tel" class="form-control" id="profilePhone" placeholder="Enter your phone number">
                            </div>
                            <button type="button" class="btn btn-primary">Update Profile</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    `;
}

// FORM HANDLERS

async function handleLogin(event) {
    event.preventDefault();

    const email = document.getElementById('loginEmail').value;
    const password = document.getElementById('loginPassword').value;

    try {
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });

        if (response.ok) {
            currentUser = await response.json();
            updateUIForLoggedIn();
            navigateTo('dashboard');
            showAlert('Welcome back!', 'success');
        } else {
            showAlert('Invalid email or password', 'danger');
        }
    } catch (error) {
        console.error('Login error:', error);
        showAlert('Login failed', 'danger');
    }
}

async function handleRegister(event) {
    event.preventDefault();

    const name = document.getElementById('regName').value;
    const email = document.getElementById('regEmail').value;
    const password = document.getElementById('regPassword').value;
    const phone = document.getElementById('regPhone').value;

    try {
        const response = await fetch('/api/auth/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, email, password, phone })
        });

        if (response.ok) {
            showAlert('Registration successful! Please sign in.', 'success');
            navigateTo('login');
        } else {
            showAlert('Registration failed', 'danger');
        }
    } catch (error) {
        console.error('Registration error:', error);
        showAlert('Registration failed', 'danger');
    }
}

async function logout() {
    try {
        await fetch('/api/auth/logout', { method: 'POST' });
        currentUser = null;
        updateUIForLoggedOut();
        navigateTo('home');
        showAlert('You have been signed out', 'info');
    } catch (error) {
        console.error('Logout error:', error);
    }
}

// UTILITY FUNCTIONS

function showAlert(message, type = 'info') {
    const contentArea = document.getElementById('contentArea');
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} alert-dismissible fade show`;
    alertDiv.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    contentArea.insertBefore(alertDiv, contentArea.firstChild);

    // Auto-dismiss after 5 seconds
    setTimeout(() => alertDiv.remove(), 5000);
}

// Load API data functions (to be implemented with backend)
async function loadEventsData() {
    try {
        const response = await fetch('/api/events');
        if (response.ok) {
            return await response.json();
        }
    } catch (error) {
        console.error('Error loading events:', error);
    }
    return [];
}

async function loadBlogData() {
    try {
        const response = await fetch('/api/blog');
        if (response.ok) {
            return await response.json();
        }
    } catch (error) {
        console.error('Error loading blog posts:', error);
    }
    return [];
}
