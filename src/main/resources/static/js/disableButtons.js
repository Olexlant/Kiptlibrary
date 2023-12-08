function disableButtons(){
    var buttons = document.getElementsByTagName('button');
    for (let i = 0; i < buttons.length; i++) {
        let button = buttons[i];
        button.disabled = true;
    }
    showOverlayLoading()
}
function showOverlayLoading(){
    var overlay__inner = document.getElementById('overlay__inner')
    overlay__inner.hidden = false;
}