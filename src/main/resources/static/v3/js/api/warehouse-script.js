let addSuccess = new bootstrap.Modal('#add-success');
let addWarning = new bootstrap.Modal('#add-warning');
let itemId = 0;

function addWarehouse() {
    let warehouseData = {
        'warehouseName' : document.querySelector('#warehouse-name').value,
        'warehouseAddress' : document.querySelector('#warehouse-address').value
    };

    fetch('/api/warehouse', {
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

function findItemsByCategory() {
    let itemCategory = document.querySelector('#itemCategory').value;
    let itemList = document.querySelector('#itemName');

    fetch(`/api/item/find_by_category?category_name=${itemCategory}`,
        { method: 'GET' }
    ).then(async response => {
        let result = await response.json();
        if (response.ok) {
            itemList.innerHTML = '';
            for (let i = 0; i < result.length; i++) {
                itemList.innerHTML += `<option>${result[i].itemName}</option>`
            }
        }
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

    fetch(`/api/warehouse/add_sales/${warehouseId}`, {
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

    fetch(`/api/warehouse/add_items/${warehouseId}`, {
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

    fetch(`/api/warehouse/delete_item/${warehouseId}?item_id=${itemId}`, { method: 'DELETE' }
    ).then(async response => {
        let result = await response;
        if (result.ok) location.reload();
    });
}