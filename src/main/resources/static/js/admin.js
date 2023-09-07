const headers = new Headers();
headers.append('Content-Type', 'application/json; charset=utf-8');
let modalActionButtonType = ''

$(async function () {
    await getAdminPanel();
    await getModalForm();
    await fillNewUserForm();
    await createUser();
})

async function getAdminPanel() {
    try {
        let temp = '';
        const usersTable = document.querySelector('#allUsersTable');
        const response = await fetch('/admin/api/users');
        const users = await response.json();

        users.forEach(user => {
            temp += `
                <tr class="border-top bg-light">
                  <td class="align-middle">${user.id}</td>
                  <td class="align-middle">${user.username}</td>
          
                  <td class="align-middle">${user.email}</td>
                  <td class="align-middle">${user.roles.map(role => role.name)}</td>
                  <td class="align-middle text-center">
                    <button type="button" class="btn edit-button" data-action="Edit" data-id="${user.id}" data-target="#editModal"> Edit </button>
                  </td>
                  <td class="align-middle text-center">
                    <button type="button" class="btn btn-danger" data-action="Delete" data-id="${user.id}" data-target="#deleteModal"> Delete </button>
                  </td>
                </tr>
            `;
        });
        usersTable.innerHTML = temp;

        $("#allUsersTable").find('button').on('click', (event) => {
            let modalForm = $('#userFormModal');
            let target = $(event.target);
            let buttonUserId = target.attr('data-id');
            let buttonAction = target.attr('data-action');

            modalActionButtonType = buttonAction.toString()

            modalForm.attr('data-id', buttonUserId);
            modalForm.attr('data-action', buttonAction);
            modalForm.modal('show');
        });
    } catch (error) {
        console.log(error.toString());
    }
}

async function getModalForm() {
    $('#userFormModal').modal('hide')
        .on("show.bs.modal", (event) => {
            const thisModal = $(event.target);
            const id = thisModal.attr('data-id');
            const action = thisModal.attr('data-action');
            createModalForm(thisModal, id)
        }).on("hidden.bs.modal", (e) => {
        const thisModal = $(e.target);
        thisModal.find('.modal-title').empty();
        thisModal.find('.modal-body').empty();
        thisModal.find('.modal-footer').empty();
    })
}

async function createModalForm(modal, id) {
    const submitButton = `<button  class="btn ${modalActionButtonType === 'Delete' ? 'btn-danger' : 'btn-primary'}" id="modalSubmitButton">${modalActionButtonType}</button>`;
    const closeButton = `<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>`;

    modal.find('#userFormModalLabel').html(modalActionButtonType + ' user');
    modal.find('.modal-footer').append(closeButton);
    modal.find('.modal-footer').append(submitButton);

    try {
        const [userResponse, roleResponse] = await Promise.all([
            fetch('/admin/api/users/' + id),
            fetch('/admin/api/roles')
        ]);
        const user = await userResponse.json();
        const roles = await roleResponse.json();
        const form = fillModalForm(user,  roles)
        await modal.find('.modal-body').append(form);
    } catch (error) {
        console.log(error.toString())
    }

    $("#modalSubmitButton").on('click', async () => {
        let updateUser = {
            'id': modal.find('#editUserId').val(),
            'username': modal.find('#editUserName').val(),
            'email': modal.find('#editUserEmail').val(),
            'password': modal.find('#editUserPassword').val(),
            'roles': modal.find('#editUserRoles').val().map((role, index) => ({id: role, name: null}))
        }

        if (modalActionButtonType === 'Delete') {
            if (await deleteUser(updateUser.id)) {
                modal.modal('hide');

            }
        } else if (modalActionButtonType === 'Edit') {
            if (await updateData(updateUser)) {
                modal.modal('hide');
            }
        }


        async function deleteUser(id) {
            try {
                const response = await fetch(`/admin/api/users/${id}`, { method: 'DELETE' });
                if (response.ok) {
                    await getAdminPanel();
                    console.log('Пользователь успешно удален');
                    return true;
                } else {
                    console.log('Ошибка при удалении пользователя');
                    return false;
                }
            } catch (error) {
                console.log('Ошибка при удалении пользователя:', error.toString());
                return false;
            }
        }

        async function updateData(user) {
            try {
                const response = await fetch(`/admin/api/users/${user.id}`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(user)
                });

                if (response.ok) {
                    await getAdminPanel();
                    console.log('Пользователь успешно обновлен');
                    return true;
                } else {
                    if (response.status === 400) {
                        const errorMessage = await response.text();
                        alert (errorMessage)
                    } else {
                        console.log('Ошибка при обновлении пользователя');
                    }
                    return false;
                }
            } catch (error) {
                console.log('Ошибка при обновлении пользователя:', error.toString());
                return false;
            }
        }


    });
}

