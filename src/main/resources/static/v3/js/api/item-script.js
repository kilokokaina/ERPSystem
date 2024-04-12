let itemId = 0;
let addItemModalSuccess = new bootstrap.Modal('#multiple-two');
let addItemModalWarning = new bootstrap.Modal('#warning-alert-modal');

function addItem() {
    let itemData = {
        'itemName': document.getElementById('itemName').value,
        'categoryName': document.getElementById('itemCategory').value,
        'itemPurchasePrice': Number.parseFloat(document.getElementById('itemPurchase').value),
        'itemSalePrice': Number.parseFloat(document.getElementById('itemSale').value)
    };

    fetch('api/item', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json;charset=utf-8'
        },
        body: JSON.stringify(itemData)
    }).then(async response => {
        let result = await response;
        if (result.ok) {
            addItemModalSuccess.show();
            console.log(itemData);
        } else {
            addItemModalWarning.show();
        }
    });
}

function deleteItem(element) {
    let deleteModal = new bootstrap.Modal('#delete-modal');
    itemId = element.id.split('-')[1];

    deleteModal.show();
}

function confirmDelete() {
    fetch(
        `api/item/${itemId}`, { method: 'DELETE' }
    ).then(async response => {
        let result = await response;
        if (result.ok) location.reload();
    });
}