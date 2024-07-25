(function () {

    //Mostra i due form
    let loginFormDiv = document.getElementById('login-form');
    let signupFormDiv = document.getElementById('signup-form');
    let showSignupLink = document.getElementById('show-signup');
    let showLoginLink = document.getElementById('show-login');
    let errorMessage = document.getElementById("errorMessage");

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

    //ricevi richiesta login
    document.getElementById("loginButton").addEventListener('click', (e) => {

        errorMessage.style.display = "none";

        var url = "login";
        makePost(url, e.target.closest("form"));
    });

    //ricevi richiesta signup
    document.getElementById("signupButton").addEventListener('click', (e) => {

        errorMessage.style.display = "none";

        var url = "signup";
        var form = e.target.closest("form");

        var password = form.querySelector("#signup-password").value;
        var repeatPassword = form.querySelector("#signup-repeat-password").value;
        var email = form.querySelector("#signup-email").value;

        var inputs = form.querySelectorAll("input[required]");
        for (var input of inputs) {
            if (!input.value.trim()) {
                errorMessage.textContent = "please fill all the fields";
                return;
            }
        }

        //Controlli corrispondenza password e validità sintattica email lato client

        if (password === repeatPassword && isValidEmail(email)) {
            makePost(url, form);
            return;
        }

        if (password != null && repeatPassword != null && email != null) {
            if (password !== repeatPassword && !isValidEmail(email)) {
                errorMessage.textContent = "Invalid email format and passwords do not match.";
                return;
            }
            if (password !== repeatPassword) {
                errorMessage.textContent = "Passwords do not match.";
                return;
            }
            if (!isValidEmail(email)) {
                errorMessage.textContent = "Invalid email format.";
            }
        }
    });

    function makePost(url, formElement) {
        request = new XMLHttpRequest();
        var formData = new FormData(formElement);
        request.onreadystatechange = showResults;
        request.open("POST", url);
        request.send(formData);
    }

    function showResults() {
        errorMessage.style.display = "none";

        if (request.readyState === XMLHttpRequest.DONE) {
            if (request.status === 200) {
                var message = JSON.parse(request.responseText);
                sessionStorage.setItem('id', message.id);
                sessionStorage.setItem('username', message.username);
                window.location.href = "home.html";
            } else {
                var message = request.responseText;
                errorMessage.textContent = message;
                errorMessage.style.display = "block";
            }
        }
    }

    function isValidEmail(email) {
        var re = /\S+@\S+\.\S+/;
        return re.test(email);
    }

})();
