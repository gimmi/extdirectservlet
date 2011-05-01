package com.github.gimmi.extdirectservlet;

import com.google.gson.JsonArray;

class DirectRequest {
	public String action;
	public String method;
	public String type;
	public Integer tid;
	public JsonArray data;
}
