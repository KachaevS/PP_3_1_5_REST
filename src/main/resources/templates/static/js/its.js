// заполнение формы для обновления
document.addEventListener('DOMContentLoaded', function () {
    const editButtons = document.querySelectorAll('.edit-button');

    editButtons.forEach(function (button) {
        button.addEventListener('click', function (event) {
            event.preventDefault();
            const modal = document.getElementById('userFormModal');
            const modalDialog = modal.querySelector('.modal-dialog');
            const href = this.getAttribute('href');

            fetch(href)
                .then(function (response) {
                    if (!response.ok) {
                        throw new Error(response.status);
                    }
                    return response.text();
                })
                .then(function (data) {
                    modalDialog.innerHTML = data;
                    const header = modal.querySelector('.modal-title');
                    header.textContent = 'Edit User';
                    const saveButton = modal.querySelector('.modal-footer .btn-primary');
                    saveButton.textContent = 'Edit';
                    const form = modal.querySelector('form');
                    form.action = '/admin/update';
                    modal.show();
                })
                .catch(function (error) {
                    if (error.message === '404') {
                        window.location.href = '/admin';
                    }
                });
        });
    });
});

// заполнение формы для удаления
document.addEventListener('DOMContentLoaded', function () {
    const deleteButtons = document.querySelectorAll('.delete-button');
    deleteButtons.forEach(function (button) {
        button.addEventListener('click', function (event) {
            event.preventDefault();
            const modal = document.getElementById('userFormModal');
            const modalDialog = modal.querySelector('.modal-dialog');
            const url = this.getAttribute('href');
                        fetch(url)
                .then(function (response) {
                    if (response.status === 404) {
                        window.location.href = '/admin';
                    }
                    return response.text();
                })
                .then(function (html) {
                    modalDialog.innerHTML = html;

                    const header = modal.querySelector('.modal-title');
                    header.textContent = 'Delete User';
                    const passwordDiv = modal.querySelector('#password-div');
                    passwordDiv.remove();
                    const firstNameInput = modal.querySelector('#firstName');
                    firstNameInput.disabled = true;
                    const lastNameInput = modal.querySelector('#surname');
                    lastNameInput.disabled = true;
                    const ageInput = modal.querySelector('#age');
                    ageInput.disabled = true;
                    const emailInput = modal.querySelector('#email');
                    emailInput.disabled = true;
                    const rolesInput = modal.querySelector('#roles');
                    rolesInput.querySelectorAll('option').forEach(function (option) {
                        option.removeAttribute('selected');
                    });
                    rolesInput.disabled = true;
                    const submitButton = modal.querySelector('.modal-footer .btn-primary');
                    submitButton.textContent = 'Delete';
                    submitButton.classList.add('btn-danger');
                    const form = modal.querySelector('form');
                    form.action = '/admin/delete';
                    modal.show();
                })
                .catch(function (error) {
                    console.error('Error:', error);
                });
        });
    });
});


// *********************** - ВАЛЛИДАЦИЯ - *********************** //

// Писал я писал скрипты, а потом прочитал на сайте bootstrap
// We are aware that currently the client-side custom validation styles and tooltips are not accessible,
// since they are not exposed to assistive technologies. While we work on a solution,
// we’d recommend either using the server-side option or the default browser validation method.
document.querySelector('#firstName').addEventListener('blur', validateFirstName);
document.querySelector('#email').addEventListener('blur', validateEmail);
document.querySelector('#password').addEventListener('blur', validatePassword);
document.querySelector('#age').addEventListener('blur', validateAge);

const reSpaces = /^\S*$/;

function validateFirstName() {
    const firstName = document.querySelector('#firstName');
    const re = /^[A-Za-zА-Яа-я .-]{3,30}$/;
    if (re.test(firstName.value) && reSpaces.test(firstName.value)) {
        firstName.classList.remove('is-invalid');
        firstName.classList.add('is-valid');
        return true;
    } else {
        firstName.classList.add('is-invalid');
        firstName.classList.remove('is-valid');
        return false;
    }
}

function validateAge() {
    const ageInput = document.querySelector('#age');
    const age = parseInt(ageInput.value);

    if (age >= 0 && age <= 120) {
        ageInput.setCustomValidity('');
        ageInput.classList.remove('is-invalid');
        ageInput.classList.add('is-valid');
        return true;
    } else {
        ageInput.setCustomValidity('Пожалуйста, введите корректный возраст (от 0 до 120)');
        ageInput.classList.add('is-invalid');
        ageInput.classList.remove('is-valid');
        return false;
    }
}

function validateEmail(e) {
    const email = document.querySelector('#email');
    const re = /^([a-zA-Z0-9_\-?\.?]){3,}@([a-zA-Z]){3,}\.([a-zA-Z]){2,5}$/;

    if (reSpaces.test(email.value) && re.test(email.value)) {
        email.classList.remove('is-invalid');
        email.classList.add('is-valid');
        return true;
    } else {
        email.classList.add('is-invalid');
        email.classList.remove('is-valid');
        return false;
    }
}

function validatePassword() {
    const password = document.querySelector('#password');
    const re = /^.{6,}$/;
    if (re.test(password.value) && reSpaces.test(password.value)) {
        password.classList.remove('is-invalid');
        password.classList.add('is-valid');
        return true;
    } else {
        password.classList.add('is-invalid');
        password.classList.remove('is-valid');
        return false;
    }
}

(function () {
    const forms = document.querySelectorAll('.needs-validation2');

    for (let form of forms) {
        form.addEventListener(
            'submit',
            function (event) {
                if (
                    !form.checkValidity() ||
                    !validateEmail() ||
                    !validateFirstName() ||
                    !validatePassword() ||
                    !validateAge()
                ) {
                    event.preventDefault();
                    event.stopPropagation();
                } else {
                    form.classList.add('was-validated');
                }
            },
            false
        );
    }
})();