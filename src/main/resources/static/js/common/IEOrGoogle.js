function setLocalUrl(url) {
	setLocalUrl(url, false);
}

function setLocalUrl(url, newWindow) {
	if(isIE() == true) {
		html = "<a id='ourOwnRedirectUrlBtn' href='" + url +"' class='hide'";
		if (newWindow) {
			html += " target='_blank'";
		}
		html += ">redirect</a>";
		$("body").append(html);
		$("#ourOwnRedirectUrlBtn")[0].click();
	}else {
		window.location.href = url;
	}
}

function isIE() { //ie?
	var usrAgent = navigator.userAgent;
	var agentName = getBroswerToken(usrAgent);
	
	if (agentName == "IE(6, 7, 8, 9, 10)" || agentName == "IE11" || agentName == "Edge")
		return true;
	else
		return false;
}

function getOlder(agent) {
	var result = "";
	var iMsie = agent.indexOf('MSIE');

	// https://msdn.microsoft.com/en-us/library/ms537503(v=vs.85).aspx
	if (iMsie > 0) {
		// MSIE 6.0, MSIE 7.0, MSIE 8.0, MSIE 9.0, MSIE 10.0
		result = "IE(6, 7, 8, 9, 10)";
	}
	else if (agent.indexOf("Firefox") > 0) {
		result = "Firefox";
	}
	return result;
}

function getBroswerToken(agent) {
	var result = "";
	var iChrome = agent.indexOf('Chrome');
	var iGecko = agent.indexOf('like Gecko');
	var iEdge = agent.indexOf('Edge');
	var iDensity = iDensity_edge = iDensity_chrome = iDensity_gecko = 0;
	//
	if (iEdge > 0) {
		iDensity_edge = 100;
	}
	if (iChrome > 0) {
		iDensity_chrome = 10;
	}
	if (iGecko > 0) {
		iDensity_gecko = 1;
	}
	// 
	iDensity = iDensity_edge + iDensity_chrome + iDensity_gecko;
	switch (iDensity) {
		case 111: {
			// Edge
			result = 'Edge';
			break;
		}
		case 11: {
			result = 'Chrome';
			break;
		}
		case 1: {
			// IE11
			result = 'IE11';
			break;
		}
		default: {
			result = getOlder(agent);
			break;
		}
	}

	return result;
}
