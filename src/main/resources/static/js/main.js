  $(document).ready( function () {
        $('#categoryTable').DataTable({
            // Optional: Customize DataTables features
            "paging": true,      // Enable pagination
            "searching": true,   // Enable search box
            "ordering": true,    // Enable column sorting
            "info": true,        // Show "Showing X of Y entries" info
            "lengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"]], // Customize entries per page
            "columnDefs": [
                { "orderable": false, "targets": 4 } // Disable sorting for the "Actions" column (index 4)
            ]
        });
    });