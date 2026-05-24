document.addEventListener('DOMContentLoaded', () => {
  const header = document.querySelector('.site-header');
  if (header) {
    const syncHeader = () => header.classList.toggle('scrolled', window.scrollY > 12);
    syncHeader();
    window.addEventListener('scroll', syncHeader, { passive: true });
  }

  const menuToggle = document.querySelector('.menu-toggle');
  const mobileMenu = document.querySelector('#mobileMenu');
  if (menuToggle && mobileMenu) {
    menuToggle.addEventListener('click', () => {
      const isOpen = mobileMenu.classList.toggle('open');
      menuToggle.setAttribute('aria-expanded', String(isOpen));
      menuToggle.innerHTML = isOpen
        ? '<i class="fa-solid fa-xmark"></i>'
        : '<i class="fa-solid fa-bars"></i>';
    });
  }

  const revealEls = document.querySelectorAll('.reveal, .reveal-left, .reveal-right');
  if (revealEls.length) {
    const observer = new IntersectionObserver((entries) => {
      entries.forEach((entry) => {
        if (!entry.isIntersecting) return;
        entry.target.classList.add('visible');
        observer.unobserve(entry.target);
      });
    }, { threshold: 0.12, rootMargin: '0px 0px -60px 0px' });
    revealEls.forEach((el) => observer.observe(el));
  }

  document.querySelectorAll('.alert').forEach((alert) => {
    const close = () => {
      alert.style.opacity = '0';
      alert.style.transform = 'translateY(-8px)';
      setTimeout(() => alert.remove(), 220);
    };
    alert.querySelector('.alert-close')?.addEventListener('click', close);
    setTimeout(close, 4800);
  });

  document.querySelectorAll('.qty-control').forEach((control) => {
    const input = control.querySelector('.qty-input');
    const minus = control.querySelector('[data-action="minus"]');
    const plus = control.querySelector('[data-action="plus"]');
    if (!input) return;

    const setValue = (value) => {
      const min = Number(input.min || 1);
      const max = Number(input.max || 999);
      input.value = Math.max(min, Math.min(max, value));
    };

    minus?.addEventListener('click', () => setValue(Number(input.value || 1) - 1));
    plus?.addEventListener('click', () => setValue(Number(input.value || 1) + 1));
  });

  document.querySelectorAll('.btn-primary, .icon-btn').forEach((button) => {
    button.addEventListener('click', function (event) {
      const rect = this.getBoundingClientRect();
      const size = Math.max(rect.width, rect.height);
      const ripple = document.createElement('span');
      ripple.style.cssText = `
        position:absolute;
        width:${size}px;
        height:${size}px;
        left:${event.clientX - rect.left - size / 2}px;
        top:${event.clientY - rect.top - size / 2}px;
        border-radius:50%;
        background:rgba(255,255,255,.38);
        transform:scale(0);
        animation:ripple-anim .62s ease-out forwards;
        pointer-events:none;
      `;
      this.appendChild(ripple);
      setTimeout(() => ripple.remove(), 650);
    });
  });

  const sortSelect = document.querySelector('#sortSelect');
  const grid = document.querySelector('#productsGrid');
  if (sortSelect && grid) {
    sortSelect.addEventListener('change', () => {
      const cards = Array.from(grid.querySelectorAll('.product-card'));
      const value = sortSelect.value;
      if (!value) return;

      const getPrice = (card) => Number((card.querySelector('.product-row strong')?.textContent || '0').replace(/[^\d]/g, ''));
      const getName = (card) => (card.querySelector('h3')?.textContent || '').trim();

      cards.sort((a, b) => {
        if (value === 'price-asc') return getPrice(a) - getPrice(b);
        if (value === 'price-desc') return getPrice(b) - getPrice(a);
        return getName(a).localeCompare(getName(b), 'vi');
      });

      grid.style.opacity = '.35';
      setTimeout(() => {
        cards.forEach((card) => grid.appendChild(card));
        grid.style.opacity = '1';
      }, 180);
    });
  }
});
