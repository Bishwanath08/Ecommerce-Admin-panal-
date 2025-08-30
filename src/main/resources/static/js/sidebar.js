const theme = document.getElementById("theme");

theme.addEventListener("click", () =>
  document.body.classList.toggle("dark-theme")
);

document.addEventListener('DOMContentLoaded', function() {
    // Find all dropdown toggles
    const dropdownToggles = document.querySelectorAll('.sidebar-dropdown > a');

    dropdownToggles.forEach(toggle => {
        toggle.addEventListener('click', function(event) {
            // Prevent the default link behavior if you want to only toggle the dropdown
            // event.preventDefault();

            // Find the parent li.sidebar-dropdown
            const parentLi = this.closest('.sidebar-dropdown');
            if (parentLi) {
                // Toggle the 'active' class on the parent li
                parentLi.classList.toggle('active');

                // Optional: Close other open dropdowns
                document.querySelectorAll('.sidebar-dropdown.active').forEach(otherDropdown => {
                    if (otherDropdown !== parentLi) {
                        otherDropdown.classList.remove('active');
                    }
                });
            }
        });
    });
});
