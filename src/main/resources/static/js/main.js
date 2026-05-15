/**
 * Smart Campus Events — main.js
 * Client-side enhancements (vanilla JS, no jQuery needed)
 */

// ── Mobile Navigation Toggle ────────────────────────────────
function toggleNav() {
  const links = document.getElementById('navLinks');
  if (links) links.classList.toggle('open');
}

// ── FAQ Toggle ──────────────────────────────────────────────
function toggleFaq(card) {
  const isOpen = card.classList.contains('open');
  // Close all
  document.querySelectorAll('.faq-card.open').forEach(c => c.classList.remove('open'));
  // Open clicked (unless it was already open)
  if (!isOpen) card.classList.add('open');
}

// ── Auto-dismiss Alerts ─────────────────────────────────────
document.addEventListener('DOMContentLoaded', function () {
  // Auto-dismiss success/info alerts after 5 seconds
  document.querySelectorAll('.alert-success, .alert-info').forEach(function (alert) {
    setTimeout(function () {
      alert.style.transition = 'opacity 0.5s';
      alert.style.opacity = '0';
      setTimeout(function () { alert.remove(); }, 500);
    }, 5000);
  });

  // Animate stat bars (for statistics page)
  document.querySelectorAll('.stat-bar-fill').forEach(function (bar) {
    const finalWidth = bar.style.width;
    bar.style.width = '0%';
    setTimeout(function () {
      bar.style.transition = 'width 0.8s ease';
      bar.style.width = finalWidth;
    }, 200);
  });

  // Animate bar chart bars (grow up from 0)
  document.querySelectorAll('.bar-fill').forEach(function (bar, i) {
    const finalHeight = bar.style.height;
    bar.style.height = '0px';
    bar.style.transition = 'none';
    setTimeout(function () {
      bar.style.transition = 'height 0.7s ease';
      bar.style.height = finalHeight;
    }, 100 + i * 80);
  });

  // Set today's date as min for date inputs in event form
  const dateInputs = document.querySelectorAll('input[type="date"]');
  const today = new Date().toISOString().split('T')[0];
  dateInputs.forEach(function (input) {
    if (!input.value) {
      // Only set min, don't change existing values
      input.setAttribute('min', today);
    }
  });

  // Character counter for textareas
  document.querySelectorAll('textarea[maxlength]').forEach(function (textarea) {
    const max = textarea.getAttribute('maxlength');
    const hint = textarea.parentElement.querySelector('.form-hint');
    if (hint) {
      textarea.addEventListener('input', function () {
        const remaining = max - textarea.value.length;
        hint.textContent = remaining + ' characters remaining';
        hint.style.color = remaining < 50 ? '#e8503a' : '';
      });
    }
  });

  // Confirm delete dialogs
  document.querySelectorAll('form[onsubmit*="confirm"]').forEach(function (form) {
    // Already handled inline; this is a fallback
  });

  // Scroll to error field on form submission
  const firstError = document.querySelector('.field-error:not(:empty)');
  if (firstError) {
    firstError.closest('.form-group').scrollIntoView({ behavior: 'smooth', block: 'center' });
  }

  // Smooth scroll for hero CTA
  const heroBtn = document.querySelector('a[href="#events"]');
  if (heroBtn) {
    heroBtn.addEventListener('click', function (e) {
      e.preventDefault();
      const target = document.querySelector('#events');
      if (target) target.scrollIntoView({ behavior: 'smooth' });
    });
  }
});

// ── Toggle Password Visibility ──────────────────────────────
function togglePassword() {
  const input = document.getElementById('password');
  if (input) {
    input.type = input.type === 'password' ? 'text' : 'password';
  }
}