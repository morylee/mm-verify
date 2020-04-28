package org.mm.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.mm.core.config.Resources;
import org.mm.core.exception.IllegalParameterException;

/**
 * Map转对象工具类
 * @author LiChenhui
 *
 */
public final class Map2ModelUtil {
	private Map2ModelUtil() {}
	
	private static final Logger logger = LogManager.getLogger("Map2ModelUtil");
	
	public static final <K> K convert(Class<K> T, Map<String, Object> params) {
		try {
			K obj = T.newInstance();
			Set<Method> methodSet = getMethods(T);
			Iterator<Method> methodIt = methodSet.iterator();
			while (methodIt.hasNext()) {
				Method method = methodIt.next();
				String key = method.getName().substring(3, 4).toLowerCase() + method.getName().substring(4);
				Object value = params.get(key);
				Class<?>[] type = method.getParameterTypes();
				Object[] param_value = new Object[] { TypeParseUtil.convert(value, type[0], null) };
				method.invoke(obj, param_value);
			}
			return obj;
		} catch (Exception e) {
			logger.error("Map转换对象错误", e);
			System.out.println(e.getMessage());
			throw new IllegalParameterException(Resources.getMessage("DATA_CONVERT_ERROR", ""));
		} 
	}
	
	/**
	 * 取全部Set方法
	 *
	 * @param T
	 * @return
	 */
	public static final Set<Method> getMethods(Class<?> T) {
		Method[] methods = T.getMethods();
		Set<Method> methodSet = new HashSet<>();
		for (Method method: methods) {
			if (method.getName().startsWith("set")) methodSet.add(method);
		}
		return methodSet;
	}
	
	/**
	 * 取自定义Set方法
	 *
	 * @param T
	 * @return
	 */
	public static final Set<Method> getSetDeclaredMethods(Class<?> T) {
		Method[] methods = T.getMethods();
		Set<Method> methodSet = new HashSet<Method>();
		for (Method method : methods) {
			if (method.getName().startsWith("set")) methodSet.add(method);
		}
		return methodSet;
	}

	/**
	 * 取自定义get方法
	 * 
	 * @param T
	 * @return
	 */
	public static final Set<Method> getGetDeclaredMethods(Class<?> T) {
		Method[] methods = T.getDeclaredMethods();
		Set<Method> methodSet = new HashSet<Method>();
		for (Method method : methods) {
			if (method.getName().startsWith("get")) methodSet.add(method);
		}
		return methodSet;
	}
	
	/**
	 * 根据传递的参数修改数据
	 * 
	 * @param o
	 * @param parameterMap map参数
	 */
	public static final void covertObjWithMap(Object o, Map<String, Object> params) {
		Class<?> clazz = o.getClass();
		Iterator<Map.Entry<String, Object>> iterator = params.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, Object> entry = iterator.next();
			String key = entry.getKey().trim();
			Object value = entry.getValue();
			try {
				Method method = setMethod(key, clazz);
				if (method != null) {
					Class<?>[] parameterTypes = method.getParameterTypes();
					if (method != null) {
						Object[] param_value = new Object[] { TypeParseUtil.convert(value, parameterTypes[0], null) };
						method.invoke(o, param_value);
					}
				}
			} catch (Exception e) {
				logger.error("Map为对象赋值错误", e);
			}
		}
	}
	
	public static final Method setMethod(String fieldName, Class<?> clazz) {
		try {
			Class<?>[] parameterTypes = new Class[1];
			Field field = clazz.getDeclaredField(fieldName);
			parameterTypes[0] = field.getType();
			StringBuffer sb = new StringBuffer();
			sb.append("set");
			sb.append(fieldName.substring(0, 1).toUpperCase());
			sb.append(fieldName.substring(1));
			Method method = clazz.getMethod(sb.toString(), parameterTypes);
			return method;
		} catch (Exception e) {
			logger.error("生成自定义Set方法错误", e);
		}
		return null;
	}
	
	public static final Method getMethod(String fieldName, Class<?> clazz) {
		StringBuffer sb = new StringBuffer();
		sb.append("get");
		sb.append(fieldName.substring(0, 1).toUpperCase());
		sb.append(fieldName.substring(1));
		try {
			return clazz.getMethod(sb.toString());
		} catch (Exception e) {
			logger.error("生成自定义Get方法错误", e);
		}
		return null;
	}
}
