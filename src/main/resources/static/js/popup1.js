let currentForm;
    function openpopup(form){
      const popUp = document.getElementById('pop_up1');
      popUp.classList.add('active');
      currentForm = form;
    }

    function submitForm() {
      currentForm.submit();
    }

    function closepopupwindow(){
        const closePopUp = document.getElementById('pop_up_close1');
        closePopUp.addEventListener('click', () => {
        popUp.classList.remove('active');
      })
    }