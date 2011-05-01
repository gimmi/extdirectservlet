package com.github.gimmi.extdirectservlet;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

class DirectMethodFinder {
	public Iterable<Method> getDirectMethods(Class<?> clazz) {
		return getDirectMethodMap(clazz).values();
	}

	public Method getDirectMethod(Class<?> clazz, String method) {
		return getDirectMethodMap(clazz).get(method);
	}

	Map<String, Method> getDirectMethodMap(Class<?> clazz) {
		Map<String, Method> ret = new HashMap<String, Method>();
		for (Method method : clazz.getDeclaredMethods()) {
			if (method.getAnnotation(ExtDirect.class) != null) {
				ret.put(method.getName(), method);
			}
		}
		return ret;
	}
}
