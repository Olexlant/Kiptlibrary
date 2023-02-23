
let currentForm;
    function openpopup(form){
      const popUp = document.getElementById('pop_up');
      popUp.classList.add('active');
      currentForm = form;
    }

    function submitForm() {
      currentForm.submit();
    }

    function closepopupwindow(){
        const closePopUp = document.getElementById('pop_up_close');
        closePopUp.addEventListener('click', () => {
        popUp.classList.remove('active');
      })
    }



let currentForm1;
    function openpopup1(form){
      const popUp1 = document.getElementById('pop_up1');
      popUp1.classList.add('active');
      currentForm1 = form;
    }

    function submitForm1() {
      currentForm1.submit();
    }

    function closepopupwindow1(){
        const closePopUp1 = document.getElementById('pop_up_close1');
        closePopUp1.addEventListener('click', () => {
        popUp1.classList.remove('active');
      })
    }

