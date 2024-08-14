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

    fetch(`/api/file/item/${itemId}`, {
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
        'itemDescribe': document.getElementsByClassName('ql-editor')[0].innerHTML,
        'itemPurchasePrice': Number.parseFloat(document.getElementById('item-coast').value),
    };

    console.log(document.getElementsByClassName('ql-editor')[0].innerHTML);

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

            table.row.add([
                result.itemId,
                result.itemName,
                new Date(result.itemCreationDate).toLocaleDateString("ru-RU", {
                    year: "numeric",
                    month: "long",
                    day: "numeric",
                }),
                result.itemPurchasePrice,
                `<td class="table-action">
                    <a href="/${orgId}/item/${result.itemId}" class="action-icon"> <i class="mdi mdi-eye"></i></a>
                    <a href="javascript:void(0);" class="action-icon" id="${result.itemId}" onClick="deleteItem(this)"> <i class="mdi mdi-delete"></i></a>
                </td>`
            ]).draw();

            addItemModalSuccess.show();
        } else {
            addItemModalWarning.show();
        }
    });
}

function updateItem() {
    let itemId = document.querySelector('#item-id').innerText;

    let itemData = {
        'itemName': document.getElementById('item-name').value,
        'itemDescribe': document.getElementsByClassName('ql-editor')[0].innerHTML,
        'itemPurchasePrice': Number.parseFloat(document.getElementById('item-coast').value),
    };

    fetch(`/${orgId}/api/item/${itemId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json;charset=utf-8'
        },
        body: JSON.stringify(itemData)
    }).then(async response => {
        if (response.ok) location.reload();
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

function addBarcode() {
    let itemId = document.querySelector('#item-id').innerText;
    let barcode = document.querySelector('#item-barcode').value;

    fetch(`/${orgId}/api/item/add_barcode/${itemId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json;charset=utf-8'
        },
        body: JSON.stringify({ codeValue: barcode })
    }).then(async response => {
        if (response.ok) location.reload();
    });
}

function onScanSuccess(decodedText, decodedResult) {
    let scanLabel = document.querySelector('#scan-info');
    let itemId = document.querySelector('#item-id').innerText;

    scanLabel.innerHTML = `
        <div class="alert alert-success" role="alert">
            <strong>Штрих-код - </strong> ${decodedText}
        </div>
    `;

    fetch(`/${orgId}/api/item/add_barcode/${itemId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json;charset=utf-8'
        },
        body: JSON.stringify({ codeValue: decodedText })
    }).then(async response => {
        if (response.ok) location.reload();
    });

    console.log(`Code scanned = ${decodedText}`, decodedResult);
}

let html5QrcodeScanner = new Html5QrcodeScanner("my-qr-reader", { fps: 10, qrbox: 250 });
html5QrcodeScanner.render(onScanSuccess);