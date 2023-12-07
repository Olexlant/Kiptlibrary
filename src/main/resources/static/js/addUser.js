function disableButton() {
    var btn = document.getElementById('btn');
    btn.disabled = true;
    btn.innerText = 'Завантаження...'
    var overlay__inner = document.getElementById('overlay__inner')
    overlay__inner.hidden = false;
}