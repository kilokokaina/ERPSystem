let employeeForDeletion;

function addUser() {
    let userData = prepareUserData();

    fetch(`/${orgId}/api/user`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json;charset=UTF-8'
        },
        body: JSON.stringify(userData)
    }).then(async response => {
        let result = await response;
        if (result.ok) {
            addSuccess.show();
        } else {
            addWarning.show();
        }
    });
}

function addUserFromOrgPage() {
    let userData = prepareUserData();

    fetch(`/${orgId}/api/user`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json;charset=UTF-8'
        },
        body: JSON.stringify(userData)
    }).then(async response => {
        let result = await response.json();
        if (response.ok) {
            updateUserTable(result, userData.userAuthority);
            addSuccess.show();
        } else {
            addWarning.show();
        }
    });
}

function findUser(input) {
    let inputValue = input.value;

    if (inputValue.includes("@")) {
        fetch(`/api/user?email=${inputValue}`, {
            method: 'GET'
        }).then(async response => {
            if (response.status === 200) {
                let payload = await response.json();

                document.querySelector('#first-name').setAttribute('disabled', '');
                document.querySelector('#second-name').setAttribute('disabled', '');
                document.querySelector('#post').setAttribute('disabled', '');
                document.querySelector('#user-info').innerHTML =
                    'Этот пользователь уже работает в ERPSystem ' +
                    '<span type="button" class="badge badge-success-lighten" ' +
                    `data-bs-dismiss="modal" onclick="inviteUser('${payload.userId}')"` +
                    '>Пригласть</span>';
            }
        });
    }

    document.querySelector('#first-name').removeAttribute('disabled');
    document.querySelector('#second-name').removeAttribute('disabled');
    document.querySelector('#post').removeAttribute('disabled');
    document.querySelector('#user-info').innerHTML = '';

}

function inviteUser(userId) {
    let authElement = document.querySelector('#user-authority');
    let userAuthority = authElement.options[authElement.selectedIndex].value;

    fetch(`/${orgId}/api/user/invite?user_id=${userId}&role=${userAuthority}`, {
        method: 'GET',
    }).then(async response => {
        let result = await response.json();

        if (response.ok) {
            updateUserTable(result, userAuthority);
            addSuccess.show();
        } else {
            addWarning.show();
        }
    });
}

function uploadImage(userId) {
    const files = document.querySelector('#papers-list').files;
    const formData = new FormData();

    console.log(files);
    console.log(files.length);
    for (let i = 0; i < files.length; i++) {
        formData.append('image', files[i]);
    }

    fetch(`/api/file/user/${userId}`, {
        method: 'POST',
        body: formData
    });
}

function updateUser() {
    let userId = document.querySelector('#user-id').innerHTML;
    let email = document.querySelector('#email').value;
    let firstName = document.querySelector('#first-name').value;
    let secondName = document.querySelector('#second-name').value;

    let userData = {
        email: email,
        firstName: firstName,
        secondName: secondName,
    };

    fetch(`/api/user/update/${userId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(userData)
    }).then(async response => {
        let payload = response.text();

        if (response.ok) {
            uploadImage(userId);
            location.reload();
        }
    })

}

function fireEmployee(element) {
    employeeForDeletion = element;

    let deleteModal = new bootstrap.Modal('#delete-modal');
    let deleteButton = document.querySelector('#delete-button');
    let deleteText = document.querySelector('#dm-text');

    deleteButton.setAttribute('onclick', `confirmFireEmployee('${element.id}')`);
    deleteText.innerHTML = 'Вы уверены, что хотите удалить этого сорудника?';

    deleteModal.show();
}

function confirmFireEmployee(userId) {
    fetch(
        `/${orgId}/api/user/fire/${userId.split('-')[1]}`, { method: 'DELETE' }
    ).then(async response => {
        let result = await response;
        if (result.ok) {
            employeeTable
                .row(employeeForDeletion.parentNode.parentNode)
                .remove()
                .draw();

            let employeeCount = Number.parseInt(document.querySelector('#employee-count').innerText);
            document.querySelector('#employee-count').innerText = --employeeCount;

            addSuccess.show();
        } else {
            addWarning.show();
        }
    });
}

function updateUserTable(result, userAuthority) {
    employeeTable.row.add([
        `<td class="table-user">
                    <img src="/v3/images/users/avatar-2.jpg" alt="table-user" width="30" height="30" class="me-2 rounded-circle">
                </td>`,
        result.firstName + ' ' + result.secondName,
        result.username,
        userAuthority,
        '1111',
        `<td class="table-action">
                    <a onclick="fireEmployee(this)" href="javascript: void(0);" class="action-icon" id="user-${result.userId}">
                        <i class="mdi mdi-delete"></i>
                    </a>
                </td>`
    ]).draw();

    let employeeCount = Number.parseInt(document.querySelector('#employee-count').innerText);
    document.querySelector('#employee-count').innerText = ++employeeCount;
}

function prepareUserData() {
    let email = document.querySelector('#email').value;
    let firstName = document.querySelector('#first-name').value;
    let secondName = document.querySelector('#second-name').value;
    let userPost = document.querySelector('#post').value;
    let authElement = document.querySelector('#user-authority');
    let userAuthority = authElement.options[authElement.selectedIndex].value;

    return {
        email: email,
        firstName: firstName,
        secondName: secondName,
        userAuthority: userAuthority,
        post: userPost
    };
}
