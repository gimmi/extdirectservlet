package com.github.gimmi.extdirectservlet;

import com.google.gson.JsonElement;

class DirectResponse {
	public final String action;
	public final String method;
	public final Integer tid;
	public String type;
	public String message;
	public String where;
	public JsonElement result;

	public DirectResponse(DirectRequest request) {
		this.action = request.action;
		this.method = request.method;
		this.tid = request.tid;
		this.type = request.type;
	}

	public void setResult(JsonElement result) {
		this.result = result;
	}

	public void setException(String message, String where) {
		this.type = "exception";
		this.message = message;
		this.where = where;
	}
}
