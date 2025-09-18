  document.addEventListener("DOMContentLoaded", () => {
    const modal = document.getElementById("orderItemsModal");
    const modalOrderNumber = document.getElementById("modalOrderNumber");
    const orderItemsTableBody = document.getElementById("orderItemsTableBody");

    // Listen for button clicks
    document.querySelectorAll(".view-items-btn").forEach(button => {
        button.addEventListener("click", async () => {
            const orderId = button.getAttribute("data-order-id");
            const orderNumber = button.getAttribute("data-order-number");

            // Set modal title
            modalOrderNumber.textContent = orderNumber;

            // Clear old data
            orderItemsTableBody.innerHTML = "<tr><td colspan='3'>Loading...</td></tr>";

            try {
                const response = await fetch(`/customer/orders/${orderId}/items`);
                if (!response.ok) throw new Error("Failed to fetch items");

                const items = await response.json();

                if (items.length === 0) {
                    orderItemsTableBody.innerHTML = "<tr><td colspan='3'>No items found</td></tr>";
                } else {
                    orderItemsTableBody.innerHTML = items.map(item => `
                        <tr>
                            <td>${item.image}</td>
                            <td>${item.productName}</td>
                            <td>${item.quantity}</td>
                            <td>${item.price}</td>
                        </tr>
                    `).join("");
                }
            } catch (error) {
                orderItemsTableBody.innerHTML = `<tr><td colspan='3'>Error loading items</td></tr>`;
                console.error("Error fetching order items:", error);
            }
        });
    });
});
