const orgId = document.querySelector('#org-id').innerHTML;
let addSuccess = new bootstrap.Modal('#add-success');
let addWarning = new bootstrap.Modal('#add-warning');
let itemId = 0;

function addWarehouse() {
    let warehouseData = {
        'warehouseName' : document.querySelector('#warehouse-name').value,
        'warehouseAddress' : document.querySelector('#warehouse-address').value
    };

    fetch(`/${orgId}/api/warehouse`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json;charset=UTF-8'
        },
        body: JSON.stringify(warehouseData)
    }).then(async response => {
        let result = await response;
        if (result.ok) {
            addSuccess.show();
        } else {
            addWarning.show();
        }
    });
}

function findItemsByCategory(param, value) {
    let itemCategory = document.querySelector('#itemCategory').value;
    let itemList = document.querySelector('#itemName');

    fetch(`/${orgId}/api/item/find_by_category?category_name=${itemCategory}`,
        { method: 'GET' }
    ).then(async response => {
        let result = await response.json();
        if (response.ok) {
            itemList.innerHTML = '';
            for (let i = 0; i < result.length; i++) {
                itemList.innerHTML += `<option>${result[i].itemName}</option>`
            }
        }

        param(itemList, value);
    });
}

function addSalesToWarehouse() {
    let itemName = document.querySelector('#itemName').value;
    let itemQuantity = Number.parseInt(document.querySelector('#quantity').value);
    let warehouseId = document.querySelector('.something').innerHTML;

    if (itemQuantity < 0 || isNaN(itemQuantity)) {
        addWarning.show();
        return;
    }

    let requestData = {
        itemName: itemName,
        quantity: itemQuantity
    };

    console.log(requestData);

    fetch(`/${orgId}/api/warehouse/add_sales/${warehouseId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json;charset=UTF-8'
        },
        body: JSON.stringify(requestData)
    }).then(async response => {
        let result = await response;
        if (result.ok) {
            addSuccess.show();
        } else {
            addWarning.show();
        }
    });
}

function addItemsToWarehouse() {
    let itemName = document.querySelector('#itemName').value;
    let itemQuantity = Number.parseInt(document.querySelector('#quantity').value);
    let itemPrice = Number.parseFloat(document.querySelector('#itemPrice').value);
    let warehouseId = document.querySelector('.something').innerHTML;

    if (itemQuantity < 0 || isNaN(itemQuantity)) {
        addWarning.show();
        return;
    }

    let requestData = {
        itemName: itemName,
        itemPrice: itemPrice,
        quantity: itemQuantity
    };

    fetch(`/${orgId}/api/warehouse/add_items/${warehouseId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json;charset=UTF-8'
        },
        body: JSON.stringify(requestData)
    }).then(async response => {
        let result = await response;
        if (result.ok) {
            addSuccess.show();
        } else {
            addWarning.show();
        }
    });
}

function deleteItem(element) {
    let deleteModal = new bootstrap.Modal('#delete-modal');
    itemId = element.id.split('-')[1];

    deleteModal.show();
}

function confirmDelete() {
    let warehouseId = document.querySelector('.something').innerHTML;

    fetch(`/${orgId}/api/warehouse/delete_item/${warehouseId}?item_id=${itemId}`, { method: 'DELETE' }
    ).then(async response => {
        let result = await response;
        if (result.ok) location.reload();
    });
}

function onScanSuccess(decodedText, decodedResult) {
    let itemPrice = document.querySelector('#itemPrice');
    let itemCategory = document.querySelector('#itemCategory');
    let warehouseId = document.querySelector('.something').innerHTML;
    let modalBody = document.querySelector('#add-body');

    fetch(`/${orgId}/api/item/find_by_barcode?barcode=${decodedText}`, {
        method: 'GET'
    }).then(async result => {
        let item = await result.json();

        itemCategory.value = item.categoryModel.categoryName;
        findItemsByCategory((element, value) => {
            element.value = value;
        }, item.itemName);

        fetch(`/${orgId}/api/warehouse/get_item_price/${warehouseId}?item_id=${item.itemId}`, {
            method: 'GET'
        }).then(async result => {
            itemPrice.value = await result.text();
            modalBody.scrollTop = modalBody.scrollHeight;
        });
    });
}

let html5QrcodeScanner = new Html5QrcodeScanner("my-qr-reader", { fps: 10, qrbox: 250 });
html5QrcodeScanner.render(onScanSuccess);
