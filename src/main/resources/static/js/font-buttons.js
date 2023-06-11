
window.onload = function(){
    let cat = localStorage.getItem("plus");
    if(cat==="true"){
        buttonfontplus()
    }else if (cat==="false"){
        buttonfontminus()
    }
};
function buttonfontplus() {
    let plus = "true";
    localStorage.setItem("plus", plus);
    document.querySelector('body button').style = 'font-size: 33px !important';
    document.querySelector('body a').style = 'font-size: 45px !important';
    document.querySelector('body p').style = 'font-size: 25px !important';
    document.querySelector('section span').style = 'font-size: 45px !important';
    document.getElementsByClassName('title')[0].style= "font-size: 95px !important";
    document.getElementsByClassName('text-two')[0].style= "font-size: 70px !important";
    document.getElementsByClassName('text-three')[0].style= "font-size: 45px !important";
    document.getElementsByClassName('text-three')[0].style= "font-size: 45px !important";
    document.getElementsByClassName('text-four')[0].style= "font-size: 30px !important";
    document.getElementsByClassName('topic')[0].style= "font-size: 40px !important";
    document.getElementsByClassName('button-fon')[0].style= "font-size: 33px !important";
    document.getElementsByClassName('title-news')[0].style= "font-size: 45px !important";
    document.getElementsByClassName('title-bibl')[0].style= "font-size: 45px !important";
    document.getElementsByClassName('topic-bibl')[0].style= "font-size: 40px !important";
    document.getElementsByClassName('topic-perevag')[0].style= "font-size: 40px !important";
    document.getElementsByClassName('topic-contact')[0].style= "font-size: 40px !important";
    document.getElementsByClassName('topic1')[0].style= "font-size: 40px !important";
    document.getElementsByClassName('topic2')[0].style= "font-size: 40px !important";
    document.getElementsByClassName('topic3')[0].style= "font-size: 40px !important";
    document.getElementsByClassName('topic4')[0].style= "font-size: 40px !important";
    document.getElementsByClassName('topic5')[0].style= "font-size: 40px !important";
    document.getElementsByClassName('topic6')[0].style= "font-size: 40px !important";
    document.getElementsByClassName('topic7')[0].style= "font-size: 40px !important";
    document.getElementsByClassName('pol')[0].style= "font-size: 26px !important";
    document.getElementsByClassName('pol1')[0].style= "font-size: 26px !important";
    document.getElementsByClassName('pol2')[0].style= "font-size: 26px !important";
    document.getElementsByClassName('pol3')[0].style= "font-size: 26px !important";
    document.getElementsByClassName('pol4')[0].style= "font-size: 26px !important";
    document.getElementsByClassName('pol5')[0].style= "font-size: 26px !important";
    document.getElementsByClassName('icon-contact')[0].style= "font-size: 65px !important";
    document.getElementsByClassName('contact')[0].style= "font-size: 25px !important";
    document.querySelector('footer span').style = 'font-size: 27px !important';
    document.getElementsByClassName('boxes')[0].style= "font-size: 26px !important";
    document.getElementsByClassName('short-p')[0].style= "font-size: 25px !important";
    document.getElementsByClassName('exp')[0].style= "font-size: 24px !important";
    document.getElementsByClassName('boxes')[0].style= "font-size: 23px !important";
    document.getElementsByClassName('topic')[0].style= "font-size: 23px !important";
    document.querySelector('section p').style = 'font-size: 23px !important';
}

function buttonfontminus() {
    let plus = "false";
    localStorage.setItem("plus", plus);
    document.querySelector('body div,p,span,a,button').style.setProperty('font-size', '15px', 'important');
    document.getElementsByClassName('text-two')[0].style= "font-size: 60px !important";
    document.getElementsByClassName('text-three')[0].style= "font-size: 35px !important";
    document.getElementsByClassName('text-three')[0].style= "font-size: 35px !important";
    document.getElementsByClassName('text-four')[0].style= "font-size: 20px !important";
    document.querySelector('body button').style = 'font-size: 23px !important';
    document.querySelector('section span').style = 'font-size: 30px !important';
    document.querySelector('body p').style = 'font-size: 16px !important';
    document.getElementsByClassName('topic')[0].style= "font-size: 30px !important";
    document.getElementsByClassName('button-fon')[0].style= "font-size: 23px !important";
    document.getElementsByClassName('title-news')[0].style= "font-size: 30px !important";
    document.getElementsByClassName('title-bibl')[0].style= "font-size: 30px !important";
    document.getElementsByClassName('topic-bibl')[0].style= "font-size: 30px !important";
    document.getElementsByClassName('topic-bibl')[0].style= "font-size: 30px !important";
    document.getElementsByClassName('topic-perevag')[0].style= "font-size: 30px !important";
    document.getElementsByClassName('topic-contact')[0].style= "font-size: 30px !important";
    document.getElementsByClassName('topic1')[0].style= "font-size: 32px !important";
    document.getElementsByClassName('topic2')[0].style= "font-size: 32px !important";
    document.getElementsByClassName('topic3')[0].style= "font-size: 32px !important";
    document.getElementsByClassName('topic4')[0].style= "font-size: 32px !important";
    document.getElementsByClassName('topic5')[0].style= "font-size: 32px !important";
    document.getElementsByClassName('topic6')[0].style= "font-size: 32px !important";
    document.getElementsByClassName('topic7')[0].style= "font-size: 30px !important";
    document.getElementsByClassName('pol')[0].style= "font-size: 16px !important";
    document.getElementsByClassName('pol1')[0].style= "font-size: 16px !important";
    document.getElementsByClassName('pol2')[0].style= "font-size: 16px !important";
    document.getElementsByClassName('pol3')[0].style= "font-size: 16px !important";
    document.getElementsByClassName('pol4')[0].style= "font-size: 16px !important";
    document.getElementsByClassName('pol5')[0].style= "font-size: 16px !important";
    document.getElementsByClassName('icon-contact')[0].style= "font-size: 50px !important";
    document.getElementsByClassName('contact')[0].style= "font-size: 16px !important";
    document.querySelector('footer span').style = 'font-size: 17px !important';
    document.getElementsByClassName('boxes')[0].style= "font-size: 16px !important";
    document.getElementsByClassName('short-p')[0].style= "font-size: 16px !important";
    document.getElementsByClassName('boxes')[0].style= "font-size: 16px !important";
    document.getElementsByClassName('exp')[0].style= "font-size: 18px !important";
    document.querySelector('section p').style = 'font-size: 16px !important';
    document.querySelector('p').style = 'font-size: 16px !important';

}
