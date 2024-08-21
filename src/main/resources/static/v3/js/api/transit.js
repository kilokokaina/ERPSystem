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
    let itemPrice = document.querySelector(`#item-price-${itemId}`).innerText;
    let itemQuantity = document.querySelector(`#item-quant-${itemId}`).innerText;

    console.log(itemId + " " + itemName + " " + itemPrice + " " + itemQuantity);

    let transitList = document.querySelector('#tl-body');
    transitList.innerHTML +=
        `<tr id="ti-${itemId}">
            <td id="tin-${itemId}">${itemName}</td>
            <td>
                <input type="number" class="form-control" id="tip-${itemId}" value="${itemPrice}" min="0">
            </td>
            <td>
                <input type="number" class="form-control" id="tiq-${itemId}" value="${itemQuantity}" max="${itemQuantity}" min="0">
            </td>
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
            itemName: items.item(i).querySelector(`#tin-${itemId}`).value,
            quantity: items.item(i).querySelector(`#tiq-${itemId}`).value,
            itemPrice: items.item(i).querySelector(`#tip-${itemId}`).value
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

function itemParser(itemAsString) {
    let result = [];

    itemAsString = itemAsString.replace(/[.*+?^${}()"|[\]\\]/g, '');
    let itemQuantity = itemAsString.split('ItemModel');

    for (let i = 1; i < itemQuantity.length; i++) {
        let quantity = itemQuantity[i].split(':');
        let itemName = itemQuantity[i].split('itemName=')[1].split(',')[0];
        let itemId = itemQuantity[i].split('itemId=')[1].split(',')[0];
        quantity = quantity[quantity.length - 1].replace(',', '');

        result.push({
            itemId: itemId,
            itemName: itemName,
            quantity: quantity
        });
    }

    return result;
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

        transitStatus.setAttribute('onchange', `changeTransitStatus(${transit.transitId})`);
        switch (transit.transitStatus) {
            case 'CREATED': {
                transitStatus.innerHTML = `
                    <option selected value="CREATED">СОЗДАН</option>
                    <option value="IN_TRANSIT">В ПУТИ</option>
                `;
                break;
            }
            case 'IN_TRANSIT': {
                transitStatus.innerHTML = `
                    <option selected value="IN_TRANSIT">В ПУТИ</option>
                    <option value="DELIVERED">ДОСТАВЛЕН</option>
                `;
                break;
            }
            case 'DELIVERED': {
                transitStatus.innerHTML = `
                    <option selected value="DELIVERED">ДОСТАВЛЕН</option>
                `;
                break;
            }
        }

        modalHeader.innerText = `Транзит №${transit.transitId} от ${transit.creationDate}`;
        depWarehouse.innerHTML =
            `<h5 class="card-title">${transit.departPoint.warehouseName}</h5>
            <h6 class="card-subtitle text-muted">Пункт отправления</h6>
            `;
        arrWarehouse.innerHTML =
            `<h5 class="card-title">${transit.arrivePoint.warehouseName}</h5>
            <h6 class="card-subtitle text-muted">Пункт назначения</h6>
            `;

        let itemList = itemParser(JSON.stringify(transit.itemQuantity));
        for (let i = 0; i < itemList.length; i++) {
            transitItems.innerHTML +=
                `<tr>
                    <td>${i + 1}</td>
                    <td>${itemList[i].itemName}</td>
                    <td>${itemList[i].quantity}</td>
                    <td>
                        <a href="/${orgId}/item/${itemList[i].itemId}" class="action-icon">
                            <i class="mdi mdi-eye"></i>
                        </a>
                    </td>
                </td>
                `
        }

        transitModal.show();
    })
}

function changeTransitStatus(transitId) {
    let newStatus = document.querySelector('#status-select').value;
    let orgId = document.querySelector('#org-id').innerText;

    fetch(`/${orgId}/api/warehouse/change_transit_status?transitId=${transitId}&transitStatus=${newStatus}`, {
        method: 'PUT'
    }).then(async response => {
        if (response.ok) location.reload();
    });
}