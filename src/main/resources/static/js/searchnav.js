document.addEventListener('DOMContentLoaded', function() {
    const searchForm = document.getElementById('searchForm');
    const searchInput = document.getElementById('searchInput');

    if (searchForm && searchInput) {
        console.log("Search form and input found");
        searchInput.addEventListener('keypress', function(event) {
            if (event.key === 'Enter') {
                event.preventDefault();
                console.log("Enter key pressed");
                searchForm.submit();
            }
        });
    } else {
        console.error('searchForm or searchInput not found');
    }
});
