let addWarehouseSuccess = new bootstrap.Modal('#add-warehouse-success');
let addWarehouseWarning = new bootstrap.Modal('#add-warehouse-warning');

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
            addWarehouseSuccess.show();
        } else {
            addWarehouseWarning.show();
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

function addItemsToWarehouse() {
    let addSupplySuccess = new bootstrap.Modal('#add-supply-success');
    let addSupplyWarning = new bootstrap.Modal('#add-supply-warning');

    let itemName = document.querySelector('#itemName').value;
    let itemQuantity = document.querySelector('#quantity').value;
    let warehouseId = document.querySelector('.something').innerHTML;

    let requestData = {
        itemName: itemName,
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
            addSupplySuccess.show();
        } else {
            addSupplyWarning.show();
        }
    });
}