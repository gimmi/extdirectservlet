package com.github.gimmi.extdirectservlet;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;

class JavascriptApiBuilder {
	private final DirectMethodFinder directMethodFinder;

	public JavascriptApiBuilder(DirectMethodFinder directMethodFinder) {
		this.directMethodFinder = directMethodFinder;
	}

	public void write(Gson gson, Class<?> clazz, String url, String namespace, String actionName, PrintWriter writer) throws IOException {
		JsonObject api = new JsonObject();
		// Setting id property is useful for getting provider reference, for example:
		// var provider = Ext.direct.Manager.getProvider('Namespace.ActionName')
		// provider.on('exception', function(){ alert('provider exception'); });
		api.addProperty("id", namespace);
		api.addProperty("url", url);
		api.addProperty("type", "remoting");
		api.addProperty("namespace", namespace);
		JsonObject actions = new JsonObject();
		api.add("actions", actions);
		JsonArray methods = new JsonArray();
		for (Method method : directMethodFinder.getDirectMethods(clazz)) {
			JsonObject obj = new JsonObject();
			obj.addProperty("name", method.getName());
			obj.addProperty("len", method.getParameterTypes().length);
			methods.add(obj);
		}
		actions.add(actionName, methods);
		writer.format("Ext.ns('%1$s');\n%1$s.REMOTING_API = ", namespace == null ? "Ext.app" : namespace);
		gson.toJson(api, writer);
		writer.write(";");
	}

	String[] breakName(String className) {
		int index = className.lastIndexOf('.');
		if (index == -1) {
			return new String[]{null, className};
		}
		return new String[]{className.substring(0, index), className.substring(index + 1)};
	}
}
