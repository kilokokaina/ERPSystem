let deleteModal = new bootstrap.Modal('#delete-modal');

function addCategory() {
    let categoryName = document.querySelector('#categoryName');
    let categoryData = { 'categoryName' : categoryName.value };
    fetch(`/${orgId}/api/category`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json;charset=UTF-8'
        },
        body: JSON.stringify(categoryData)
    }).then(async response => {
        let result = await response;
        if (result.ok) {
            addSuccess.show();
        } else {
            addWarning.show();
        }
    });
}

function showDeleteModal(type, id) {
    let deleteText = document.querySelector('#dm-text');
    let deleteButton = document.querySelector('#delete-button');

    switch (type) {
        case 'category':
            deleteText.innerHTML = 'Are you sure that you want to delete this category?';
            deleteButton.setAttribute('onclick', `confirmDelete('${type}', ${id})`);
            break;
        case 'warehouse':
            deleteText.innerHTML = 'Are you sure that you want to delete this warehouse?';
            deleteButton.setAttribute('onclick', `confirmDelete('${type}', ${id})`);
            break;
        case 'employee':
            deleteText.innerHTML = 'Are you sure that you want to delete this employee?';
            deleteButton.setAttribute('onclick', `confirmDelete('${type}', ${id})`);
            break;
        default:
            addWarning.show();
            break;
    }

    deleteModal.show();
}

async function confirmDelete(type, id) {
    let response;
    switch (type) {
        case 'category':
            response = await fetch(`/${orgId}/api/category/${id}`, { method: 'DELETE' });
            break;
        case 'warehouse':
            response = await fetch(`/${orgId}/api/warehouse/${id}`, { method: 'DELETE' });
            break;
        case 'employee':
            response = await fetch(`/${orgId}/api/user/${id}`, { method: 'DELETE' });
            break;
        default:
            addWarning.show();
            break;
    }

    if (response.ok) location.reload();
}
