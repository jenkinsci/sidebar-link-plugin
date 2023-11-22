Behaviour.specify(".sidebar-link__file-upload", "sidebar-link-upload", 0, function(element) {
    const button = element.querySelector('.sidebar-link__file-upload-button');
    const noFileMessage = button.dataset.noFile;
    button.onclick = async function() {
        const fileField = element.querySelector('.sidebar-link__file-upload-input');
        let formData = new FormData();
        if (fileField.files.length == 0) {
            notificationBar.show(noFileMessage, notificationBar.WARNING);
        } else {
            const url = button.getAttribute("data-url");
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
