package org.mm.core.util;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mm.core.config.Resources;
import org.mm.core.exception.IllegalParameterException;

public class AssertUtil {

	public static void isNull(Object object, String key) {
		if (object == null) {
			throw new IllegalParameterException(message("PARAM_IS_NULL", message(key)));
		}
	}
	
	public static void isBlank(String str, String key) {
		if (str == null || "".equals(str.trim())) {
			throw new IllegalParameterException(message("PARAM_IS_BLANK", message(key)));
		}
	}
	
	/** 允许最小值 */
	public static void min(Integer value, Integer min, String key) {
		if (value < min) {
			throw new IllegalArgumentException(message("INT_VALUE_MIN", message(key), min));
		}
	}
	
	/** 允许最大值 */
	public static void max(Integer value, Integer max, String key) {
		if (value > max) {
			throw new IllegalArgumentException(message("INT_VALUE_MAX", message(key), max));
		}
	}
	
	/** 允许值范围 */
	public static void range(Integer value, Integer min, Integer max, String key) {
		min(value, min, key);
		max(value, max, key);
	}
	
	/** 允许最小值 */
	public static void min(Float value, Float min, String key) {
		if (value < min) {
			throw new IllegalArgumentException(message("DOUBLE_VALUE_MIN", message(key), min));
		}
	}
	
	/** 允许最大值 */
	public static void max(Float value, Float max, String key) {
		if (value > max) {
			throw new IllegalArgumentException(message("DOUBLE_VALUE_MAX", message(key), max));
		}
	}
	
	/** 允许值范围 */
	public static void range(Float value, Float min, Float max, String key) {
		min(value, min, key);
		max(value, max, key);
	}
	
	/** 允许最小值 */
	public static void min(Double value, Double min, String key) {
		if (value < min) {
			throw new IllegalArgumentException(message("DOUBLE_VALUE_MIN", message(key), min));
		}
	}
	
	/** 允许最大值 */
	public static void max(Double value, Double max, String key) {
		if (value > max) {
			throw new IllegalArgumentException(message("DOUBLE_VALUE_MAX", message(key), max));
		}
	}
	
	/** 允许值范围 */
	public static void range(Double value, Double min, Double max, String key) {
		min(value, min, key);
		max(value, max, key);
	}
	
	public static void length(String str, Integer min, Integer max, String key) {
		if (min != null && str.length() < min) {
			throw new IllegalParameterException(message("PARAM_LENGTH_LESS_THAN", message(key), min));
		}
		if (max != null && str.length() > max) {
			throw new IllegalParameterException(message("PARAM_LENGTH_MORE_THAN", message(key), max));
		}
	}
	
	public static void length(List<Long> list, Integer min, Integer max, String key) {
		if (min != null && list.size() < min) {
			throw new IllegalParameterException(message("PARAM_SIZE_LESS_THAN", message(key), min));
		}
		if (max != null && list.size() > max) {
			throw new IllegalParameterException(message("PARAM_SIZE_MORE_THAN", message(key), max));
		}
	}
	
	public static void equal(String value, String checkValue, String key) {
		if (!value.equals(checkValue)) {
			throw new IllegalParameterException(message("TWO_VALUE_NOT_EQUAL", message(key)));
		}
	}
	
	public static void match(String value, String checkValue, String key) {
		if (!value.equals(checkValue)) {
			throw new IllegalParameterException(message("TWO_VALUE_NOT_MATCH", message(key)));
		}
	}
	
	public static void moreThan(Date date, Date compareDate, String dateKey, String compareKey) {
		if (date != null && compareDate != null) {
			if (date.getTime() > compareDate.getTime()) {
				throw new IllegalParameterException(message("DATE_MORE_THAN_COMPARE_DATE", message(dateKey), message(compareKey)));
			}
		}
	}
	
	public static void contains(Integer item, List<Integer> list, String key) {
		if (!list.contains(item)) {
			throw new IllegalParameterException(message("PARAM_NOT_IN_RANGE", message(key)));
		}
	}
	
	public static void contains(Double item, List<Double> list, String key) {
		if (!list.contains(item)) {
			throw new IllegalParameterException(message("PARAM_NOT_IN_RANGE", message(key)));
		}
	}
	
	public static void name(String name, String key) {
		String regex = "^[a-zA-Z0-9\u4e00-\u9fa5]{2,16}$";
		pattern(name, regex, key);
	}
	
	public static void email(String email, String key) {
		String regex = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
		pattern(email, regex, key);
	}
	
	public static void mobile(String text, String key) {
		String regex = "((^(13|15|17|18|19)[0-9]{9}|(166)[0-9]{8}$)|(^0[1,2]{1}\\d{1}-?\\d{8}$)|(^0[3-9] {1}\\d{2}-?\\d{7,8}$)|(^0[1,2]{1}\\d{1}-?\\d{8}-(\\d{1,4})$)|(^0[3-9]{1}\\d{2}-? \\d{7,8}-(\\d{1,4})$))";
		pattern(text, regex, key);
	}
	
	public static void mobileOrPhone(String text, String key){
		String phoneRegex = "^(0\\d{2}-\\d{7,8}(-\\d{1,4})?)|(0\\d{3}-\\d{7,8}(-\\d{1,4})?)$";
		String mobileRegex = "((^(13|15|17|18|19)[0-9]{9}|(166)[0-9]{8}$)|(^0[1,2]{1}\\d{1}-?\\d{8}$)|(^0[3-9] {1}\\d{2}-?\\d{7,8}$)|(^0[1,2]{1}\\d{1}-?\\d{8}-(\\d{1,4})$)|(^0[3-9]{1}\\d{2}-? \\d{7,8}-(\\d{1,4})$))";
		
		boolean isPhone = pattern(text, phoneRegex);
		boolean isMobile = pattern(text, mobileRegex);
		if (!isPhone && !isMobile) {
			throw new IllegalArgumentException(message("REGEX_ILLEGAL", message(key)));
		}
	}
	
	public static String emailOrMobile(String text, String key) {
		String emailRegex = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
		String mobileRegex = "((^(13|15|17|18|19)[0-9]{9}|(166)[0-9]{8}$)|(^0[1,2]{1}\\d{1}-?\\d{8}$)|(^0[3-9] {1}\\d{2}-?\\d{7,8}$)|(^0[1,2]{1}\\d{1}-?\\d{8}-(\\d{1,4})$)|(^0[3-9]{1}\\d{2}-? \\d{7,8}-(\\d{1,4})$))";
		
		boolean isEmail = pattern(text, emailRegex);
		boolean isMobile = pattern(text, mobileRegex);
		String flag = null;
		if (isEmail) flag = "email";
		if (isMobile) flag = "mobile";
		if (!isEmail && !isMobile) {
			throw new IllegalArgumentException(message("REGEX_ILLEGAL", message(key)));
		}
		
		return flag;
	}
	
	public static boolean pattern(String text, String regex, String key) {
		boolean result = pattern(text, regex);
		if (!result && key != null) {
			throw new IllegalArgumentException(message("REGEX_ILLEGAL", message(key)));
		}
		return result;
	}
	
	public static boolean pattern(String text, String regex) {
		boolean result = false;
		try {
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(text);
			result = matcher.matches();
		} catch (Exception e) {
			result = false;
		}
		return result;
	}
	
	public static String message(String key, Object... args) {
		return Resources.getMessage(key, args);
	}

}
