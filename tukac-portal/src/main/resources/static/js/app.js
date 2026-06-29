const $ = id => document.getElementById(id);
const $val = id => $(id).value;
const content = $('app-content');

window.addEventListener('hashchange', router);
window.addEventListener('load', router);

function navigate(path) { window.location.hash = path; }

function router() {
    renderNav();
    let h = window.location.hash || '#/';
    if(h==='#/') renderHome();
    else if(h==='#/login') renderLogin();
    else if(h==='#/register') renderRegister();
    else if(h==='#/dashboard') { if(!api.isAuth()) return navigate('#/login'); renderDashboard(); }
    else if(h==='#/events') renderEvents();
    else if(h==='#/blog') renderBlog();
    else if(h==='#/finance') { if(!api.isAdmin()) return navigate('#/dashboard'); renderFinance(); }
    else if(h==='#/members') { if(!api.isAdmin()) return navigate('#/dashboard'); renderMembers(); }
    else renderHome();
}

function renderNav() {
    let links = `<li class="nav-item"><a class="nav-link" href="#/"><i class="bi bi-house"></i> Home</a></li>
                 <li class="nav-item"><a class="nav-link" href="#/blog"><i class="bi bi-newspaper"></i> Blog</a></li>
                 <li class="nav-item"><a class="nav-link" href="#/events"><i class="bi bi-calendar"></i> Events</a></li>`;
    if(api.isAuth()) links += `<li class="nav-item"><a class="nav-link" href="#/dashboard"><i class="bi bi-speedometer2"></i> Dashboard</a></li>`;
    if(api.isAdmin()) {
        links += `<li class="nav-item"><a class="nav-link" href="#/members"><i class="bi bi-people"></i> Members</a></li>
                  <li class="nav-item"><a class="nav-link" href="#/finance"><i class="bi bi-cash"></i> Finance</a></li>`;
    }
    $('nav-links').innerHTML = links;

    $('nav-auth').innerHTML = api.isAuth() 
        ? `<li class="nav-item"><span class="nav-link text-white"><i class="bi bi-person"></i> ${api.getUser().name}</span></li>
           <li class="nav-item"><button class="btn btn-sm btn-outline-danger ms-2" onclick="api.clearAuth();navigate('#/');">Logout</button></li>`
        : `<li class="nav-item"><a class="nav-link" href="#/login">Login</a></li>
           <li class="nav-item"><a class="btn btn-primary-tukac ms-2" href="#/register">Join</a></li>`;
}

function renderHome() {
    content.innerHTML = `
    <div class="hero-section text-center py-5">
      <div class="container py-5">
        <h1 class="display-3 fw-bold mb-4">Technical University of Kenya <span class="gradient-text">Ability Club</span></h1>
        <p class="lead mb-5 mx-auto" style="max-width:700px">Empowering every student regardless of ability. Join us in building an inclusive campus.</p>
        <button class="btn btn-primary-tukac btn-lg me-3" onclick="navigate('#/register')">Join TUKAC</button>
        <button class="btn btn-outline-light btn-lg" onclick="navigate('#/events')">View Events</button>
      </div>
    </div>`;
}

function renderLogin() {
    content.innerHTML = `
    <div class="container py-5" style="max-width:400px">
        <div class="card shadow-sm p-4 border-0">
            <h3 class="fw-bold text-center mb-4">Login</h3>
            <div id="err" class="alert alert-danger d-none"></div>
            <form id="lf">
                <input type="email" id="le" class="form-control mb-3" placeholder="Email" required>
                <input type="password" id="lp" class="form-control mb-3" placeholder="Password" required>
                <button type="submit" class="btn btn-primary-tukac w-100">Sign In</button>
            </form>
        </div>
    </div>`;

    $('lf').addEventListener('submit', async (e) => {
        e.preventDefault();
        try {
            const data = await api.req('/auth/login', { method:'POST', body:{email:$val('le'),password:$val('lp')} });
            api.setToken(data.token); api.setUser(data.user); navigate('#/dashboard');
        } catch(err) { $('err').textContent = err.message; $('err').classList.remove('d-none'); }
    });
}

function renderRegister() {
    content.innerHTML = `
    <div class="container py-5" style="max-width:450px">
        <div class="card shadow-sm p-4 border-0">
            <h3 class="fw-bold text-center mb-4">Join TUKAC</h3>
            <div id="msg" class="alert d-none"></div>
            <form id="rf">
                <input type="text" id="rn" class="form-control mb-3" placeholder="Full Name" required>
                <input type="email" id="re" class="form-control mb-3" placeholder="Email" required>
                <input type="password" id="rp" class="form-control mb-3" placeholder="Password" required>
                <input type="password" id="rcp" class="form-control mb-3" placeholder="Confirm Password" required>
                <button type="submit" class="btn btn-primary-tukac w-100">Register</button>
            </form>
        </div>
    </div>`;

    $('rf').addEventListener('submit', async (e) => {
        e.preventDefault();
        try {
            const res = await api.req('/auth/register', { method:'POST', body:{name:$val('rn'),email:$val('re'),password:$val('rp'),confirmPassword:$val('rcp')} });
            $('msg').className='alert alert-success'; $('msg').textContent=res.message; $('rf').reset();
        } catch(err) { $('msg').className='alert alert-danger'; $('msg').textContent=err.message; }
    });
}

