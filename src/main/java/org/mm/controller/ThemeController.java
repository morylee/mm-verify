package org.mm.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mm.core.captcha.CaptchaUtil;
import org.mm.core.util.RandomUtil;
import org.mm.core.util.TypeParseUtil;
import org.mm.model.Website;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/theme")
public class ThemeController extends BaseController {

	@RequestMapping(value = "/display", method = { RequestMethod.POST })
	public Object display(ModelMap modelMap, HttpServletRequest request,
			@RequestParam(value = "themeNum", required = false) String _themeNum,
			@RequestParam(value = "scalingRatio", required = false) String _scalingRatio) {
		Integer themeNum = TypeParseUtil.convertToInteger(_themeNum);
		if (themeNum == null || themeNum < Website.THEME_NUM_MIN || themeNum > Website.THEME_NUM_MAX) {
			themeNum = RandomUtil.randomInt(Website.THEME_NUM_MIN, Website.THEME_NUM_MAX + 1);
		}
		Double scalingRatio = TypeParseUtil.convertToDouble(_scalingRatio);
		if (scalingRatio == null || scalingRatio < Website.SCALING_RATIO_MIN || scalingRatio > Website.SCALING_RATIO_MAX)
			scalingRatio =  Website.SCALING_RATIO_MAX;
		
		List<String> backgrounds = CaptchaUtil.backgrounds(themeNum, scalingRatio);
		modelMap.addAttribute("backgrounds", backgrounds);
		modelMap.addAttribute("themeWidth", CaptchaUtil.DEFAULT_WIDTH * scalingRatio);
		
		return "theme/display::themeDisplay";
	}

}