function fillModalForm(userData, rolesData) {
    const ifDelete = modalActionButtonType === 'Delete' ? 'disabled' : '';

    return `
            <form role="form" id="userFormModal-form">
                <div class="container col-6 text-center">
                    <div class="m-3">
                        <label for="editUserId" class="bold-form-label">ID</label>
                        <input type="text" class="form-control" id="editUserId" value="${userData.id}" disabled/>
                    </div>
                    
                    <div class="m-3">
                        <label for="editUserName" class="bold-form-label">Username</label>
                        <input type="text" class="form-control" id="editUserName" value="${userData.username}" ${ifDelete}/>
                        <div style="color: red" class="error-message" id="editUserNameError"></div>
                    </div>
        
                    <div class="m-3">
                        <label for="editUserEmail" class="bold-form-label">Email</label>
                        <input type="text" class="form-control" id="editUserEmail" value="${userData.email}" ${ifDelete}/>
                        <div style="color: red" class="error-message" id="editUserEmailError"></div>
                    </div>
                    ${modalActionButtonType !== 'Delete' ? `
                        <div class="m-3">
                            <label for="editUserPassword" class="bold-form-label">Password</label>
                            <input type="password" class="form-control" id="editUserPassword" name="editPassword" value=""/>
                            <div style="color: red" class="error-message" id="editUserPasswordError"></div>
                        </div>
                    ` : ''}
                    <div class="m-3">
                        <label for="editUserRoles" class="bold-form-label">Role</label>
                        <select multiple class="form-select" size="${rolesData.length}" id="editUserRoles" ${ifDelete}>
                            ${rolesData.map(role => `
                                <option value="${role.id}" ${userData.roles.some(r => r.id === role.id) ? 'selected' : ''}>
                                    ${role.name}
                                </option>
                            `).join('')}
                        </select>
                        <div style="color: red" class="error-message" id="editUserRolesError"></div>
                    </div>
                </div>
            </form>
    `;
}

async function fillNewUserForm() {
    const form = document.querySelector('#newUserRoles-div')

    try {
        const response = await fetch('/admin/api/roles');
        const roles = await response.json();

        form.innerHTML = `
                <label for="newUserRoles" class="bold-form-label">Role</label>
                <select multiple class="form-select" size="${roles.length}" id="newUserRoles">
                    ${roles.map(role => `
                        <option value="${role.id}">
                            ${role.name}
                        </option>
                    `).join('')}
                </select>
                <div style="color: red" class="error-message" id="newUserRolesError"></div>
        `;

    } catch (error) {
        console.log(error.toString())
    }
}

async function createUser() {
    $('#createNewUser').click(async () => {
        let newUserForm = $('#newUserForm');

        let user = {
            'username': newUserForm.find('#newUserName').val(),
            'email': newUserForm.find('#newUserEmail').val(),
            'password': newUserForm.find('#newUserPassword').val(),
            'roles': newUserForm.find('#newUserRoles').val().map((role, index) => ({id: role, name: null}))
        };

        if (await fetchData(user, 'new')) {
            newUserForm.find('#newUserName').val('');
            newUserForm.find('#newUserEmail').val('');
            newUserForm.find('#newUserPassword').val('');
            newUserForm.find('#newUserRoles').val([]);
            await getAdminPanel();
            $('.nav-tabs button[data-bs-target="#usersTable"]').tab('show');
        }
    });
}

async function fetchData(data, requestType) {
    try {
        const response = await fetch("/admin/api/users/", {
            method: 'POST',
            headers: headers,
            body: JSON.stringify(data)
        });

        if (response.ok) {
            await getAdminPanel();
            return true;
        } else if (response.status === 400) {
            const errorsData = await response.text();
            if (errorsData === "Пользователь с таким именем уже существует") {
                alert(errorsData)
            } else {
                checkValidationErrors(errorsData, requestType);
            }
            return false;
        } else {
            console.log("Error getting data");
            return false;
        }
    } catch (error) {
        console.log(error.toString());
    }
}

function checkValidationErrors(data, type) {
    let errorMessages;

    if (Array.isArray(data)) {
        errorMessages = data;
    } else if (typeof data === 'object' && data.errors && Array.isArray(data.errors)) {
        errorMessages = data.errors.map(error => error.defaultMessage);
    } else {
        return;
    }

    $('.is-invalid').removeClass('is-invalid');
    $('.error-message').empty();

    errorMessages.forEach(msg => {
        if (msg.includes("Name")) {
            $(`#${type}UserName`).addClass('is-invalid');
            $(`#${type}UserNameError`).append(`<div>${msg}</div>`);
        }
        if (msg.includes("Surname")) {
            $(`#${type}UserName`).addClass('is-invalid');
            $(`#${type}UserSurnameError`).append(`<div>${msg}</div>`);
        }
        if (msg.includes("Age")) {
            $(`#${type}UserAge`).addClass('is-invalid');
            $(`#${type}UserAgeError`).append(`<div>${msg}</div>`);
        }
        if (msg.includes("mail")) {
            $(`#${type}UserEmail`).addClass('is-invalid');
            $(`#${type}UserEmailError`).append(`<div>${msg}</div>`);
        }
        if (msg.includes("Password")) {
            $(`#${type}UserPassword`).addClass('is-invalid');
            $(`#${type}UserPasswordError`).append(`<div>${msg}</div>`);
        }
        if (msg.includes("role")) {
            $(`#${type}UserRoles`).addClass('is-invalid');
            $(`#${type}UserRolesError`).append(`<div>${msg}</div>`);
        }
    })




}