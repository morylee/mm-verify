$("#createWebsiteBtn").on("click", function () {
	if ($("#websiteForm").valid()) {
		var website = formatFormDataById('websiteForm');
		$.ajax({
			type: "POST",
			url: "website/create",
			dataType: "JSON",
			contentType: "application/json;charset=UTF-8",
			data: JSON.stringify(website)
		}).then(function (result) {
			if (result.httpCode == 200) {
				setLocalUrl("website/" + result.data);
			} else {
				toastr.error(result.msg);
			}
		});
	}
})
