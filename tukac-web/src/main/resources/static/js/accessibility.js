// ============================================================
// accessibility.js — TUKAC Accessibility Toolkit
// Contrast modes, font controls, and voice helper
// ============================================================

(function () {
  'use strict';

  const STORAGE_KEY = 'tukac_a11y';
  const defaults = {
    contrast: 'normal',    // normal | high | yellow-black | inverted
    fontSize: 100,         // 80–200 percent
    fontWeight: 'normal',  // normal | semibold | bold | extrabold
    voiceSpeed: 1,         // 0.5 – 2
    readOnHover: false,
  };

  // ── Load / Save ──────────────────────────────────────────
  function loadPrefs() {
    try { return { ...defaults, ...JSON.parse(localStorage.getItem(STORAGE_KEY)) }; }
    catch { return { ...defaults }; }
  }
  function savePrefs(p) { localStorage.setItem(STORAGE_KEY, JSON.stringify(p)); }
  let prefs = loadPrefs();

  // ── Inject CSS ───────────────────────────────────────────
  const style = document.createElement('style');
  style.id = 'a11y-styles';
  style.textContent = `
    /* ── Floating Toggle ── */
    .a11y-fab {
      position: fixed; bottom: 24px; right: 24px; z-index: 10000;
      width: 56px; height: 56px; border-radius: 50%;
      background: linear-gradient(135deg, #003366, #1a6b3c);
      border: 3px solid #c8921a; color: #fff;
      font-size: 26px; cursor: pointer;
      box-shadow: 0 6px 24px rgba(0,0,0,0.3);
      display: flex; align-items: center; justify-content: center;
      transition: all 0.3s ease;
      animation: a11yPulse 3s ease-in-out infinite;
    }
    .a11y-fab:hover { transform: scale(1.12); box-shadow: 0 8px 32px rgba(0,0,0,0.4); }
    .a11y-fab.active { background: #c8921a; animation: none; }
    @keyframes a11yPulse {
      0%, 100% { box-shadow: 0 6px 24px rgba(0,0,0,0.3); }
      50% { box-shadow: 0 6px 24px rgba(200,146,26,0.5); }
    }

    /* ── Panel ── */
    .a11y-panel {
      position: fixed; bottom: 92px; right: 24px; z-index: 10000;
      width: 340px; max-height: calc(100vh - 120px);
      background: #fff; border: 1px solid #dde1e6;
      border-radius: 16px; box-shadow: 0 12px 48px rgba(0,0,0,0.18);
      display: none; flex-direction: column; overflow: hidden;
      animation: a11ySlideIn 0.25s ease;
    }
    .a11y-panel.open { display: flex; }
    @keyframes a11ySlideIn { from { opacity:0; transform: translateY(16px) scale(0.96); } to { opacity:1; transform: translateY(0) scale(1); } }

    .a11y-panel-header {
      padding: 16px 20px; background: linear-gradient(135deg, #003366, #1a6b3c);
      color: #fff; display: flex; align-items: center; justify-content: space-between;
    }
    .a11y-panel-header h3 { font-size: 15px; font-weight: 700; margin: 0; display: flex; align-items: center; gap: 8px; }
    .a11y-panel-close {
      background: rgba(255,255,255,0.15); border: none; color: #fff;
      width: 30px; height: 30px; border-radius: 50%; cursor: pointer;
      font-size: 16px; display: flex; align-items: center; justify-content: center;
      transition: all 0.2s;
    }
    .a11y-panel-close:hover { background: rgba(255,255,255,0.3); }

    .a11y-panel-body { padding: 16px 20px; overflow-y: auto; flex: 1; }
    .a11y-section { margin-bottom: 20px; }
    .a11y-section:last-child { margin-bottom: 4px; }
    .a11y-section-title {
      font-size: 11px; font-weight: 700; text-transform: uppercase;
      letter-spacing: 0.08em; color: #6c757d; margin-bottom: 10px;
      display: flex; align-items: center; gap: 6px;
    }

    /* Button group */
    .a11y-btn-group { display: flex; gap: 6px; flex-wrap: wrap; }
    .a11y-btn {
      flex: 1; min-width: 0; padding: 8px 6px; border: 1.5px solid #dde1e6;
      border-radius: 8px; background: #fff; cursor: pointer;
      font-size: 12px; font-weight: 600; color: #495057;
      transition: all 0.2s; text-align: center; white-space: nowrap;
    }
    .a11y-btn:hover { border-color: #003366; color: #003366; background: rgba(0,51,102,0.04); }
    .a11y-btn.active { background: #003366; color: #fff; border-color: #003366; }

    /* Slider row */
    .a11y-slider-row {
      display: flex; align-items: center; gap: 10px;
    }
    .a11y-slider-row label { font-size: 13px; font-weight: 600; color: #495057; white-space: nowrap; }
    .a11y-slider-row input[type=range] {
      flex: 1; accent-color: #003366; height: 6px; cursor: pointer;
    }
    .a11y-slider-row .a11y-val {
      font-size: 12px; font-weight: 700; color: #003366;
      min-width: 40px; text-align: right;
    }

    /* Voice section */
    .a11y-voice-controls { display: flex; gap: 8px; flex-wrap: wrap; }
    .a11y-voice-btn {
      flex: 1; min-width: 80px; padding: 10px 8px; border: 1.5px solid #dde1e6;
      border-radius: 10px; background: #fff; cursor: pointer;
      font-size: 12px; font-weight: 600; color: #495057;
      transition: all 0.2s; text-align: center;
      display: flex; flex-direction: column; align-items: center; gap: 4px;
    }
    .a11y-voice-btn .voice-icon { font-size: 22px; }
    .a11y-voice-btn:hover { border-color: #1a6b3c; color: #1a6b3c; background: rgba(26,107,60,0.04); }
    .a11y-voice-btn.speaking { background: #1a6b3c; color: #fff; border-color: #1a6b3c; animation: a11ySpeakPulse 1.5s ease-in-out infinite; }
    @keyframes a11ySpeakPulse { 0%,100% { box-shadow: none; } 50% { box-shadow: 0 0 12px rgba(26,107,60,0.4); } }
    .a11y-voice-btn.stop-btn:hover { border-color: #dc3545; color: #dc3545; }

    .a11y-hover-toggle {
      display: flex; align-items: center; gap: 10px; margin-top: 10px;
      font-size: 12px; font-weight: 600; color: #495057; cursor: pointer;
    }
    .a11y-hover-toggle input[type=checkbox] {
      width: 18px; height: 18px; accent-color: #003366; cursor: pointer;
    }

    .a11y-reset {
      width: 100%; padding: 10px; border: 1.5px dashed #dde1e6;
      border-radius: 10px; background: transparent; cursor: pointer;
      font-size: 12px; font-weight: 600; color: #6c757d;
      transition: all 0.2s; text-align: center;
    }
    .a11y-reset:hover { border-color: #dc3545; color: #dc3545; background: rgba(220,53,69,0.04); }

    /* ── Contrast Mode Overrides ── */
    html.a11y-high-contrast {
      --bg-page: #000 !important; --bg-card: #111 !important;
      --border: #888 !important; --text-primary: #fff !important;
      --text-secondary: #eee !important; --text-muted: #ccc !important;
      --navy: #4da6ff !important; --navy-dark: #000 !important;
      --green: #5fdd5f !important; --gold: #ffd700 !important;
      --shadow: none !important;
    }
    html.a11y-high-contrast body { background: #000 !important; color: #fff !important; }
    html.a11y-high-contrast .sidebar { background: #111 !important; border-color: #555 !important; }
    html.a11y-high-contrast .topbar { background: #111 !important; border-color: #555 !important; color: #fff !important; }
    html.a11y-high-contrast .nav-item { color: #ddd !important; }
    html.a11y-high-contrast .nav-item.active,
    html.a11y-high-contrast .nav-item:hover { background: #333 !important; color: #fff !important; }
    html.a11y-high-contrast .main-content,
    html.a11y-high-contrast .main-wrapper { background: #000 !important; }
    html.a11y-high-contrast .stat-card,
    html.a11y-high-contrast .event-card,
    html.a11y-high-contrast .forum-card,
    html.a11y-high-contrast .contact-card,
    html.a11y-high-contrast .modal,
    html.a11y-high-contrast .card,
    html.a11y-high-contrast table,
    html.a11y-high-contrast .form-control { background: #1a1a1a !important; color: #fff !important; border-color: #666 !important; }
    html.a11y-high-contrast a { color: #6db3f8 !important; }
    html.a11y-high-contrast .btn { border-width: 2px !important; }
    html.a11y-high-contrast h1, html.a11y-high-contrast h2, html.a11y-high-contrast h3,
    html.a11y-high-contrast h4, html.a11y-high-contrast .topbar-title,
    html.a11y-high-contrast .section-title, html.a11y-high-contrast .stat-value,
    html.a11y-high-contrast .forum-title, html.a11y-high-contrast .brand-text { color: #fff !important; }
    html.a11y-high-contrast .topbar-subtitle, html.a11y-high-contrast .stat-label,
    html.a11y-high-contrast .forum-meta, html.a11y-high-contrast .brand-subtitle,
    html.a11y-high-contrast .user-role, html.a11y-high-contrast .nav-section-label { color: #aaa !important; }

    html.a11y-yellow-black {
      --bg-page: #000 !important; --bg-card: #111 !important;
      --border: #aa0 !important; --text-primary: #ff0 !important;
      --text-secondary: #ff0 !important; --text-muted: #cc0 !important;
    }
    html.a11y-yellow-black body { background: #000 !important; color: #ff0 !important; }
    html.a11y-yellow-black * { color: #ff0 !important; border-color: #660 !important; }
    html.a11y-yellow-black .sidebar, html.a11y-yellow-black .topbar,
    html.a11y-yellow-black .main-wrapper, html.a11y-yellow-black .main-content { background: #000 !important; }
    html.a11y-yellow-black .stat-card, html.a11y-yellow-black .event-card,
    html.a11y-yellow-black .forum-card, html.a11y-yellow-black .contact-card,
    html.a11y-yellow-black .modal, html.a11y-yellow-black .form-control,
    html.a11y-yellow-black table { background: #111 !important; border-color: #660 !important; }
    html.a11y-yellow-black .nav-item.active, html.a11y-yellow-black .nav-item:hover { background: #330 !important; }
    html.a11y-yellow-black a { color: #ff0 !important; text-decoration: underline !important; }
    html.a11y-yellow-black .btn { background: #330 !important; color: #ff0 !important; border: 2px solid #ff0 !important; }
    html.a11y-yellow-black .btn-primary { background: #660 !important; }
    html.a11y-yellow-black img, html.a11y-yellow-black svg { filter: grayscale(1) brightness(2); }

    html.a11y-inverted { filter: invert(1) hue-rotate(180deg); }
    html.a11y-inverted img, html.a11y-inverted video, html.a11y-inverted svg,
    html.a11y-inverted .a11y-fab, html.a11y-inverted .a11y-panel { filter: invert(1) hue-rotate(180deg); }

    /* ── Font Weight Overrides ── */
    html.a11y-weight-semibold body { font-weight: 500 !important; }
    html.a11y-weight-semibold p, html.a11y-weight-semibold span,
    html.a11y-weight-semibold div, html.a11y-weight-semibold a,
    html.a11y-weight-semibold td, html.a11y-weight-semibold li,
    html.a11y-weight-semibold label, html.a11y-weight-semibold input,
    html.a11y-weight-semibold textarea { font-weight: 500 !important; }

    html.a11y-weight-bold body { font-weight: 700 !important; }
    html.a11y-weight-bold p, html.a11y-weight-bold span,
    html.a11y-weight-bold div, html.a11y-weight-bold a,
    html.a11y-weight-bold td, html.a11y-weight-bold li,
    html.a11y-weight-bold label, html.a11y-weight-bold input,
    html.a11y-weight-bold textarea { font-weight: 700 !important; }

    html.a11y-weight-extrabold body { font-weight: 900 !important; }
    html.a11y-weight-extrabold * { font-weight: 900 !important; }

    /* ── Voice highlight ── */
    .a11y-reading-highlight {
      outline: 3px solid #c8921a !important;
      outline-offset: 2px;
      background: rgba(200,146,26,0.08) !important;
      transition: outline 0.2s, background 0.2s;
    }

    /* ── Screen reader hint on hover ── */
    .a11y-hover-active [data-a11y-readable]:hover {
      outline: 2px dashed rgba(0,51,102,0.3);
      outline-offset: 2px;
    }
  `;
  document.head.appendChild(style);

  // ── Build Panel HTML ─────────────────────────────────────
  function buildPanel() {
    // FAB button
    const fab = document.createElement('button');
    fab.className = 'a11y-fab';
    fab.id = 'a11yFab';
    fab.innerHTML = '♿';
    fab.title = 'Accessibility Settings';
    fab.setAttribute('aria-label', 'Open accessibility settings');
    fab.onclick = togglePanel;
    document.body.appendChild(fab);

    // Panel
    const panel = document.createElement('div');
    panel.className = 'a11y-panel';
    panel.id = 'a11yPanel';
    panel.setAttribute('role', 'dialog');
    panel.setAttribute('aria-label', 'Accessibility Settings');
    panel.innerHTML = `
      <div class="a11y-panel-header">
        <h3>♿ Accessibility</h3>
        <button class="a11y-panel-close" onclick="window._a11y.togglePanel()" aria-label="Close">✕</button>
      </div>
      <div class="a11y-panel-body">

        <!-- Contrast -->
        <div class="a11y-section">
          <div class="a11y-section-title">🎨 Contrast Mode</div>
          <div class="a11y-btn-group" id="a11yContrastGroup">
            <button class="a11y-btn" data-contrast="normal">Normal</button>
            <button class="a11y-btn" data-contrast="high">High</button>
            <button class="a11y-btn" data-contrast="yellow-black">Yellow</button>
            <button class="a11y-btn" data-contrast="inverted">Invert</button>
          </div>
        </div>

        <!-- Font Size -->
        <div class="a11y-section">
          <div class="a11y-section-title">🔤 Font Size</div>
          <div class="a11y-slider-row">
            <label>A<small style="font-size:9px">a</small></label>
            <input type="range" id="a11yFontSize" min="80" max="200" step="10" value="100" />
            <span class="a11y-val" id="a11yFontSizeVal">100%</span>
            <label style="font-size:18px;">A</label>
          </div>
        </div>

        <!-- Font Weight -->
        <div class="a11y-section">
          <div class="a11y-section-title">🅱️ Font Weight</div>
          <div class="a11y-btn-group" id="a11yWeightGroup">
            <button class="a11y-btn" data-weight="normal" style="font-weight:400">Normal</button>
            <button class="a11y-btn" data-weight="semibold" style="font-weight:500">Semi</button>
            <button class="a11y-btn" data-weight="bold" style="font-weight:700">Bold</button>
            <button class="a11y-btn" data-weight="extrabold" style="font-weight:900">Extra</button>
          </div>
        </div>

        <!-- Voice Helper -->
        <div class="a11y-section">
          <div class="a11y-section-title">🔊 Voice Helper</div>
          <div class="a11y-voice-controls">
            <button class="a11y-voice-btn" id="a11yReadPage" onclick="window._a11y.readPage()">
              <span class="voice-icon">▶️</span>
              Read Page
            </button>
            <button class="a11y-voice-btn stop-btn" id="a11yStopRead" onclick="window._a11y.stopReading()">
              <span class="voice-icon">⏹️</span>
              Stop
            </button>
          </div>
          <div class="a11y-slider-row" style="margin-top:12px;">
            <label>🐢</label>
            <input type="range" id="a11yVoiceSpeed" min="0.5" max="2" step="0.25" value="1" />
            <span class="a11y-val" id="a11yVoiceSpeedVal">1×</span>
            <label>🐇</label>
          </div>
          <label class="a11y-hover-toggle">
            <input type="checkbox" id="a11yReadHover" />
            Read text when I hover over it
          </label>
        </div>

        <!-- Reset -->
        <button class="a11y-reset" onclick="window._a11y.resetAll()">↩ Reset to Defaults</button>
      </div>
    `;
    document.body.appendChild(panel);

    // Wire up events
    panel.querySelectorAll('#a11yContrastGroup .a11y-btn').forEach(btn => {
      btn.onclick = () => setContrast(btn.dataset.contrast);
    });
    panel.querySelectorAll('#a11yWeightGroup .a11y-btn').forEach(btn => {
      btn.onclick = () => setFontWeight(btn.dataset.weight);
    });
    document.getElementById('a11yFontSize').oninput = function () {
      setFontSize(parseInt(this.value));
    };
    document.getElementById('a11yVoiceSpeed').oninput = function () {
      prefs.voiceSpeed = parseFloat(this.value);
      document.getElementById('a11yVoiceSpeedVal').textContent = this.value + '×';
      savePrefs(prefs);
    };
    document.getElementById('a11yReadHover').onchange = function () {
      prefs.readOnHover = this.checked;
      savePrefs(prefs);
      applyHoverRead();
    };

    // Close on Escape
    document.addEventListener('keydown', e => {
      if (e.key === 'Escape' && panel.classList.contains('open')) togglePanel();
    });
  }

  // ── Toggle Panel ─────────────────────────────────────────
  function togglePanel() {
    const panel = document.getElementById('a11yPanel');
    const fab = document.getElementById('a11yFab');
    panel.classList.toggle('open');
    fab.classList.toggle('active');

    // Announce for screen readers
    if (panel.classList.contains('open') && 'speechSynthesis' in window) {
      const u = new SpeechSynthesisUtterance('Accessibility settings opened');
      u.volume = 0.3; u.rate = 1.5;
      speechSynthesis.speak(u);
    }
  }

  // ── Contrast ─────────────────────────────────────────────
  function setContrast(mode) {
    const html = document.documentElement;
    html.classList.remove('a11y-high-contrast', 'a11y-yellow-black', 'a11y-inverted');
    if (mode !== 'normal') html.classList.add('a11y-' + mode);
    prefs.contrast = mode;
    savePrefs(prefs);
    updateContrastButtons();
  }
  function updateContrastButtons() {
    document.querySelectorAll('#a11yContrastGroup .a11y-btn').forEach(btn => {
      btn.classList.toggle('active', btn.dataset.contrast === prefs.contrast);
    });
  }

  // ── Font Size ────────────────────────────────────────────
  function setFontSize(size) {
    document.documentElement.style.fontSize = size + '%';
    prefs.fontSize = size;
    savePrefs(prefs);
    const valEl = document.getElementById('a11yFontSizeVal');
    const slider = document.getElementById('a11yFontSize');
    if (valEl) valEl.textContent = size + '%';
    if (slider) slider.value = size;
  }

  // ── Font Weight ──────────────────────────────────────────
  function setFontWeight(weight) {
    const html = document.documentElement;
    html.classList.remove('a11y-weight-semibold', 'a11y-weight-bold', 'a11y-weight-extrabold');
    if (weight !== 'normal') html.classList.add('a11y-weight-' + weight);
    prefs.fontWeight = weight;
    savePrefs(prefs);
    updateWeightButtons();
  }
  function updateWeightButtons() {
    document.querySelectorAll('#a11yWeightGroup .a11y-btn').forEach(btn => {
      btn.classList.toggle('active', btn.dataset.weight === prefs.fontWeight);
    });
  }

  // ── Voice Helper ─────────────────────────────────────────
  let currentUtterance = null;

  function readPage() {
    if (!('speechSynthesis' in window)) {
      alert('Sorry, your browser does not support text-to-speech.');
      return;
    }
    stopReading();

    // Gather readable text from main content
    const mainContent = document.querySelector('.main-content') || document.querySelector('.main-wrapper') || document.body;
    const pageTitle = document.querySelector('.topbar-title');
    let text = '';
    if (pageTitle) text += pageTitle.textContent + '. ';

    // Walk through content and build readable text
    const walker = document.createTreeWalker(mainContent, NodeFilter.SHOW_TEXT, {
      acceptNode: function (node) {
        const parent = node.parentElement;
        if (!parent) return NodeFilter.FILTER_REJECT;
        const tag = parent.tagName;
        const style = getComputedStyle(parent);
        if (style.display === 'none' || style.visibility === 'hidden') return NodeFilter.FILTER_REJECT;
        if (['SCRIPT', 'STYLE', 'NOSCRIPT'].includes(tag)) return NodeFilter.FILTER_REJECT;
        if (parent.closest('.a11y-panel') || parent.closest('.a11y-fab')) return NodeFilter.FILTER_REJECT;
        return NodeFilter.FILTER_ACCEPT;
      }
    });

    let node;
    while (node = walker.nextNode()) {
      const t = node.textContent.trim();
      if (t) text += t + ' ';
    }

    if (!text.trim()) {
      text = 'This page has no readable content.';
    }

    // Split into chunks (speechSynthesis has limits on long text)
    const chunks = splitText(text.trim(), 200);
    speakChunks(chunks, 0);

    const readBtn = document.getElementById('a11yReadPage');
    if (readBtn) readBtn.classList.add('speaking');
  }

  function splitText(text, maxWords) {
    const words = text.split(/\s+/);
    const chunks = [];
    for (let i = 0; i < words.length; i += maxWords) {
      chunks.push(words.slice(i, i + maxWords).join(' '));
    }
    return chunks;
  }

  function speakChunks(chunks, index) {
    if (index >= chunks.length) {
      const readBtn = document.getElementById('a11yReadPage');
      if (readBtn) readBtn.classList.remove('speaking');
      return;
    }

    const utterance = new SpeechSynthesisUtterance(chunks[index]);
    utterance.rate = prefs.voiceSpeed;
    utterance.pitch = 1;
    utterance.volume = 1;

    // Try to pick a good voice
    const voices = speechSynthesis.getVoices();
    const englishVoice = voices.find(v => v.lang.startsWith('en') && v.name.includes('Female'))
                      || voices.find(v => v.lang.startsWith('en'))
                      || voices[0];
    if (englishVoice) utterance.voice = englishVoice;

    utterance.onend = () => speakChunks(chunks, index + 1);
    utterance.onerror = () => {
      const readBtn = document.getElementById('a11yReadPage');
      if (readBtn) readBtn.classList.remove('speaking');
    };

    currentUtterance = utterance;
    speechSynthesis.speak(utterance);
  }

  function stopReading() {
    if ('speechSynthesis' in window) {
      speechSynthesis.cancel();
    }
    const readBtn = document.getElementById('a11yReadPage');
    if (readBtn) readBtn.classList.remove('speaking');
    currentUtterance = null;
  }

  function readText(text) {
    if (!('speechSynthesis' in window) || !text.trim()) return;
    speechSynthesis.cancel();
    const utterance = new SpeechSynthesisUtterance(text.trim());
    utterance.rate = prefs.voiceSpeed;
    const voices = speechSynthesis.getVoices();
    const englishVoice = voices.find(v => v.lang.startsWith('en')) || voices[0];
    if (englishVoice) utterance.voice = englishVoice;
    speechSynthesis.speak(utterance);
  }

  // ── Read-on-Hover ────────────────────────────────────────
  let hoverTimeout = null;
  function applyHoverRead() {
    if (prefs.readOnHover) {
      document.body.classList.add('a11y-hover-active');
      document.addEventListener('mouseover', hoverReadHandler);
      document.addEventListener('mouseout', hoverOutHandler);
    } else {
      document.body.classList.remove('a11y-hover-active');
      document.removeEventListener('mouseover', hoverReadHandler);
      document.removeEventListener('mouseout', hoverOutHandler);
    }
  }
  function hoverReadHandler(e) {
    const el = e.target.closest('p, h1, h2, h3, h4, h5, span, a, button, label, td, th, li, .stat-value, .stat-label, .forum-title, .forum-body, .event-card h3, .contact-value, .nav-item');
    if (!el || el.closest('.a11y-panel') || el.closest('.a11y-fab')) return;
    clearTimeout(hoverTimeout);
    hoverTimeout = setTimeout(() => {
      const text = el.textContent.trim();
      if (text && text.length > 1) readText(text);
    }, 400);
  }
  function hoverOutHandler() {
    clearTimeout(hoverTimeout);
  }

  // ── Reset ────────────────────────────────────────────────
  function resetAll() {
    stopReading();
    prefs = { ...defaults };
    savePrefs(prefs);
    applyAll();
    const panel = document.getElementById('a11yPanel');
    if (panel && panel.classList.contains('open')) {
      // Brief confirmation
      readText('Settings reset to default');
    }
  }

  // ── Apply all saved preferences ──────────────────────────
  function applyAll() {
    setContrast(prefs.contrast);
    setFontSize(prefs.fontSize);
    setFontWeight(prefs.fontWeight);

    const speedSlider = document.getElementById('a11yVoiceSpeed');
    const speedVal = document.getElementById('a11yVoiceSpeedVal');
    if (speedSlider) speedSlider.value = prefs.voiceSpeed;
    if (speedVal) speedVal.textContent = prefs.voiceSpeed + '×';

    const hoverCheck = document.getElementById('a11yReadHover');
    if (hoverCheck) hoverCheck.checked = prefs.readOnHover;
    applyHoverRead();
  }

  // ── Initialize ───────────────────────────────────────────
  function init() {
    buildPanel();
    applyAll();

    // Preload voices (Chrome needs this)
    if ('speechSynthesis' in window) {
      speechSynthesis.getVoices();
      speechSynthesis.onvoiceschanged = () => speechSynthesis.getVoices();
    }
  }

  // Run on DOM ready
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
  } else {
    init();
  }

  // Expose API for inline handlers
  window._a11y = { togglePanel, readPage, stopReading, resetAll };

})();
