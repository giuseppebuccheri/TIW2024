/**
 * Auth management
 */

(function () { // avoid variables ending up in the global scope

    //Mostra i due form

    var loginFormDiv = document.getElementById('login-form');
    var signupFormDiv = document.getElementById('signup-form');
    var showSignupLink = document.getElementById('show-signup');
    var showLoginLink = document.getElementById('show-login');

    showSignupLink.addEventListener('click', function (event) {
        event.preventDefault();
        loginFormDiv.style.display = 'none';
        signupFormDiv.style.display = 'block';
    });

    showLoginLink.addEventListener('click', function (event) {
        event.preventDefault();
        signupFormDiv.style.display = 'none';
        loginFormDiv.style.display = 'block';
    });

    //gestisci autenticazioni

    document.getElementById("loginButton").addEventListener('click', (e) => {
        var form = e.target.closest("form");

        if (form.checkValidity()) {
            makeCall("POST", 'login', e.target.closest("form"),
                function (req) {
                    if (req.readyState === XMLHttpRequest.DONE) {
                        var message = req.responseText;

                        switch (req.status) {
                            case 200:
                                sessionStorage.setItem('username', message);
                                window.location.href = "home.html";
                                break;
                            case 400: // bad request
                                document.getElementById("errorMessage").textContent = message;
                                break;
                            case 401: // unauthorized
                                document.getElementById("errorMessage").textContent = message;
                                break;
                            case 500: // server error
                                document.getElementById("errorMessage").textContent = message;
                                break;
                        }
                    }
                }, false);
        } else {
            form.reportValidity();
        }
    });

    document.getElementById("signupButton").addEventListener('click', (e) => {
        var form = e.target.closest("form");

        var password = form.querySelector("#signup-password").value;
        var repeatPassword = form.querySelector("#signup-repeat-password").value;
        var email = form.querySelector("#signup-email").value;

        if (form.checkValidity() && password === repeatPassword && isValidEmail(email)) {
            makeCall("POST", 'signup', e.target.closest("form"),
                function (req) {
                    if (req.readyState === XMLHttpRequest.DONE) {
                        var message = req.responseText;

                        switch (req.status) {
                            case 200:
                                sessionStorage.setItem('username', message);
                                window.location.href = "home.html";
                                break;
                            case 400: // bad request
                                document.getElementById("errorMessage").textContent = message;
                                break;
                            case 401: // unauthorized
                                document.getElementById("errorMessage").textContent = message;
                                break;
                            case 500: // server error
                                document.getElementById("errorMessage").textContent = message;
                                break;
                        }
                    }
                }, false);
        } else {
            if (password !== repeatPassword && !isValidEmail(email)) {
                document.getElementById("errorMessage").textContent = "Invalid email format and passwords do not match.";
            } else if (password !== repeatPassword) {
                document.getElementById("errorMessage").textContent = "Passwords do not match.";
            } else if (!isValidEmail(email)) {
                document.getElementById("errorMessage").textContent = "Invalid email format.";
            } else {
                form.reportValidity();
            }
        }
    });

    function isValidEmail(email) {
        var re = /\S+@\S+\.\S+/;
        return re.test(email);
    }

})();
