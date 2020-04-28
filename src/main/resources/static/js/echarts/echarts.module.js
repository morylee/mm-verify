function getLineOption(title, lineTitles, xTitle, xData, yTitle, dataList) {
	var list = [];
	for (var i = 0; i < dataList.length; i++) {
		var item = {
			name: lineTitles[i],
			type: 'line',
			markPoint: {
				data: [
					{type: 'max', name: '最大值'}
				]
			},
			data: dataList[i]
		};
		list.push(item);
	}
	return {
		grid:{
			x: 80,
			y: 80,
			x2: 60,
			y2: 60,
			borderWidth: 1
		},
		title: {
			text: title,
			padding: 15,
			x: 'left'
		},
		tooltip: {
			trigger: 'axis'
		},
		legend: {
			data: lineTitles,
			y: 'bottom'
		},
		calculable: true,
		xAxis : [{
			name: xTitle,
			type: 'category',
			data: xData
		}],
		yAxis: [{
			name: yTitle,
			type: 'value',
			scale: true
		}],
		series: list
	};
}

function getPieOption(title, dataTitle, data) {
	return {
		grid:{
			x: 80,
			y: 80,
			x2: 60,
			y2: 60,
			borderWidth: 1
		},
		title: {
			text: title,
			padding: 15,
			x: 'left'
		},
		tooltip: {
//			trigger: 'axis'
		},
		series : [
			{
				name: dataTitle,
				type: 'pie',   // 设置图表类型为饼图
				radius: '55%', // 饼图的半径，外半径为可视区尺寸（容器高宽中较小一项）的 55% 长度。
				data: data
			}
		]
	}
}
