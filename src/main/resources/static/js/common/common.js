function loadData(renderId, reqUrl, formId, page) {
	var reqData = formatFormDataById(formId);
	reqData.page = page;
	var paramStr = "";
	$.each(reqData, function(key, value) {
		if (value != null && value != undefined) paramStr += "&" + key + "=" + value;
	});
	if (paramStr != "") reqUrl += "?" + paramStr.substring(1);
	$.ajax({
		type: "POST",
		url: reqUrl,
		data: {},
		success: function (result) {
			$("#" + renderId).html(result);
		}
	})
}

function listenerSearch(renderId, reqUrl, formId) {
	if (formId != null && formId != undefined) {
		$("#" + formId + " input, #" + formId + " select").on("change", function () {
			loadData(renderId, reqUrl, formId, 1);
		})
	}
}

function formatFormDataById(formId) {
	return formatFormData($("#" + formId));
}

function formatFormData(formObj) {
	if (formObj == null || formObj == undefined || formObj.length == 0) return {};
	var array = formObj.serializeArray();
	var data = {};
	$.each(array, function () {
		var name = this.name;
		if (name.length > 2 && name.substring(name.length - 2) == "[]") {
			name = name.substring(0, name.length - 2);
			if (data[name] == undefined) {
				data[name] = [this.value || ''];
			} else {
				data[name].push(this.value || '');
			}
		} else {
			if (data[name] !== undefined) {
				if (!data[name].push) {
					data[name] = [data[name]];
				}
				data[name].push(this.value || '');
			} else {
				data[name] = this.value || '';
			}
		}
	});
	return data;
}

function random(from, to) {
	return parseInt(Math.random() * (to - from + 1) + from);
}

function bindEnterEvent(inputId, btnId) {
	$('#' + inputId).bind('keypress', function(event) {
		if (event.keyCode == "13") {
			event.preventDefault(); 
			$('#' + btnId).click();
		}
	});
}

Date.prototype.format = function (){
	var s = '';
	s += this.getFullYear() + '-';
	s += (this.getMonth() + 1) + '-';
	s += this.getDate();
	return(s);
};

function getDayListBefore(dateStr, days) {
	var list = new Array();
	var dateArr = dateStr.split('-');
	var date = new Date();
	date.setUTCFullYear(dateArr[0], dateArr[1] - 1, dateArr[2]);
	var unixTime = date.getTime();
	for (var d = days - 1; d >= 0; d--) {
		list.push((new Date(parseInt(unixTime - d * 24 * 60 * 60 * 1000))).format().toString());
	}
	return list;
}

listenerWindowHeight();

function listenerWindowHeight() {
	var windowHeight = $(window).height();
	var headerHeight = $(".globalHeader nav").height();
	var footerHeight = $(".globalFooter").height();
	$(".verifyMainContent").css("min-height", windowHeight - headerHeight - footerHeight + "px");
}

window.onresize = listenerWindowHeight;

$(".modal").on("show.bs.modal", function (e) {
	// 关键代码，如没将modal设置为 block，则$modala_dialog.height() 为零
	$(this).css("display", "block");
	var modalHeight = $(window).height() / 2 - $(this).find(".modal-dialog").height() / 2;
	if (modalHeight < 0) modalHeight = 0;
	$(this).find(".modal-dialog").css({
		"margin-top": modalHeight
	});
});