async function renderDashboard() {
    content.innerHTML = `<div class="text-center py-5">Loading...</div>`;
    try {
        const d = await api.req('/dashboard/stats');
        content.innerHTML = `
        <div class="container py-5">
            <h2 class="fw-bold mb-4">Dashboard</h2>
            <div class="row g-4">
                <div class="col-md-3"><div class="card p-4 shadow-sm text-center"><h2>${d.totalMembers}</h2><span class="text-muted">Members</span></div></div>
                <div class="col-md-3"><div class="card p-4 shadow-sm text-center"><h2>${d.totalEvents}</h2><span class="text-muted">Events</span></div></div>
                <div class="col-md-3"><div class="card p-4 shadow-sm text-center"><h2>${d.totalPosts}</h2><span class="text-muted">Posts</span></div></div>
                <div class="col-md-3"><div class="card p-4 shadow-sm text-center"><h2>KES ${d.balance.toFixed(2)}</h2><span class="text-muted">Balance</span></div></div>
            </div>
        </div>`;
    } catch(err) { content.innerHTML = `<div class="container py-5 text-danger">Error: ${err.message}</div>`; }
}

async function renderEvents() {
    content.innerHTML = `<div class="text-center py-5">Loading...</div>`;
    try {
        const d = await api.req('/events/public');
        let html = `<div class="container py-5"><h2 class="fw-bold mb-4">Events</h2><div class="row g-4">`;
        d.upcomingEvents.forEach(e => {
            html += `<div class="col-md-4"><div class="card shadow-sm p-3 h-100 border-0">
                <h5 class="fw-bold">${e.title}</h5>
                <p class="small text-muted">${e.date} @ ${e.time}<br>${e.location}</p>
                <button class="btn btn-sm btn-outline-primary mt-auto" onclick="regEvent(${e.id})">Register</button>
            </div></div>`;
        });
        content.innerHTML = html + `</div></div>`;
    } catch(err) { content.innerHTML = `<div class="container py-5 text-danger">Error: ${err.message}</div>`; }
}
window.regEvent = async (id) => {
    if(!api.isAuth()) return navigate('#/login');
    try { const res = await api.req(`/events/${id}/register`, {method:'POST'}); alert(res.message); }
    catch(err) { alert(err.message); }
};

async function renderBlog() {
    content.innerHTML = `<div class="text-center py-5">Loading...</div>`;
    try {
        const posts = await api.req('/blog/public');
        let html = `<div class="container py-5"><h2 class="fw-bold mb-4">Blog</h2><div class="row g-4">`;
        posts.forEach(p => {
            html += `<div class="col-md-4"><div class="card shadow-sm p-3 h-100 border-0">
                <span class="badge bg-primary mb-2 align-self-start">${p.category}</span>
                <h5 class="fw-bold">${p.title}</h5>
                <p class="small text-muted mt-auto mb-0">By: ${p.author?p.author.name:'Unknown'}</p>
            </div></div>`;
        });
        content.innerHTML = html + `</div></div>`;
    } catch(err) { content.innerHTML = `<div class="container py-5 text-danger">Error: ${err.message}</div>`; }
}

async function renderMembers() {
    content.innerHTML = `<div class="text-center py-5">Loading...</div>`;
    try {
        const d = await api.req('/members');
        let html = `<div class="container py-5"><h2 class="fw-bold mb-4">Members</h2><table class="table">
        <thead><tr><th>Name</th><th>Email</th><th>Role</th><th>Status</th><th>Action</th></tr></thead><tbody>`;
        d.members.forEach(m => {
            html += `<tr><td>${m.name}</td><td>${m.email}</td><td>${m.role}</td><td><span class="badge bg-secondary">${m.approvalStatus}</span></td>
            <td>${m.approvalStatus==='PENDING'?`<button class="btn btn-sm btn-success" onclick="apprMem(${m.id})">Approve</button>`:''}</td></tr>`;
        });
        content.innerHTML = html + `</tbody></table></div>`;
    } catch(err) { content.innerHTML = `<div class="container py-5 text-danger">Error: ${err.message}</div>`; }
}
window.apprMem = async(id) => { try { await api.req(`/members/${id}/approve`, {method:'POST'}); renderMembers(); } catch(e) { alert(e.message); } };

async function renderFinance() {
    content.innerHTML = `<div class="text-center py-5">Loading...</div>`;
    try {
        const d = await api.req('/finance');
        let html = `<div class="container py-5"><h2 class="fw-bold mb-4">Finance</h2><table class="table">
        <thead><tr><th>Date</th><th>Description</th><th>Type</th><th>Amount</th></tr></thead><tbody>`;
        d.transactions.forEach(t => {
            html += `<tr><td>${t.date}</td><td>${t.description}</td><td>${t.type}</td>
            <td class="${t.type==='INCOME'?'text-success':'text-danger'} fw-bold">${t.amount.toFixed(2)}</td></tr>`;
        });
        content.innerHTML = html + `</tbody></table></div>`;
    } catch(err) { content.innerHTML = `<div class="container py-5 text-danger">Error: ${err.message}</div>`; }
}
