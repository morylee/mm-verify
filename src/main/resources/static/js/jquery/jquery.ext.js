hookAjax({
	//拦截回调
	onreadystatechange:function(xhr){
		try {
			var data = JSON.parse(xhr.responseText);
			if (data.httpCode == 401) {
				$(window).unbind('beforeunload');
				window.location.reload();
				return true;
			}
		} catch (err) {
			
		}
		return false;
	},
	onload:function(xhr){
		try {
			var data = JSON.parse(xhr.responseText);
			if (data.httpCode == 401) {
				$(window).unbind('beforeunload');
				window.location.reload();
				return true;
			}
		} catch (err) {
			
		}
		return false;
	},
	//拦截函数
	open:function(arg){
	}
})
