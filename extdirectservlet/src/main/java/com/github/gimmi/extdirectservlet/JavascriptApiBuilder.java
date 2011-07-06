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

	public void write(Gson gson, Class<?> clazz, String url, String fullActionName, PrintWriter writer) throws IOException {
		String[] nameParts = breakName(fullActionName, clazz);
		JsonObject api = new JsonObject();
		// Setting id property is useful for getting provider reference, for example:
		// var provider = Ext.direct.Manager.getProvider('Namespace.ActionName')
		// provider.on('exception', function(){ alert('provider exception'); });
		api.addProperty("id", nameParts[0]);
		api.addProperty("url", url);
		api.addProperty("type", "remoting");
		api.addProperty("namespace", nameParts[0]);
		JsonObject actions = new JsonObject();
		api.add("actions", actions);
		JsonArray methods = new JsonArray();
		for (Method method : directMethodFinder.getDirectMethods(clazz)) {
			JsonObject obj = new JsonObject();
			obj.addProperty("name", method.getName());
			obj.addProperty("len", method.getParameterTypes().length);
			methods.add(obj);
		}
		actions.add(nameParts[1], methods);
		writer.format("Ext.ns('%1$s');\n%1$s.REMOTING_API = ", nameParts[0] == null ? "Ext.app" : nameParts[0]);
		gson.toJson(api, writer);
		writer.write(";");
	}

	String[] breakName(String name, Class<?> clazz) {
		if (name == null) {
			name = clazz.getSimpleName();
		}
		int index = name.lastIndexOf('.');
		if (index == -1) {
			return new String[]{null, name};
		}
		return new String[]{name.substring(0, index), name.substring(index + 1)};
	}
}
