var timer;
function loadThemeDisplay(themeNum, scalingRatio) {
	clearInterval(timer);
	reqThemeDisplay(themeNum, scalingRatio);
	if (themeNum == null || themeNum == undefined || themeNum == "") {
		timer = setInterval(function () {
			reqThemeDisplay(themeNum, scalingRatio);
		}, 10000);
	}
}

function reqThemeDisplay(themeNum, scalingRatio) {
	var url = "theme/display";
	var reqParams = "";
	if (themeNum != null && themeNum != undefined && themeNum != "") {
		reqParams += "&themeNum=" + themeNum;
	}
	if (scalingRatio != null && scalingRatio != undefined && scalingRatio != "") {
		reqParams += "&scalingRatio=" + scalingRatio;
	}
	if (reqParams != "") {
		url += "?" + reqParams.substring(1);
	}
	$.ajax({
		type: "POST",
		url: url,
		data: {},
		success: function (result) {
			$("#themeDisplay").html(result);
		}
	})
}
