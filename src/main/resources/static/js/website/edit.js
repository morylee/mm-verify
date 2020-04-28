$("#updateWebsiteBtn").on("click", function () {
	if ($("#websiteForm").valid()) {
		var website = formatFormDataById('websiteForm');
		$.ajax({
			type: "POST",
			url: "website/update",
			dataType: "JSON",
			contentType: "application/json;charset=UTF-8",
			data: JSON.stringify(website)
		}).then(function (result) {
			if (result.httpCode == 200) {
				setLocalUrl("website/" + website.webKey);
			} else {
				toastr.error(result.msg);
			}
		});
	}
})
