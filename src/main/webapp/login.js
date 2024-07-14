document.addEventListener('DOMContentLoaded', function() {
    var loginFormDiv = document.getElementById('login-form');
    var signupFormDiv = document.getElementById('signup-form');
    var showSignupLink = document.getElementById('show-signup');
    var showLoginLink = document.getElementById('show-login');

    //Mostra i due form

    showSignupLink.addEventListener('click', function(event) {
        event.preventDefault();
        loginFormDiv.style.display = 'none';
        signupFormDiv.style.display = 'block';
    });

    showLoginLink.addEventListener('click', function(event) {
        event.preventDefault();
        signupFormDiv.style.display = 'none';
        loginFormDiv.style.display = 'block';
    });

    //Gestisci submit

    var loginForm = document.getElementById('login-form');
    var signupForm = document.getElementById('signup-form');

    loginForm.addEventListener('submit', function(event) {
        event.preventDefault();
        if (validateLoginForm()) {
            submitForm('login', new FormData(loginForm));
        }
    });

    signupForm.addEventListener('submit', function(event) {
        event.preventDefault();
        if (validateSignupForm()) {
            submitForm('signup', new FormData(signupForm));
        }
    });

    function validateLoginForm() {
        var username = document.getElementById('login-username').value;
        var password = document.getElementById('login-password').value;

        if (username.trim() === '' || password.trim() === '') {
            alert('Please enter both username and password.');
            return false;
        }
        return true;
    }

    function validateSignupForm() {
        var email = document.getElementById('signup-email').value;
        var username = document.getElementById('signup-username').value;
        var password = document.getElementById('signup-password').value;
        var repeatPassword = document.getElementById('signup-repeat-password').value;

        if (email.trim() === '' || username.trim() === '' || password.trim() === '' || repeatPassword.trim() === '') {
            alert('Please fill in all fields.');
            return false;
        }

        if (password !== repeatPassword) {
            alert('Passwords do not match.');
            return false;
        }

        return true;
    }

    function submitForm(action, formData) {
        fetch(action, {
            method: 'POST',
            body: formData
        })
            .then(response => response.json())
            .then(data => {
                if (data.status === 'success') {
                    sessionStorage.setItem('username', );
                    window.location.href = 'home';
                } else {
                    alert(data.message);
                }
            })
            .catch(error => {
                alert('An error occurred. Please try again later.');
            });
    }
});
