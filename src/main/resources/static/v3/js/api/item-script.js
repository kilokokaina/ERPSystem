let addItemModalSuccess = new bootstrap.Modal('#multiple-two');
let addItemModalWarning = new bootstrap.Modal('#warning-alert-modal');
const orgId = document.querySelector('#org-id').innerHTML;

function uploadImage(itemId) {
    const files = document.querySelector('#papers-list').files;
    const formData = new FormData();

    console.log(files);
    console.log(files.length);
    for (let i = 0; i < files.length; i++) {
        formData.append('images', files[i]);
    }

    fetch(`/api/file/${itemId}`, {
        method: 'POST',
        body: formData
    });
}

function updateList() {
    const files = document.querySelector('#papers-list').files;
    const filesList = document.querySelector('#files-list');

    filesList.innerHTML = '';
    for (let i = 0; i < files.length; i++) {
        filesList.innerHTML += `<h5 id="file-${i}"><span class="badge badge-outline-warning">Готов к загрузке</span> ${files[i].name}</h5>`
    }
}

function addItem() {
    let itemData = {
        'itemName': document.getElementById('item-name').value,
        'categoryName': document.getElementById('item-category').value,
        'itemPurchasePrice': Number.parseFloat(document.getElementById('item-coast').value),
    };

    console.log(document.querySelector('#snow-editor').innerHTML);

    fetch(`/${orgId}/api/item`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json;charset=utf-8'
        },
        body: JSON.stringify(itemData)
    }).then(async response => {
        let result = await response.json();

        if (response.ok) {
            uploadImage(result.itemId);

            addItemModalSuccess.show();
            console.log(itemData);
        } else {
            addItemModalWarning.show();
        }
    });
}

function deleteItem(element) {
    let deleteModal = new bootstrap.Modal('#delete-modal');
    let deleteButton = document.querySelector('#delete-button');
    deleteButton.setAttribute('onclick', `confirmItemDelete(${element.id})`);

    deleteModal.show();
}

function confirmItemDelete(itemId) {
    fetch(
        `/${orgId}/api/item/${itemId}`, { method: 'DELETE' }
    ).then(async response => {
        let result = await response;
        if (result.ok) location.reload();
    });
}