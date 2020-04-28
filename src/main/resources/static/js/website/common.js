var pwdCheckValidator = $("#pwdCheckForm").validate({
	ignore:"",
	debug:true,
	rules:{
		password:{
			required: true,
			minlength: 6
		}
	},
	messages:{
		password:{
			required: "请输入登录密码",
			minlength: "登录密码不得少于6位"
		}
	}
});

$(".websiteBox").on("click", ".glyphicon-eye-open", function () {
	var parentObj = $(this).parent();
	var webKey = $(this).data("webkey");
	$("#globalPwdCheck").modal("show");
	$("#globalPwdCheck #password").focus();
	$("#globalPwdCheck #pwdCheckOkBtn").on("click", function() {
		if ($("#pwdCheckForm").valid()) {
			var password = $.sha1($("#globalPwdCheck #password").val());
			$.ajax({
				type: "POST",
				url: "website/apiKey",
				dataType: "JSON",
				contentType: "application/json;charset=UTF-8",
				data: JSON.stringify({webKey: webKey, password: password})
			}).then(function (result) {
				if (result.httpCode == 200) {
					$("#globalPwdCheck").modal("hide");
					toggleOpenOrClose(parentObj, result.data);
				} else {
					toastr.error(result.msg);
				}
			});
		}
	});
})
$("#globalPwdCheck").on("hidden.bs.modal", function () {
	$("#globalPwdCheck #password").val("");
	$("#globalPwdCheck #pwdCheckOkBtn").unbind("click");
})

bindEnterEvent("globalPwdCheck #password", "globalPwdCheck #pwdCheckOkBtn");

$(".websiteBox").on("click", ".glyphicon-eye-close", function () {
	var parentObj = $(this).parent();
	toggleOpenOrClose(parentObj, "");
})
function toggleOpenOrClose(parentObj, apiKey) {
	parentObj.find("span.openInfo").html(apiKey);
	if (apiKey == null || apiKey == undefined || apiKey == "") {
		parentObj.find("span.openInfo").addClass("hide");
		parentObj.find("span.closeInfo").removeClass("hide");
		parentObj.find(".openOrClose").addClass("glyphicon-eye-open").removeClass("glyphicon-eye-close").attr("title", "查看");
	} else {
		parentObj.find("span.openInfo").removeClass("hide");
		parentObj.find("span.closeInfo").addClass("hide");
		parentObj.find(".openOrClose").removeClass("glyphicon-eye-open").addClass("glyphicon-eye-close").attr("title", "隐藏");
	}
}
