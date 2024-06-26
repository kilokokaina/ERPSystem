let orgId = 0;

function createOrganization() {
    let orgName = document.querySelector('#org-name').value;
    let orgAddress = document.querySelector('#address').value;

    let orgData = {
        orgName: orgName,
        orgAddress: orgAddress
    };

    fetch('/43/api/org', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(orgData)
    }).then(async response => {
        let payload = await response.json();
        orgId = payload.orgId;

        console.log(orgId);
    })
}
