function addUser() {
    let userEmail = document.querySelector('#email').value;
    let firstName = document.querySelector('#first-name').value;
    let secondName = document.querySelector('#second-name').value;
    let userPost = document.querySelector('#post').value;

    let userData = {
        username: userEmail,
        firstName: firstName,
        secondName: secondName,
        post: userPost
    };

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