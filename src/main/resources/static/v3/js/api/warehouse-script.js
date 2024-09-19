const orgId = document.querySelector('#org-id').innerHTML;
let itemId = 0;

let warehouseForDeletion;
let itemForDeletion;

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
        let result = await response.json();

        if (response.status === 200) {
            storageTable.row.add([
                result.warehouseName,
                result.warehouseAddress,
                0,
                `<td class="table-action">
                    <a href="javascript: void(0);" class="action-icon"> <i class="mdi mdi-pencil"></i></a>
                    <a onclick="deleteWarehouse(this)" class="action-icon" id="warehouse-${result.warehouseId}">
                        <i class="mdi mdi-delete"></i>
                    </a>
                </td>`
            ]).draw();

            document.querySelector('.toast-body').innerHTML = `Склад "${result.warehouseName}" успешно создан`;
            toastBootstrap.show();
        } else {
            document.querySelector('.toast-body').innerHTML = `Склад "${result.warehouseName}" не был создан`;
            toastBootstrap.show();
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

    fetch(`/${orgId}/api/warehouse/add_sales/${warehouseId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json;charset=UTF-8'
        },
        body: JSON.stringify(requestData)
    }).then(async response => {
        let result = await response.json();

        if (response.status === 200) {
            saleTable.row.add([
                result.item.itemName,
                result.item.categoryModel.categoryName,
                new Date(result.saleDate).toLocaleString("ru-RU"),
                result.itemSaleQuantity,
            ]).draw();

            document.querySelector('.toast-body').innerHTML = `Продажа товара "${result.item.itemName}" была зафиксирована`;
            toastBootstrap.show();
        } else {
            document.querySelector('.toast-body').innerHTML = `Продажа товара "${result.item.itemName}" не была зафиксирована`;
            toastBootstrap.show();
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
        let result = await response.json();

        let itemQuantity = result.itemQuantity;
        let itemPrice = result.itemPrice;

        let storage = itemParser(JSON.stringify(itemPrice), JSON.stringify(itemQuantity), requestData.itemName);

        if (response.status === 200) {
            storageTable.row.add([
                storage.itemName,
                storage.itemCategory,
                storage.itemPurchasePrice,
                storage.price,
                storage.quantity,
                `<td class="table-action">
                    <a class="action-icon" id="${storage.itemId}" onclick="deleteItem(this)">
                        <i class="ri-delete-bin-line"></i>
                    </a>
                </td>`
            ]).draw();

            document.querySelector('.toast-body').innerHTML = `Товар "${storage.itemName}" был добавлен на склад`;
            toastBootstrap.show();
        } else {
            document.querySelector('.toast-body').innerHTML = `Товар "${storage.itemName}" не был добавлен на склад`;
            toastBootstrap.show();
        }
    });
}

function itemParser(itemPrice, itemQuantity, searchName) {
    let result;

    itemPrice = itemPrice.replace(/[*+?^${}()"|[\]\\]/g, '');
    itemQuantity = itemQuantity.replace(/[*+?^${}()"|[\]\\]/g, '');

    itemPrice = itemPrice.split('ItemModel');
    itemQuantity = itemQuantity.split('ItemModel');

    for (let i = 1; i < itemPrice.length; i++) {
        let itemName = itemPrice[i].split('itemName=')[1].split(',')[0];

        if (itemName === searchName) {
            let quantity = itemQuantity[i].split(':');
            let price = itemPrice[i].split(':');

            let itemId = itemQuantity[i].split('itemId=')[1].split(',')[0];
            let itemCategory = itemQuantity[i].split('categoryName=')[1].split(',')[0];
            let itemPurchasePrice = itemQuantity[i].split('itemPurchasePrice=')[1].split(':')[0];

            quantity = quantity[quantity.length - 1].replace(',', '');
            price = price[price.length - 1].replace(',', '');

            result = {
                itemId: itemId,
                itemName: itemName,
                itemCategory: itemCategory,
                itemPurchasePrice: itemPurchasePrice,
                quantity: quantity,
                price: price
            };
        }
    }

    return result;
}

function deleteItem(element) {
    itemForDeletion = element;
    let deleteModal = new bootstrap.Modal('#delete-modal');
    let deleteButton = document.querySelector('#delete-button');
    deleteButton.setAttribute('onclick', `confirmItemDelete('${element.id}')`);

    deleteModal.show();
}

function confirmItemDelete(itemId) {
    let warehouseId = document.querySelector('.something').innerHTML;

    fetch(`/${orgId}/api/warehouse/delete_item/${warehouseId}?item_id=${itemId}`, { method: 'DELETE' }
    ).then(async response => {
        let result = await response.json();

        if (response.status === 200) {
            storageTable
                .row(itemForDeletion.parentNode.parentNode)
                .remove()
                .draw();

            document.querySelector('.toast-body').innerHTML = `Товар "${result.itemName}" был удален со склада`;
            toastBootstrap.show();
        } else {
            document.querySelector('.toast-body').innerHTML = `Товар "${result.itemName}" не был удален со склада`;
            toastBootstrap.show();
        }
    });
}

function deleteWarehouse(element) {
    warehouseForDeletion = element;

    let deleteModal = new bootstrap.Modal('#delete-modal');
    let deleteButton = document.querySelector('#delete-button');
    let deleteText = document.querySelector('#dm-text');

    deleteButton.setAttribute('onclick', `confirmWarehouseDelete('${element.id}')`);
    deleteText.innerHTML = 'Вы уверены, что хотите удалить этот склад?';

    deleteModal.show();
}

function confirmWarehouseDelete(warehouseId) {
    fetch(
        `/${orgId}/api/warehouse/${warehouseId.split('-')[1]}`, { method: 'DELETE' }
    ).then(async response => {
        let result = await response.json();

        if (response.status === 200) {
            storageTable
                .row(warehouseForDeletion.parentNode.parentNode)
                .remove()
                .draw();

            document.querySelector('.toast-body').innerHTML = `Склад "${result.warehouseName}" был удален`;
            toastBootstrap.show();
        } else {
            document.querySelector('.toast-body').innerHTML = `Склад "${result.warehouseName}" не был удален`;
            toastBootstrap.show();
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
        if (response.status === 200) {
            itemList.innerHTML = '';
            for (let i = 0; i < result.length; i++) {
                itemList.innerHTML += `<option>${result[i].itemName}</option>`
            }
        }

        param(itemList, value);
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
