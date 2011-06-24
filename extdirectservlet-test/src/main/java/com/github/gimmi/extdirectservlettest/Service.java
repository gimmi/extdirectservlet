package com.github.gimmi.extdirectservlettest;

import com.github.gimmi.extdirectservlet.ExtDirect;
import com.github.gimmi.extdirectservlet.ExtDirectServlet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Service extends ExtDirectServlet {
	private final Logger logger = LoggerFactory.getLogger(Service.class);

	@Override
	public String getNamespace() {
		return "Ns";
	}

	@Override
	public Gson getGson() {
		return new GsonBuilder().setPrettyPrinting().create();
	}

	public static class PrimitiveTypesEchoResult {
		String stringValue;
		Integer intValue;
		Boolean boolValue;
		Double doubleValue;
	}

	@ExtDirect
	public PrimitiveTypesEchoResult primitiveTypesEcho(String stringValue, Integer intValue, Boolean boolValue, Double doubleValue) {
		logger.info("primitiveTypesEcho({}, {}, {}, {})", new Object[]{stringValue, intValue, boolValue, doubleValue});
		PrimitiveTypesEchoResult ret = new PrimitiveTypesEchoResult();
		ret.stringValue = stringValue;
		ret.intValue = intValue;
		ret.boolValue = boolValue;
		ret.doubleValue = doubleValue;
		return ret;
	}

	@ExtDirect
	public String echo(String value) {
		logger.info("echo({})", value);
		return value;
	}

	@ExtDirect
	public Boolean noParams() {
		logger.info("noParams()");
		return true;
	}

	@ExtDirect
	public void exception() {
		logger.info("exception()");
		throw new RuntimeException("exception message");
	}
}
