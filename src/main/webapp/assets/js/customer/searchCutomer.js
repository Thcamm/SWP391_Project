document.getElementById("btnSearch").addEventListener("click", () => {
    const name = document.getElementById("searchName").value;
    const plate = document.getElementById("searchLicensePlate").value;
    const email = document.getElementById("searchEmail").value;
    const sort = document.getElementById("sortOrder").value;

    const tbody = document.getElementById("customerTableBody");
    tbody.innerHTML = "";

    if (data.length === 0) {
        tbody.innerHTML = `<tr><td colspan="5" class="text-center text-muted">Không tìm thấy khách hàng nào</td></tr>`;
    } else {
        data.forEach((c, i) => {
            const row = `
            <tr>
              <td>${i + 1}</td>
              <td>${c.name}</td>
              <td>${c.licensePlate}</td>
              <td>${c.email}</td>
              <td>${c.phone}</td>
            </tr>
          `;
            tbody.insertAdjacentHTML("beforeend", row);
        });
    }
});