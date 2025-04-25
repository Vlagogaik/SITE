var a_link = document.querySelectorAll(
  "a-link, a, .register-btn, .redirect-btn, .navbar-links a, .navbar-logo a, .footer-links a, btn close-btn, btn delete-btn, reg-link, login-link, createDeposit, signin, btn close-btn, btn transfer-btn"
);


// Handle mouse over/out event on links
a_link.forEach(e => e.addEventListener('mouseenter', handleMouseEnter));
a_link.forEach(e => e.addEventListener('mouseleave', handleMouseLeave));
window.addEventListener('mousemove', handleMouseMove);
// Move the cursor in dom/window
function handleMouseMove(event) {
  console.log(event);
  var top = event.pageY - (cursor.clientHeight / 2);
  var left = event.pageX - (cursor.clientWidth / 2);
  cursor.style.top = top + 'px';
  cursor.style.left = left + 'px';
}
// event: mouse enter on link
function handleMouseEnter() {
  cursor.classList.add('hovered');
}
// event: mouse leave on link
function handleMouseLeave() {
  cursor.classList.remove('hovered');
}