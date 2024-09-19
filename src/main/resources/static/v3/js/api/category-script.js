let categoryForDeletion;

function addCategory() {
    let categoryName = document.querySelector('#categoryName');
    let categoryData = { 'categoryName' : categoryName.value };
    fetch(`/${orgId}/api/category`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json;charset=UTF-8'
        },
        body: JSON.stringify(categoryData)
    }).then(async response => {
        let result = await response.json();

        if (response.status === 200) {
            categoryTable.row.add([
                result.categoryName,
                0,
                `<td class="table-action">
                    <a href="javascript: void(0);" class="action-icon"> <i class="mdi mdi-pencil"></i></a>
                    <a onclick="deleteCategory(this)" class="action-icon" id="category-${result.categoryId}">
                        <i class="mdi mdi-delete"></i>
                    </a>
                </td>`
            ]).draw();

            document.querySelector('.toast-body').innerHTML = `Категория "${categoryName.value}" успешно добавлена`;
            toastBootstrap.show();
        } else {
            document.querySelector('.toast-body').innerHTML = `Товар "${categoryName.value}" не была добавлена`;
            toastBootstrap.show();
        }
    });
}

function deleteCategory(element) {
    categoryForDeletion = element;

    let deleteModal = new bootstrap.Modal('#delete-modal');
    let deleteButton = document.querySelector('#delete-button');
    let deleteText = document.querySelector('#dm-text');

    deleteButton.setAttribute('onclick', `confirmCategoryDelete('${element.id}')`);
    deleteText.innerHTML = 'Вы уверены, что хотите удалить эту категорию?';

    deleteModal.show();
}

function confirmCategoryDelete(categoryId) {
    fetch(
        `/${orgId}/api/category/${categoryId.split('-')[1]}`, { method: 'DELETE' }
    ).then(async response => {
        let result = await response.json();

        if (response.status === 200) {
            categoryTable
                .row(categoryForDeletion.parentNode.parentNode)
                .remove()
                .draw();

            document.querySelector('.toast-body').innerHTML = `Категория "${result.categoryName}" успешно добавлена`;
            toastBootstrap.show();
        } else {
            document.querySelector('.toast-body').innerHTML = `Товар "${result.categoryName}" не была добавлена`;
            toastBootstrap.show();
        }
    });
}
