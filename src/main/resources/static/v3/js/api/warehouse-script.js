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