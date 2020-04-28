$(".paginationBox").on("click", ".pageBtn:not(.disabled)", function () {
	var renderId = $(this).parents(".paginationBox").data("render-id");
	var reqUrl = $(this).parents(".paginationBox").data("req-url");
	var formId = $(this).parents(".paginationBox").data("form-id");
	var page = $(this).data("page");
	loadData(renderId, reqUrl, formId, page);
})
