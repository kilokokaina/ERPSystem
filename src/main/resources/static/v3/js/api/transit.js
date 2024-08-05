function selectItem(element) {
    let itemId = element.id.split('-')[1];

    if (document.querySelector(`#item-${itemId}`).checked === false) {
        deleteSelected(element);
        return;
    }
    else if(document.querySelector(`#item-${itemId}`).checked) {
        if (document.querySelector(`#ti-${itemId}`) !== null) return;
    }

    let itemName = document.querySelector(`#item-name-${itemId}`).innerText;
    let itemPrice = document.querySelector(`#item-price-${itemId}`).value;
    let itemQuantity = document.querySelector(`#item-quant-${itemId}`).value;

    console.log(itemId + " " + itemName + " " + itemPrice + " " + itemQuantity);

    let transitList = document.querySelector('#tl-body');
    transitList.innerHTML +=
        `<tr id="ti-${itemId}">
            <td id="tin-${itemId}">${itemName}</td>
            <td id="tip-${itemId}">${itemPrice}</td>
            <td id="tiq-${itemId}">${itemQuantity}</td>
            <td>
                <a class="action-icon" id="di-${itemId}" onclick="deleteSelected(this)">
                    <i class="ri-delete-bin-line"></i>
                </a>
            </td>
        </tr>`
    ;
}

function deleteSelected(element) {
    let itemId = element.id.split('-')[1];

    document.querySelector(`#item-${itemId}`).checked = false;
    document.querySelector(`#ti-${itemId}`).remove();
}

function confirmTransit() {
    let tlBody = document.querySelector('#tl-body');
    let items = tlBody.children;

    let departPoint = document.querySelector('#warehouse-id').innerText;
    let arrivePoint = document.querySelector('#arrive-select').value;

    let orgId = document.querySelector('#org-id').innerText;

    let itemList = [];

    for (let  i = 0; i < items.length; i++) {
        let itemId = items.item(i).id.split('-')[1];
        itemList.push({
            itemId: itemId,
            itemName: items.item(i).querySelector(`#tin-${itemId}`).innerText,
            quantity: items.item(i).querySelector(`#tiq-${itemId}`).innerText
        });
    }

    let transitData = {
        status: 'CREATED',
        items: itemList,
        departPoint: departPoint,
        arrivePoint: arrivePoint,
        orgId: orgId
    };

    fetch(`/${orgId}/api/warehouse/add_transit`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json;charset=UTF-8'
        },
        body: JSON.stringify(transitData)
    }).then(async response => {
        if (response.status === 200) location.reload();
    });
}

function showTransit(element) {
    let transitModal = new bootstrap.Modal('#show-transit-modal');
    let orgId = document.querySelector('#org-id').innerText;

    let modalHeader = document.querySelector('#show-transit-modalLabel');
    let transitStatus = document.querySelector('#status-select');
    let depWarehouse = document.querySelector('#dep-warehouse');
    let arrWarehouse = document.querySelector('#arr-warehouse');
    let transitItems = document.querySelector('#transit-items');

    transitItems.innerText = '';

    fetch(`/${orgId}/api/warehouse/get_transit?transitId=${element.id}`, {
        method: 'GET'
    }).then(async result => {
        let transit = await result.json();

        console.log(transit);

        modalHeader.innerText = `Транзит №${transit.transitId} от ${transit.creationDate}`;
        transitStatus.value = transit.transitStatus;
        depWarehouse.innerText = transit.departPoint.warehouseName;
        arrWarehouse.innerText = transit.arrivePoint.warehouseName;

        function itemParser() {

        }

        let test = transit.itemQuantity;

        console.log(test);

        transitModal.show();
    })
}