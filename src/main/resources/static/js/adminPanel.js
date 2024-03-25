window.onload = function(){
    getBookOrdersCount()
    generateRegistrationKey()
};
function generateRegistrationKey(){
    fetch("/admin/userService/generateRegistrationAccessKey")
        .then((response) => response.text())
        .then((data) => document.getElementById("registrationAccessKey").innerHTML = data)
}

function getBookOrdersCount(){
    fetch("/admin/bookorders/getcount")
        .then((response) => response.text())
        .then((data) => document.getElementById("bookOrdersCount").innerHTML = data)
}

function getRegistrationAccessKey() {
    fetch("/admin/userService/getRegistrationAccessKey")
        .then((response) => response.text())
        .then((data) => document.getElementById("registrationAccessKey").innerHTML = data)
}
setInterval(getRegistrationAccessKey, 5000);
setInterval(getBookOrdersCount, 5000)