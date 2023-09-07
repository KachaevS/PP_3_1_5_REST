$(async function () {
    await getCurrentUser();
});

async function getCurrentUser() {
    let temp = '';
    const userTable = document.querySelector('#userTableBody');

    try {
        const response = await fetch('user/api/current');
        const user = await response.json();

        temp += `
                  <tr class="border-top bg-light">
                      <td class="align-middle">${user.id}</td>
                      <td class="align-middle">${user.username}</td>
                  
                      <td class="align-middle">${user.email}</td>
                      <td class="align-middle">${user.roles.map(role => role.name)}</td>
                  </tr>
                `;
        userTable.innerHTML = temp;
    } catch (error) {
        console.log(error.toString())
    }
}
