Behaviour.specify("#sidebar-link-upload-button", "sidebar-link-upload", 0, function(e) {
    e.onclick = async function() {
        let formData = new FormData();
        const fileField = document.querySelector('input[type="file"]');
        if (fileField.files.length == 0) {
            notificationBar.show("No file chosen.", notificationBar.WARNING);
        } else {
            const url = e.getAttribute("data-url");
            formData.append("linkimage.file", fileField.files[0]);
            const result = await fetch(url, {
                method: "POST",
                headers: crumb.wrap({}),
                body: formData
            });
            const text = await result.text();
            if (result.ok) {
                let success = {...notificationBar.SUCCESS};
                success.sticky = true;
                notificationBar.show(text, success);
            } else {
                notificationBar.show(text, notificationBar.ERROR);
            }
        }
    }
});
