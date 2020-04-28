loadData("websitesBox", "website/search", null, 1);

listenerSearch("websitesBox", "website/search", null);

$("#websitesBox").on("click", " .delWebsite", function () {
	var webKey = $(this).data("webkey");
	$("#globalConfirm").modal("show");
	$("#globalConfirm #confirmOkBtn").on("click", function () {
		$.ajax({
			type: "POST",
			url: "website/delete",
			dataType: "JSON",
			contentType: "application/json;charset=UTF-8",
			data: JSON.stringify({webKey: webKey})
		}).then(function (result) {
			if (result.httpCode == 200) {
				toastr.success("删除成功");
				$("#globalConfirm").modal("hide");
				var page = $("#websitesBox .paginationBox .numPage.current").data("page");
				loadData("websitesBox", "website/search", null, page);
			} else {
				toastr.error(result.msg);
			}
		});
	})
})
$("#globalConfirm").on("hidden.bs.modal", function () {
	$("#globalConfirm #confirmOkBtn").unbind("click");
})
