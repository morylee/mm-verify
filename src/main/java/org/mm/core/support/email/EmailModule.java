package org.mm.core.support.email;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.mm.core.Constants;

public final class EmailModule {

	public static final String sendCaptchaModule(Map<String, String> params) {
		StringBuffer sbBuilder = new StringBuffer();
		sbBuilder.append("<h1 class=\"_3JBS\">您好</h1>");
		sbBuilder.append("<p>&emsp;&emsp;欢迎注册可信验！您的验证码为：");
		sbBuilder.append(params.get("captcha"));
		sbBuilder.append("（5分钟内有效）</p>");
		sbBuilder.append("<p>&emsp;&emsp;有任何问题请和我们联系。</p>");
		
		params.put("content", sbBuilder.toString());
		return module(params);
	}
	
	
	public static final String module(Map<String, String> params) {
		String path = params.get("path");
		if (StringUtils.isBlank(path)) path = Constants.SYS_DEFAULT_WEBSITE;
		
		StringBuffer sbBuilder = new StringBuffer();
		sbBuilder.append("<html class=\"no-js\">");
		sbBuilder.append("<style id=\"css\"> ._217Y{background-color:#ffffff}");
		sbBuilder.append("._2ZCI, ._217Y{box-sizing:border-box}");
		sbBuilder.append("._2ZCI{height:60px;width:100%;text-align:center}");
		sbBuilder.append("._2ZCI img{width:140px;height:30px;margin:15px 0}");
		sbBuilder.append("._4xC_{margin:20px auto 30px;max-width:600px;min-height:420px;padding:20px 60px 30px;background:#fff;border:1px solid #ddd;box-sizing:border-box;border-radius:4px}");
		sbBuilder.append("._4xC_ ._3lkm{font-size:20px;font-weight:400;color:#4c5156;line-height:29px}");
		sbBuilder.append("._4xC_ ._3lkm+hr{margin:10px 0 15px}");
		sbBuilder.append("._1bsd{border:none;border-top:1px solid #e4e4e4}");
		sbBuilder.append("._4xC_ .aaIV ._3JBS{margin-bottom:10px;font-size:18px;font-weight:400;color:#4c5156;line-height:25px}");
		sbBuilder.append("._4xC_ ._16tS p, ._4xC_ .aaIV p{margin:0 0 5px;font-size:14px;line-height:20px;color:#717478}");
		sbBuilder.append("._4xC_ ._3BkN{height:70px;padding:10px 0;margin:30px 0 80px;text-align:center;box-sizing:border-box}");
		sbBuilder.append("._4xC_ ._3BkN ._1wgI{padding:0;height:50px;width:267px;font-size:16px;color:#fff!important;border-radius:4px;border:1px solid #25cca7;background:#25cca7 none;box-sizing:border-box;cursor:pointer;text-transform:none;outline:none}");
		sbBuilder.append("._4xC_ ._3BkN ._1wgI:focus, ._4xC_ ._3BkN ._1wgI:hover{color:#fff!important;outline:none;border-color:#3bd1b0;background:#3bd1b0 none}");
		sbBuilder.append("._1GlX{font-size:14px;line-height:20px;padding:10px 0 0;margin-bottom:5px;list-style:none;text-align:center}");
		sbBuilder.append("._1GlX&gt;li{display:inline-block;padding-right:5px;padding-left:5px;color:#717478}");
		sbBuilder.append("._1GlX&gt;li&gt;a{text-decoration:none;color:#717478}");
		sbBuilder.append("._10m5{display:block;padding-bottom:10px;text-align:center;font-size:14px;line-height:20px;color:#717478}");
		sbBuilder.append("._10m5&gt;span+span{margin-left:15px}");
		sbBuilder.append("._58FN{width:100%}");
		sbBuilder.append("._58FN&gt;span, ._58FN&gt;ul{text-align:center}");
		sbBuilder.append("._58FN ._1ZHp{padding-right:5px;padding-left:5px;list-style:none;text-decoration:none;display:inline;color:#717478;margin-left:0}");
		sbBuilder.append("._58FN ._1ZHp a{text-decoration:none!important;color:#717478}");
		sbBuilder.append("._3GaQ{pointer-events:none;cursor:default}");
		sbBuilder.append("._3Xoz:active, ._3Xoz:hover{color:#34495e}");
		sbBuilder.append("._1EWg{height:6px;display:block!important;background:linear-gradient(90deg,#25cca7,#53a0fd)}");
		sbBuilder.append("</style>");
		sbBuilder.append("<div id=\"app\">");
		sbBuilder.append("<div class=\"_217Y\">");
		sbBuilder.append("<div class=\"_2ZCI\">");
		sbBuilder.append("<img src='https://www.cloudcrowd.com.cn/images/logo_white.svg' lazysrc=\"https://www.cloudcrowd.com.cn/images/logo_white.svg\"/>");
		sbBuilder.append("</div>");
		sbBuilder.append("<div class=\"_4xC_\">");
		sbBuilder.append("<div class=\"_3lkm\">");
		sbBuilder.append(params.get("title"));
		sbBuilder.append("</div>");
		sbBuilder.append("<hr class=\"_1bsd\"/>");
		sbBuilder.append("<div class=\"aaIV\">");
		sbBuilder.append(params.get("content"));
		sbBuilder.append("</div>");
		sbBuilder.append("<div class=\"_3BkN\"><br/></div>");
		sbBuilder.append("<div class=\"_16tS\">");
		sbBuilder.append("<p>云众可信团队</p>");
		sbBuilder.append("</div>");
		sbBuilder.append("</div>");
		sbBuilder.append("<div class=\"_58FN\"><ul class=\"_1GlX\">");
		sbBuilder.append("<li class=\"_1ZHp\"><a class=\"_3Xoz\" href=\"");
		sbBuilder.append(path);
		sbBuilder.append("/help\" target=\"_blank\">帮助中心</a></li>");
		sbBuilder.append("<li class=\"_1ZHp\"> · </li><li class=\"_1ZHp\"><a class=\"_3Xoz\" href=\"");
		sbBuilder.append(path);
		sbBuilder.append("/about\">关于我们</a></li>");
		sbBuilder.append("<li class=\"_1ZHp\"> · </li><li class=\"_1ZHp\"><a class=\"_3Xoz\" href=\"");
		sbBuilder.append(path);
		sbBuilder.append("/contact\" target=\"_blank\">联系我们</a></li></ul>");
		sbBuilder.append("<div class=\"_10m5\"><span>&#xA9; 启明星辰 2020 版权所有</span>&nbsp;&nbsp;");
		sbBuilder.append("</div>");
		sbBuilder.append("</div>");
		sbBuilder.append("</div>");
		sbBuilder.append("</html>");
		return sbBuilder.toString();
	}

}
