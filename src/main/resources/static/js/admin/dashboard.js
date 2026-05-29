(function () {
  'use strict';

  /* Alerts auto-dismiss */
  function initAlerts() {
    document.querySelectorAll('.alert').forEach(function (el) {
      var btn = el.querySelector('.alert-close');
      if (btn) btn.addEventListener('click', function () { dismiss(el); });
      setTimeout(function () { dismiss(el); }, 4200);
    });
  }
  function dismiss(el) {
    el.style.transition = 'opacity .3s ease, transform .3s ease';
    el.style.opacity = '0';
    el.style.transform = 'translateX(110%)';
    setTimeout(function () { el.remove(); }, 320);
  }

  /* Active nav */
  function initActiveNav() {
    var path = window.location.pathname;
    document.querySelectorAll('.admin-nav-link').forEach(function (a) {
      var href = a.getAttribute('href') || '';
      if (href && href !== '#' && path.startsWith(href)) a.classList.add('active');
    });
  }

  /* Password toggle */
  function initPasswordToggle() {
    document.querySelectorAll('[data-toggle-password]').forEach(function (btn) {
      btn.addEventListener('click', function () {
        var input = document.getElementById(btn.dataset.togglePassword);
        if (!input) return;
        var show = input.type === 'password';
        input.type = show ? 'text' : 'password';
        var icon = btn.querySelector('i');
        if (icon) icon.className = show ? 'fa-solid fa-eye-slash' : 'fa-solid fa-eye';
        btn.setAttribute('aria-label', show ? 'Ẩn mật khẩu' : 'Hiện mật khẩu');
      });
    });
  }

  /* Status select color */
  function initStatusSelect() {
    var map = { PENDING:'#f0b429', CONFIRMED:'#38bdf8', DELIVERED:'#4ade80', CANCELLED:'#f05050' };
    document.querySelectorAll('.status-select').forEach(function (sel) {
      function paint() { sel.style.color = map[(sel.value||'').toUpperCase()] || ''; }
      paint();
      sel.addEventListener('change', paint);
    });
  }

  /* Counter animation */
  function initCounters() {
    document.querySelectorAll('.stat-value').forEach(function (el) {
      var text  = el.textContent.trim();
      var prefix = text.match(/^[^0-9]*/)[0];
      var raw    = text.replace(/[^0-9]/g, '');
      if (!raw) return;
      var target = parseInt(raw, 10);
      if (!target) return;
      var start = null, dur = 900;
      requestAnimationFrame(function step(ts) {
        if (!start) start = ts;
        var p = Math.min((ts - start) / dur, 1);
        var e = 1 - Math.pow(1 - p, 3);
        el.textContent = prefix + Math.floor(e * target).toLocaleString('vi-VN');
        if (p < 1) requestAnimationFrame(step);
        else el.textContent = prefix + target.toLocaleString('vi-VN');
      });
    });
  }

  /* Image preview */
  function initImagePreview() {
    var fi = document.getElementById('imageFile');
    if (!fi) return;
    fi.addEventListener('change', function () {
      var file = fi.files && fi.files[0];
      if (!file) return;
      var r = new FileReader();
      r.onload = function (e) {
        var img = document.querySelector('.current-image');
        if (img) { img.src = e.target.result; return; }
        img = document.createElement('img');
        img.src = e.target.result;
        img.className = 'current-image';
        fi.parentElement.insertAdjacentElement('beforebegin', img);
      };
      r.readAsDataURL(file);
    });
  }

  document.addEventListener('DOMContentLoaded', function () {
    initAlerts();
    initActiveNav();
    initPasswordToggle();
    initStatusSelect();
    initCounters();
    initImagePreview();
  });
})();