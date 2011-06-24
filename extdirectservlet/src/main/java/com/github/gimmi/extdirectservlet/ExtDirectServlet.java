package com.github.gimmi.extdirectservlet;

import com.google.gson.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public abstract class ExtDirectServlet extends HttpServlet {
	private final JavascriptApiBuilder javascriptApiBuilder;
	private final DirectRequestProcessor directRequestProcessor;

	protected ExtDirectServlet() {
		this(new JavascriptApiBuilder(new DirectMethodFinder()), new DirectRequestProcessor(new DirectMethodFinder()));
	}

	protected ExtDirectServlet(JavascriptApiBuilder javascriptApiBuilder, DirectRequestProcessor directRequestProcessor) {
		this.javascriptApiBuilder = javascriptApiBuilder;
		this.directRequestProcessor = directRequestProcessor;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PrintWriter writer = buildResponseWriter(resp, "text/javascript", "UTF-8");
		javascriptApiBuilder.write(getGson(), getClass(), req.getRequestURI(), getNamespace(), getActionName(), writer);
	}

	public String getNamespace() {
		return null;
	}

	public String getActionName(){
		return getClass().getSimpleName();
	}

	public Gson getGson() {
		return new Gson();
	}

	private Object getActionInstance(String action) {
		return this;
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		JsonElement jsonReq = new JsonParser().parse(req.getReader());
		JsonElement jsonResp = processBatchRequest(jsonReq);
		getGson().toJson(jsonResp, buildResponseWriter(resp, "application/json", "UTF-8"));
	}

	private JsonElement processBatchRequest(JsonElement json) {
		if (json.isJsonArray()) {
			JsonArray ret = new JsonArray();
			for (JsonElement item : json.getAsJsonArray()) {
				ret.add(processRequest(item.getAsJsonObject()));
			}
			return ret;
		} else {
			return processRequest(json.getAsJsonObject());
		}
	}

	private JsonElement processRequest(JsonObject reqJson) {
		DirectRequest req = new DirectRequest();
		req.action = reqJson.get("action").getAsString();
		req.method = reqJson.get("method").getAsString();
		req.type = reqJson.get("type").getAsString();
		req.tid = reqJson.get("tid").getAsInt();
		req.data = (reqJson.get("data").isJsonArray() ? reqJson.get("data").getAsJsonArray() : new JsonArray());

		DirectResponse resp = directRequestProcessor.process(getGson(), getActionInstance(req.action), req);

		JsonObject respJson = new JsonObject();
		respJson.addProperty("action", resp.action);
		respJson.addProperty("method", resp.method);
		respJson.addProperty("type", resp.type);
		respJson.addProperty("tid", resp.tid);
		respJson.addProperty("message", resp.message);
		respJson.addProperty("where", resp.where);
		respJson.add("result", resp.result);
		return respJson;
	}

	public static PrintWriter buildResponseWriter(HttpServletResponse response, String contentType, String charset) throws IOException {
		response.setStatus(HttpServletResponse.SC_OK);
		// See http://stackoverflow.com/questions/3613161/tomcat-server-file-download-problem-with-encoding
		response.setCharacterEncoding(charset);
		response.setContentType(contentType + ";charset=" + charset);
		return response.getWriter();
	}
}
