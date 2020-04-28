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
	
	loadTimeLineChart($('#timeFrom').val(), $('#timeTo').val());
	loadHourLineChart($('#timeFrom').val(), $('#timeTo').val());
	loadStatePieChart($('#timeFrom').val(), $('#timeTo').val());
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
	
	loadTimeLineChart($('#timeFrom').val(), $('#timeTo').val());
	loadHourLineChart($('#timeFrom').val(), $('#timeTo').val());
	loadStatePieChart($('#timeFrom').val(), $('#timeTo').val());
});
$("#timeFrom").datetimepicker("setDate", todayTime);
$("#timeTo").datetimepicker("setDate", currTime);

var indexTimeLineChart = echarts.init(document.getElementById('indexTimeLineChart'), 'westeros');
var indexHourLineChart = echarts.init(document.getElementById('indexHourLineChart'), 'westeros');
var indexStatePieChart = echarts.init(document.getElementById('indexStatePieChart'), 'westeros');

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

indexTimeLineChart.setOption(getLineOption('历史验证态势图', ['总计次数'], '时间', timeLineDefaultNames, '请求次数', [timeLineDefaultValues]));
indexHourLineChart.setOption(getLineOption('时段验证态势图', ['总计次数'], '时间', hourLineDefaultNames, '请求次数', [hourLineDefaultValues]));
indexStatePieChart.setOption(getPieOption('使用状态分布图', '请求次数', statePieDefaultData));

loadTimeLineChart(null, null);
function loadTimeLineChart(timeFrom, timeTo) {
	$.ajax({
		type: "POST",
		url: "index/monitor/time",
		dataType: "JSON",
		contentType: "application/json;charset=UTF-8",
		data: JSON.stringify({timeFrom: timeFrom, timeTo: timeTo})
	}).then(function (result) {
		if (result.httpCode == 200) {
			indexTimeLineChart.setOption(getLineOption('历史验证态势图', result.websiteNames,
				'时间', result.names, '请求次数', result.valuesList));
		} else {
			toastr.error(result.msg);
		}
	});
}

loadHourLineChart(null, null);
function loadHourLineChart(timeFrom, timeTo) {
	$.ajax({
		type: "POST",
		url: "index/monitor/hour",
		dataType: "JSON",
		contentType: "application/json;charset=UTF-8",
		data: JSON.stringify({timeFrom: timeFrom, timeTo: timeTo})
	}).then(function (result) {
		if (result.httpCode == 200) {
			indexHourLineChart.setOption(getLineOption('时段验证态势图', result.websiteNames,
				'时间', result.names, '请求次数', result.valuesList));
		} else {
			toastr.error(result.msg);
		}
	});
}

loadStatePieChart(null, null);
function loadStatePieChart(timeFrom, timeTo) {
	$.ajax({
		type: "POST",
		url: "index/monitor/state",
		dataType: "JSON",
		contentType: "application/json;charset=UTF-8",
		data: JSON.stringify({timeFrom: timeFrom, timeTo: timeTo})
	}).then(function (result) {
		if (result.httpCode == 200) {
			indexStatePieChart.setOption(getPieOption('使用状态分布图', '请求次数', result.data));
		} else {
			toastr.error(result.msg);
		}
	});
}
