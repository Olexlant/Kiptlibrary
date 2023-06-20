
window.onload = function(){
    var increaseCount = localStorage.getItem("increaseCount");
    for (var i=0; i<increaseCount; i++){
        buttonfontplus("auto")
    }
};
function increasefont(elements){
    for(var i=0; i < elements.length; i++){
        try {
            add20per(elements[i]);
        } catch (e){}
    }
}
function decreaseFont(elements){
    for(var i=0; i < elements.length; i++){
        try {
            min20per(elements[i]);
        } catch (e){}
    }
}
var n = 0;
function buttonfontplus(auto) {
    var increaseCount = localStorage.getItem("increaseCount");
    if (auto==="auto"){
    }else{
        if (increaseCount<=1){
            increaseCount++;
            localStorage.setItem("increaseCount", increaseCount);
        }
    }
    if (n<increaseCount){
        n++
        increasefont(document.querySelectorAll('.box p'))
        increasefont(document.querySelectorAll('.about-details .left p'))
        increasefont(document.querySelectorAll('.skills-details p'))
        increasefont(document.querySelectorAll('.user-info h2 span'))
        increasefont(document.querySelectorAll('.navbar .menu .golovna'))
        increasefont(document.querySelectorAll('.navbar .btn a'))
        increasefont(document.querySelectorAll('div .text'))
        increasefont(document.querySelectorAll('div .topic'))
        increasefont(document.querySelectorAll('select'))
        increasefont(document.querySelectorAll('option'))
        increasefont(document.querySelectorAll('span .itcss__item_name'))
        increasefont(document.querySelectorAll('span .itcss__item_text'))
        increasefont(document.querySelectorAll('div .title'))
        increasefont(document.querySelectorAll('div .title-library'))
        increasefont(document.querySelectorAll('div .title-services'))
        increasefont(document.querySelectorAll('div .title-contact'))
        increasefont(document.querySelectorAll('section .topic-bibl'))
        increasefont(document.querySelectorAll('div .topic'))
        increasefont(document.querySelectorAll('div .per'))
        increasefont(document.querySelectorAll('div .num'))
        increasefont(document.querySelectorAll('div .exp'))
        increasefont(document.querySelectorAll('div .topic1'))
        increasefont(document.querySelectorAll('div .topic2'))
        increasefont(document.querySelectorAll('div .topic3'))
        increasefont(document.querySelectorAll('div .topic4'))
        increasefont(document.querySelectorAll('div .topic5'))
        increasefont(document.querySelectorAll('div .topic6'))
        increasefont(document.querySelectorAll('p .pol'))
        increasefont(document.querySelectorAll('p .pol1'))
        increasefont(document.querySelectorAll('p .pol2'))
        increasefont(document.querySelectorAll('p .pol3'))
        increasefont(document.querySelectorAll('p .pol4'))
        increasefont(document.querySelectorAll('p .pol5'))
        increasefont(document.querySelectorAll('button'))
        increasefont(document.querySelectorAll('form .user-details .input-box input'))
        increasefont(document.querySelectorAll('form .input-box span.details'))
        increasefont(document.querySelectorAll('.user-details label input'))
        increasefont(document.querySelectorAll('.user-details label span'))
        increasefont(document.querySelectorAll('form .sign_up'))
        increasefont(document.querySelectorAll('form a'))
        increasefont(document.querySelectorAll('.container .box-container .box .content h3'))
        increasefont(document.querySelectorAll('.container .box-container .box .content .icons span'))
        increasefont(document.querySelectorAll('.v label'))
        increasefont(document.querySelectorAll('.v input'))
        increasefont(document.querySelectorAll('.container form input'))
        increasefont(document.querySelectorAll('.pop_up_body'))
        increasefont(document.querySelectorAll('.box .button a'))
    }
}

