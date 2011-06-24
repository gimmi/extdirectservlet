package com.github.gimmi.extdirectservlet;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;

class DirectRequestProcessor {
	private final DirectMethodFinder directMethodFinder;

	public DirectRequestProcessor(DirectMethodFinder directMethodFinder) {
		this.directMethodFinder = directMethodFinder;
	}

	public DirectResponse process(Gson gson, Object actionInstance, DirectRequest req) {
		DirectResponse resp = new DirectResponse(req);
		Method method = directMethodFinder.getDirectMethod(actionInstance.getClass(), req.method);
		try {
			Object[] args = getInvocationArgs(gson, method, req.data);
			Object returnValue = method.invoke(actionInstance, args);
			resp.setResult(gson.toJsonTree(returnValue, method.getReturnType()));
		} catch (java.lang.reflect.InvocationTargetException tie) {
			Throwable e = tie.getCause();
			resp.setException(e.getMessage(), getStackTrace(e));
		} catch (Throwable e) {
			resp.setException(e.getMessage(), getStackTrace(e));
		}
		return resp;
	}

	Object[] getInvocationArgs(Gson gson, Method method, JsonArray data) {
		Class<?>[] parameterTypes = method.getParameterTypes();
		Object[] paramValues = new Object[parameterTypes.length];
		for (int i = 0; i < parameterTypes.length; i++) {
			Class<?> type = parameterTypes[i];
			paramValues[i] = (JsonElement.class.isAssignableFrom(type) ? data.get(i) : gson.fromJson(data.get(i), type));
		}
		return paramValues;
	}

	public static String getStackTrace(Throwable throwable) {
		Writer result = new StringWriter();
		throwable.printStackTrace(new PrintWriter(result));
		return result.toString();
	}
}
