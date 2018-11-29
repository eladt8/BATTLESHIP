$(function getUserError() {
    $.ajax({
        url: "actions/error",
        success: function (Error) {
            debugger;
            updateErrorMessageAndRedirect(Error);
        }
    });
});

function updateErrorMessageAndRedirect(error) {
    var errorDiv = document.getElementById("errorDiv");
    errorDiv.innerText = "An error occurred!\n" + error.m_ErrorMsg.toString();
    redirectWithDelay(error.m_ErrorType.toString());
}

function redirectWithDelay(redirectTo) {
    var redirectMsg = "Redirecting to: ";
    var redirectUrl;

    switch (redirectTo) {
        case "NAME_USED":
            redirectMsg += "SignUp Page";
            redirectUrl = "usersignup";
            break;
        case "FILE_UPLOAD":
        case "ROOM_NAME_USED":
            redirectMsg += "Lobby";
            redirectUrl = "pages/lobby/gamelobby.html";
            break;
        case "UNEXPECTED":
            redirectMsg += "Main Page";
            redirectUrl = "index.html";
            break;
    }

    var redirectEle = document.getElementById("redirectDiv");
    redirectEle.innerText = redirectMsg + ", in 5 seconds\n ";
    var a = document.createElement('a');
    a.appendChild(redirectEle);
    a.title = "Click here to be redirected without waiting";
    a.href = redirectUrl;
    document.body.appendChild(a);
    setTimeout(function (param) {
            window.location.href = param;
        }
        , 5000, redirectUrl);
}
