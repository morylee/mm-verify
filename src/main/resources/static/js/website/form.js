var webKey = $("#webKey").val();
loadWebsiteInfo(webKey);
function loadWebsiteInfo(webKey) {
	var url = "website/info";
	if (webKey != null && webKey != undefined && webKey != "") {
		url += "/" + webKey;
	}
	$.ajax({
		type: "POST",
		url: url,
		data: {},
		success: function (result) {
			$("#websiteInfo").html(result);
			var themeNum = $("input[name='themeNum']:checked").val();
			var scalingRatio = $("#scalingRatio").val();
			loadThemeDisplay(themeNum, scalingRatio);
			listenerThemeChange();
		}
	})
}

function listenerThemeChange() {
	$("input[name='themeNum'], #scalingRatio").on("change", function () {
		$("#themeDisplay").html("<img src='images/website/loading.gif'>");
		var themeNum = $("input[name='themeNum']:checked").val();
		var scalingRatio = $("#scalingRatio").val();
		loadThemeDisplay(themeNum, scalingRatio);
	})
}

$("#websiteForm").validate({
	ignore: "",
	debug: true,
	rules: {
		name: {
			required: true,
			minlength: 2,
			maxlength: 32,
		},
		url: {
			required: true,
			minlength: 6,
			maxlength: 100,
		},
		themeNum: {
			required: true,
		},
		scalingRatio: {
			required: true,
		},
		secMode: {
			required: true,
		},
		secLevel: {
			required: true,
		},
	},
	messages: {
		name: {
			required: "请输入网站名称",
			minlength: "网站名称不得少于2位",
			maxlength: "网站名称不得超过32位",
		},
		url: {
			required:"请输入网站地址",
			minlength: "网站地址不得少于6位",
			maxlength: "网站地址不得超过100位",
		},
		themeNum: {
			required: "请选择主题图库",
		},
		scalingRatio: {
			required:"请选择缩放比例",
		},
		secMode: {
			required:"请选择验证模式",
		},
		secLevel: {
			required: "请选择安全级别",
		},
	},
	errorPlacement: function(error, element) {
		if (element.is(":checkbox") || element.is(":radio")) {
			error.insertAfter(element.parents(".multipleInput"));
		} else if (element.is("select")) {
			error.appendTo(element.parent());
		} else {
			error.insertAfter(element);
		}
	}
});
