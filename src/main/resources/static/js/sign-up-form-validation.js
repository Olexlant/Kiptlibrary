document.addEventListener('DOMContentLoaded', function() {
    var emailInput = document.getElementById('emailInput');
    var passwordInput = document.getElementById('passwordInput');
    var firstNameInput = document.getElementById('firstNameInput');
    var lastNameInput = document.getElementById('lastNameInput');
    var phoneInput = document.getElementById('phoneInput');
    var groupInput = document.getElementById('groupInput');
    var registrationKeyInput = document.getElementById('registrationKeyInput');

    var emailValid = false;
    var passValid = false;
    var firstNameValid = false;
    var lastNameValid = false;
    var phoneValid = false;
    var groupValid = false;
    var registrationKeyValid = false;

    emailInput.addEventListener('input', function() {
        var emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
        var emailError = document.getElementById('emailError');
        if (!emailPattern.test(emailInput.value)) {
            emailError.style.display = 'inline';
            emailValid = false
        } else {
            emailError.style.display = 'none';
            emailValid = true
        }
    });

    passwordInput.addEventListener('input', function() {
        var passwordError = document.getElementById('passwordError');
        if (passwordInput.value.length < 6) {
            passwordError.style.display = 'inline';
            passValid = false
        } else {
            passwordError.style.display = 'none';
            passValid = true
        }
    });

    firstNameInput.addEventListener('input', function() {
        var firstNameError = document.getElementById('firstNameError');
        if (firstNameInput.value.length < 2) {
            firstNameError.style.display = 'inline';
            firstNameValid = false
        } else {
            firstNameError.style.display = 'none';
            firstNameValid = true
        }
    });

    lastNameInput.addEventListener('input', function() {
        var lastNameError = document.getElementById('lastNameError');
        if (lastNameInput.value.length < 2) {
            lastNameError.style.display = 'inline';
            lastNameValid = false
        } else {
            lastNameError.style.display = 'none';
            lastNameValid = true
        }
    });

    phoneInput.addEventListener('input', function() {
        var phonePattern = /^\+380\d{9}$/;
        var phoneError = document.getElementById('phoneError');
        if (!phonePattern.test(phoneInput.value)) {
            phoneError.style.display = 'inline';
            phoneValid = false
        } else {
            phoneError.style.display = 'none';
            phoneValid = true
        }
    });

    registrationKeyInput.addEventListener('input', function() {
        var registrationKeyError = document.getElementById('registrationKeyError');
        if (registrationKeyInput.value.length !== 6) {
            registrationKeyError.style.display = 'inline';
            registrationKeyValid = true
        } else {
            registrationKeyError.style.display = 'none';
            registrationKeyValid = true
        }
    });

    document.getElementById('buttonSubmit').addEventListener('click', function(event) {
        if (!emailValid || !passValid || !firstNameValid || !lastNameValid || !phoneValid){
            event.preventDefault();
            event.stopImmediatePropagation();
        }
    }, true);

});