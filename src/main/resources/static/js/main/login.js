$("#demoCarousel").carousel('cycle');

$("#account").focus();

var loginVerify = new GraphicVerify({
	webKey: "28e7c0b54c934934b9519c651b699f9b",
	container: "#loginVerify",
	color: "#999",
	verifyWidth: 300,
	successCallback: validLoginVerify
});

function validLoginVerify () {
	if (loginVerify.verifyResult) {
		$("#loginBtn").focus();
		$("#loginVerify_error").addClass("hide");
		return true;
	} else {
		$("#loginVerify_error").removeClass("hide");
		return false;
	}
}

var registerVerify = new GraphicVerify({
	webKey: "28e7c0b54c934934b9519c651b699f9b",
	container: "#registerVerify",
	color: "#999",
	verifyWidth: 300,
	successCallback: validRegisterVerify
});

function validRegisterVerify () {
	if (registerVerify.verifyResult) {
		$("#sendCaptchaBtn").focus();
		$("#registerVerify_error").addClass("hide");
		return true;
	} else {
		$("#registerVerify_error").removeClass("hide");
		return false;
	}
}

bindEnterEvent("loginBtn", "loginBtn");
bindEnterEvent("sendCaptchaBtn", "sendCaptchaBtn");
bindEnterEvent("captcha", "registerNextBtn");
bindEnterEvent("confirmPassword", "registerBtn");

$("#loginTab #account, #loginTab #password").on("input", function () {
	loginVerify.reset();
	var current = $(this);
	doValidInput(current);
})

$("#registerTab #email").on("input", function () {
	registerVerify.reset();
	var current = $(this);
	doValidInput(current);
})

$("#registerTab #captcha, #registerTab #name, #registerTab #regPassword, #registerTab #confirmPassword").on("input", function () {
	var current = $(this);
	doValidInput(current);
})

function doValidInput(current) {
	var currId = current.attr("id");
	var flag = false;
	if (currId == "account") {
		flag = validAccount(current.val());
	} else if (currId == "name") {
		flag = validName(current.val());
	} else if (currId == "password" || currId == "regPassword") {
		flag = validPassword(current.val());
	} else if (currId == "confirmPassword") {
		flag = validConfirmPassword($("#regPassword").val(), current.val());
	} else if (currId == "email") {
		flag = validEmail(current.val());
	} else if (currId == "captcha") {
		flag = validCaptcha(current.val());
	}
	if (flag) {
		$("#" + currId + "_error").addClass("hide");
	} else {
		$("#" + currId + "_error").removeClass("hide");
	}
	return flag;
}

function validName(value) {
	var nameReg = /^[a-zA-Z0-9\u4e00-\u9fa5]{2,16}$/;
	return nameReg.test(value);
}

function validAccount(value) {
	var mobileReg = /^[1][3,4,5,7,8,9][0-9]{9}$/;
	var accountReg = /^([a-z0-9A-Z]+[-|_|\\.]?)+[a-zA-Z0-9_-]@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/;
	return mobileReg.test(value) || accountReg.test(value);
}

function validPassword(value) {
	var passwordReg = /^(?=.{7,})(((?=.*[A-Z])(?=.*[a-z]))|((?=.*[A-Z])(?=.*[0-9]))|((?=.*[a-z])(?=.*[0-9]))).*$/;
	return passwordReg.test(value);
}

function validConfirmPassword(origin, value) {
	return origin == value;
}

function validEmail(value) {
	var accountReg = /^([a-z0-9A-Z]+[-|_|\\.]?)+[a-zA-Z0-9_-]@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/;
	return accountReg.test(value);
}

function validCaptcha(value) {
	var captchaReg = /^\d{6}$/;
	return captchaReg.test(value);
}

function changeSubmitBtnState(btnId, state) {
	if (state) {
		$("#" + btnId).removeAttr("disabled");
	} else {
		$("#" + btnId).attr("disabled", "disabled");
	}
}

