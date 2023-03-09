
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
        const formdata = new FormData(form);
        const groupId = formdata.get("groupId");
        const groupName = formdata.get("groupName");
        document.getElementById("newGroupId").value = groupId;
        document.getElementById("newGroupName").value = groupName;
    }

    function submitForm1() {
      const newGroupId = document.getElementById("newGroupId").value
      const newGroupName = document.getElementById("newGroupName").value
        fetch("/admin/groups/"+ newGroupId+"/"+newGroupName+"/update", {
            method: 'POST'
        })
        setTimeout(function(){
            window.location.assign("/admin/groups?saved")
        }, 200)
    }

    function closepopupwindow1(){
        const closePopUp1 = document.getElementById('pop_up_close1');
        closePopUp1.addEventListener('click', () => {
        popUp1.classList.remove('active');
      })
    }

let currentForm2;
function openpopup2(form){
    const popUp = document.getElementById('pop_up2');
    popUp.classList.add('active');
    currentForm2 = form;
}

function submitForm2() {
    currentForm2.submit();
}

function closepopupwindow2(){
    const closePopUp = document.getElementById('pop_up_close2');
    closePopUp.addEventListener('click', () => {
        popUp.classList.remove('active');
    })
}