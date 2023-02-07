
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



