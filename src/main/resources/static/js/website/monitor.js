var webKey = $("#webKey").val();

var currTime = new Date();
var todayTime = new Date(currTime.getTime() - 24 * 60 * 60 * 1000);

$("#timeFrom").datetimepicker({
	format: 'yyyy-mm-dd hh:ii:00',
	autoclose: true,
	todayBtn: true,
	language:'zh-CN',
	initialDate: todayTime,
	endDate: currTime,
}).on('changeDate', function(event) {
	event.preventDefault();
	event.stopPropagation();
	var timeFrom = event.date;
	$('#timeTo').datetimepicker('setStartDate', timeFrom);
	
	loadTimeLineChart(webKey, $('#timeFrom').val(), $('#timeTo').val());
	loadHourLineChart(webKey, $('#timeFrom').val(), $('#timeTo').val());
	loadStatePieChart(webKey, $('#timeFrom').val(), $('#timeTo').val());
});
$("#timeTo").datetimepicker({
	format: 'yyyy-mm-dd hh:ii:00',
	autoclose: true,
	todayBtn: true,
	language:'zh-CN',
	initialDate: currTime,
	startDate: todayTime
}).on('changeDate', function(event) {
	event.preventDefault();
	event.stopPropagation();
	var timeTo = event.date;
	$('#timeFrom').datetimepicker('setEndDate', timeTo);
	
	loadTimeLineChart(webKey, $('#timeFrom').val(), $('#timeTo').val());
	loadHourLineChart(webKey, $('#timeFrom').val(), $('#timeTo').val());
	loadStatePieChart(webKey, $('#timeFrom').val(), $('#timeTo').val());
});
$("#timeFrom").datetimepicker("setDate", todayTime);
$("#timeTo").datetimepicker("setDate", currTime);

var websiteTimeLineChart = echarts.init(document.getElementById('websiteTimeLineChart'), 'westeros');
var websiteHourLineChart = echarts.init(document.getElementById('websiteHourLineChart'), 'westeros');
var websiteStatePieChart = echarts.init(document.getElementById('websiteStatePieChart'), 'westeros');

var timeLineDefaultNames = getDayListBefore((new Date()).format().toString(), 30);
var timeLineDefaultValues = [];
for (var i = 0; i < timeLineDefaultNames.length; i++) {
	timeLineDefaultValues.push(0);
}

var hourLineDefaultNames = [];
var hourLineDefaultValues = [];
for (var i = 0; i < 24; i++) {
	hourLineDefaultNames.push(i + '时');
	hourLineDefaultValues.push(0);
}

var statePieDefaultData = [{name: '未验证', value: 0}, {name: '已验证', value: 0}, {name: '验证失败', value: 0}, {name: '验证成功', value: 0}];

websiteTimeLineChart.setOption(getLineOption('历史验证态势图', ['总计次数'], '时间', timeLineDefaultNames, '请求次数', [timeLineDefaultValues]));
websiteHourLineChart.setOption(getLineOption('时段验证态势图', ['总计次数'], '时间', hourLineDefaultNames, '请求次数', [hourLineDefaultValues]));
websiteStatePieChart.setOption(getPieOption('使用状态分布图', '请求次数', statePieDefaultData));

loadTimeLineChart(webKey, null, null);
function loadTimeLineChart(webKey, timeFrom, timeTo) {
	$.ajax({
		type: "POST",
		url: "website/monitor/time",
		dataType: "JSON",
		contentType: "application/json;charset=UTF-8",
		data: JSON.stringify({webKey: webKey, timeFrom: timeFrom, timeTo: timeTo})
	}).then(function (result) {
		if (result.httpCode == 200) {
			websiteTimeLineChart.setOption(getLineOption('历史验证态势图', [result.websiteName],
				'时间', result.names, '请求次数', [result.values]));
		} else {
			toastr.error(result.msg);
		}
	});
}

loadHourLineChart(webKey, null, null);
function loadHourLineChart(webKey, timeFrom, timeTo) {
	$.ajax({
		type: "POST",
		url: "website/monitor/hour",
		dataType: "JSON",
		contentType: "application/json;charset=UTF-8",
		data: JSON.stringify({webKey: webKey, timeFrom: timeFrom, timeTo: timeTo})
	}).then(function (result) {
		if (result.httpCode == 200) {
			websiteHourLineChart.setOption(getLineOption('时段验证态势图', [result.websiteName],
				'时间', result.names, '请求次数', [result.values]));
		} else {
			toastr.error(result.msg);
		}
	});
}

loadStatePieChart(webKey, null, null);
function loadStatePieChart(webKey, timeFrom, timeTo) {
	$.ajax({
		type: "POST",
		url: "website/monitor/state",
		dataType: "JSON",
		contentType: "application/json;charset=UTF-8",
		data: JSON.stringify({webKey: webKey, timeFrom: timeFrom, timeTo: timeTo})
	}).then(function (result) {
		if (result.httpCode == 200) {
			websiteStatePieChart.setOption(getPieOption('使用状态分布图', '请求次数', result.data));
		} else {
			toastr.error(result.msg);
		}
	});
}
