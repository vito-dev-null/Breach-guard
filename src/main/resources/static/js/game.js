// Small UI niceties for the Breach Game
document.addEventListener('DOMContentLoaded', () => {
  const badge = document.querySelector('.badge');
  if (badge && badge.classList.contains('bg-success')) {
    badge.classList.add('confetti');
  }

  document.querySelectorAll('.background-mask, .mask-icon, .status-icon, .status-icon-anonymous, .mask-theme').forEach(el => el.remove());

  if (document.body.classList.contains('result-page')) {
    document.body.style.background = '#050814';
    document.body.style.backgroundImage = 'none';
    document.body.style.boxShadow = 'none';
    document.body.style.filter = 'none';
  }
});
