function addCategory() {
    let categoryName = document.querySelector('#category-input');
    let categoryData = { 'categoryName' : categoryName.value };
    fetch('api/category', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json;charset=UTF-8'
        },
        body: JSON.stringify(categoryData)
    });
}