function disableBtn(btnId) {
	var count = 60;
	var timer = null;
	var btn = $(btnId);
	timer = setInterval(function() {
		count--;
		if(count >= 0) {
			btn.text("重新发送(" + count + ")");
			btn.attr("disabled", true);
			btn.css("background", "#d4d7d9");
		} else {
			btn.text("重新发送");
			btn.attr("disabled", false);
			btn.css("background", "");
			clearInterval(timer);
		}
	}, 1000)
	return timer;
}

function enableBtn(btnId, timer) {
	var btn = $(btnId);
	btn.text("重新发送");
	btn.attr("disabled", false);
	btn.css("background", "");
	clearInterval(timer);
}

$("#loginBtn").on("click", function () {
	if (doValidInput($("#account")) && doValidInput($("#password")) && validLoginVerify()) {
		var account = $("#account").val();
		var password = $.sha1($("#password").val());
		var captcha = loginVerify.verifyResult;
		loginVerify.reset();
		$.ajax({
			type: "POST",
			url: "user/login",
			dataType: "JSON",
			contentType: "application/json;charset=UTF-8",
			data: JSON.stringify({account: account, password: password, captcha: captcha})
		}).then(function (result) {
			if (result.httpCode == 200) {
				setLocalUrl(result.data);
			} else {
				toastr.error(result.msg);
			}
		});
	}
})

$("#sendCaptchaBtn").on("click", function () {
	if (doValidInput($("#email")) && validRegisterVerify()) {
		var self = this;
		var timer = disableBtn(self);
		
		var email = $("#email").val();
		var captcha = registerVerify.verifyResult;
		registerVerify.reset();
		$.ajax({
			type: "POST",
			url: "email/register",
			dataType: "JSON",
			contentType: "application/json;charset=UTF-8",
			data: JSON.stringify({email: email, captcha: captcha})
		}).then(function (result) {
			if (result.httpCode == 200) {
				if (result.success) {
					toastr.success("发送成功，请注意查收邮件");
				} else {
					toastr.error(result.msg);
					enableBtn(self, timer);
				}
			} else {
				toastr.error(result.msg);
				enableBtn(self, timer);
			}
		}).fail(function () {
			toastr.error("发送失败");
			enableBtn(self, timer);
		});
	}
})

$("#registerNextBtn").on("click", function () {
	if (doValidInput($("#email")) && doValidInput($("#captcha"))) {
		var email = $("#email").val();
		var captcha = $("#captcha").val();
		$.ajax({
			type: "POST",
			url: "email/registerCheck",
			dataType: "JSON",
			contentType: "application/json;charset=UTF-8",
			data: JSON.stringify({email: email, captcha: captcha})
		}).then(function (result) {
			if (result.httpCode == 200) {
				if (result.success) {
					$("#registerStep1").addClass("hide");
					$("#registerStep2").removeClass("hide");
				} else {
					toastr.error(result.msg);
				}
			} else {
				toastr.error(result.msg);
			}
		});
	}
})

$("#registerBtn").on("click", function () {
	if (doValidInput($("#name")) && doValidInput($("#regPassword")) && doValidInput($("#confirmPassword"))) {
		var email = $("#email").val();
		var captcha = $("#captcha").val();
		var name = $("#name").val();
		var password = $.sha1($("#regPassword").val());
		var confirmPassword = $.sha1($("#confirmPassword").val());
		$.ajax({
			type: "POST",
			url: "user/register",
			dataType: "JSON",
			contentType: "application/json;charset=UTF-8",
			data: JSON.stringify({
				email: email,
				captcha: captcha,
				name: name,
				password: password,
				confirmPassword: confirmPassword
			})
		}).then(function (result) {
			if (result.httpCode == 200) {
				toastr.success("恭喜您，注册成功");
				$("#loginTabBtn").trigger("click");
				$("#account").focus();
			} else {
				toastr.error(result.msg);
			}
		});
	}
})

$("#loginTabBtn").on("shown.bs.tab", function () {
	$("#account").focus();
})
$("#registerTabBtn").on("shown.bs.tab", function () {
	$("#email").focus();
})

$(".switchToRegister").on("click", function () {
	$("#loginTabBtn").trigger("click");
	$("#account").focus();
	$('html, body').animate({scrollTop: $("#loginTabBtn").offset().top - 60}, 500)
})
