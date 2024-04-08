function addItem() {
    // let addItemModal = new bootstrap.Modal('#multiple-one');
    let addItemModalSuccess = new bootstrap.Modal('#multiple-two');

    let itemData = {
        'itemName': document.getElementById('itemName').value,
        'categoryName': document.getElementById('itemCategory').value,
        'itemPurchasePrice': Number.parseFloat(document.getElementById('itemPurchase').value),
        'itemSalePrice': Number.parseFloat(document.getElementById('itemSale').value)
    }

    fetch('api/item', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json;charset=utf-8'
        },
        body: JSON.stringify(itemData)
    });

    addItemModalSuccess.show();
    console.log(itemData);
}

function deleteItem(element) {
    // let deleteModal = new bootstrap.Modal('#delete-modal');
    let itemId = element.id.split('-')[1];
    fetch(`api/item/${itemId}`, { method: 'DELETE' });
}