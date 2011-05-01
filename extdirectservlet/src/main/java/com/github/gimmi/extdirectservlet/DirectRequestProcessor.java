package com.github.gimmi.extdirectservlet;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;

class DirectRequestProcessor {
	private final DirectMethodFinder directMethodFinder;
	private final Gson gson;

	public DirectRequestProcessor(DirectMethodFinder directMethodFinder, Gson gson) {
		this.directMethodFinder = directMethodFinder;
		this.gson = gson;
	}

	public DirectResponse process(ExtDirectServlet servlet, DirectRequest req) {
		DirectResponse resp = new DirectResponse(req);
		Method method = directMethodFinder.getDirectMethod(servlet.getClass(), req.method);
		try {
			Object[] args = getInvocationArgs(method, req.data);
			Object returnValue = method.invoke(servlet, args);
			resp.setResult(gson.toJsonTree(returnValue, method.getReturnType()));
		} catch (java.lang.reflect.InvocationTargetException tie) {
			Throwable e = tie.getCause();
			resp.setException(e.getMessage(), getStackTrace(e));
		} catch (Throwable e) {
			resp.setException(e.getMessage(), getStackTrace(e));
		}
		return resp;
	}

	private Object[] getInvocationArgs(Method method, JsonArray data) {
		Class<?>[] parameterTypes = method.getParameterTypes();
		Object[] paramValues = new Object[parameterTypes.length];
		for (int i = 0; i < parameterTypes.length; i++) {
			paramValues[i] = gson.fromJson(data.get(i), parameterTypes[i]);
		}
		return paramValues;
	}

	public static String getStackTrace(Throwable throwable) {
		Writer result = new StringWriter();
		throwable.printStackTrace(new PrintWriter(result));
		return result.toString();
	}
}