function buttonfontminus() {
    var increaseCount = localStorage.getItem("increaseCount");
    if (increaseCount>0){
        increaseCount--;
        n--
        localStorage.setItem("increaseCount", increaseCount);
        decreaseFont(document.querySelectorAll('.box p'))
        decreaseFont(document.querySelectorAll('.about-details .left p'))
        decreaseFont(document.querySelectorAll('.skills-details p'))
        decreaseFont(document.querySelectorAll('.user-info h2 span'))
        decreaseFont(document.querySelectorAll('.navbar .menu .golovna'))
        decreaseFont(document.querySelectorAll('.navbar .btn a'))
        decreaseFont(document.querySelectorAll('div .text'))
        decreaseFont(document.querySelectorAll('div .topic'))
        decreaseFont(document.querySelectorAll('select'))
        decreaseFont(document.querySelectorAll('option'))
        decreaseFont(document.querySelectorAll('span .itcss__item_name'))
        decreaseFont(document.querySelectorAll('span .itcss__item_text'))
        decreaseFont(document.querySelectorAll('div .title'))
        decreaseFont(document.querySelectorAll('div .title-library'))
        decreaseFont(document.querySelectorAll('div .title-services'))
        decreaseFont(document.querySelectorAll('div .title-contact'))
        decreaseFont(document.querySelectorAll('section .topic-bibl'))
        decreaseFont(document.querySelectorAll('div .topic'))
        decreaseFont(document.querySelectorAll('div .per'))
        decreaseFont(document.querySelectorAll('div .num'))
        decreaseFont(document.querySelectorAll('div .exp'))
        decreaseFont(document.querySelectorAll('div .topic1'))
        decreaseFont(document.querySelectorAll('div .topic2'))
        decreaseFont(document.querySelectorAll('div .topic3'))
        decreaseFont(document.querySelectorAll('div .topic4'))
        decreaseFont(document.querySelectorAll('div .topic5'))
        decreaseFont(document.querySelectorAll('div .topic6'))
        decreaseFont(document.querySelectorAll('p .pol'))
        decreaseFont(document.querySelectorAll('p .pol1'))
        decreaseFont(document.querySelectorAll('p .pol2'))
        decreaseFont(document.querySelectorAll('p .pol3'))
        decreaseFont(document.querySelectorAll('p .pol4'))
        decreaseFont(document.querySelectorAll('p .pol5'))
        decreaseFont(document.querySelectorAll('button'))
        decreaseFont(document.querySelectorAll('form .user-details .input-box input'))
        decreaseFont(document.querySelectorAll('form .input-box span.details'))
        decreaseFont(document.querySelectorAll('.user-details label input'))
        decreaseFont(document.querySelectorAll('.user-details label span'))
        decreaseFont(document.querySelectorAll('form .sign_up'))
        decreaseFont(document.querySelectorAll('form a'))
        decreaseFont(document.querySelectorAll('.container .box-container .box .content h3'))
        decreaseFont(document.querySelectorAll('.container .box-container .box .content .icons span'))
        decreaseFont(document.querySelectorAll('.v label'))
        decreaseFont(document.querySelectorAll('.v input'))
        decreaseFont(document.querySelectorAll('.container form input'))
        decreaseFont(document.querySelectorAll('.pop_up_body'))
        decreaseFont(document.querySelectorAll('.box .button a'))

    }
}
function add20per(element) {
    var currentSize = window.getComputedStyle(element, null).getPropertyValue('font-size');
    if (currentSize) {
        currentSize = parseFloat(currentSize.replace("px",""));
        element.style.fontSize = (currentSize * 1.1) + "px";
        for(var i=0; i < element.children.length; i++){
            add20per(element.children[i]);
        }
    }
}

function min20per(element) {
    var currentSize = window.getComputedStyle(element, null).getPropertyValue('font-size');
    if (currentSize) {
        currentSize = parseFloat(currentSize.replace("px",""));
        element.style.fontSize = (currentSize - (currentSize / 11)) + "px";
        for(var i=0; i < element.children.length; i++){
            min20per(element.children[i]);
        }
    }
}
