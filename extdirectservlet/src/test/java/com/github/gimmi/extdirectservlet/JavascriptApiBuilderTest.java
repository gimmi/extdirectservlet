package com.github.gimmi.extdirectservlet;

import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JavascriptApiBuilderTest {
	DirectMethodFinder directMethodFinder;
	JavascriptApiBuilder target;

	@Before
	public void setUp() throws Exception {
		directMethodFinder = mock(DirectMethodFinder.class);
		target = new JavascriptApiBuilder(directMethodFinder);
	}

	@Test
	public void should_build_api_as_expected() throws Exception {
		Writer result = new StringWriter();
		setupMethodStub(getClass());
		target.write(new Gson(), getClass(), "http://localhost/app/rpc", "Ns.ClassName", new PrintWriter(result));
		assertEquals("Ext.ns('Ns');\nNs.REMOTING_API = {'id':'Ns','url':'http://localhost/app/rpc','type':'remoting','namespace':'Ns','actions':{'ClassName':[{'name':'sampleMethod','len':2}]}};", result.toString().replace('"', '\''));
	}

	@Test
	public void should_build_api_as_expected_without_namespace() throws Exception {
		Writer result = new StringWriter();
		setupMethodStub(getClass());
		target.write(new Gson(), getClass(), "http://localhost/app/rpc", "ClassName", new PrintWriter(result));
		assertEquals("Ext.ns('Ext.app');\nExt.app.REMOTING_API = {'url':'http://localhost/app/rpc','type':'remoting','actions':{'ClassName':[{'name':'sampleMethod','len':2}]}};", result.toString().replace('"', '\''));
	}

	@Test
	public void should_break_name_in_action_and_namespace() throws Exception {
		String[] actual;

		actual = target.breakName("name.space.Action", getClass());
		assertEquals("name.space", actual[0]);
		assertEquals("Action", actual[1]);
		
		actual = target.breakName("Action", getClass());
		assertNull(actual[0]);
		assertEquals("Action", actual[1]);

		actual = target.breakName(null, getClass());
		assertNull(actual[0]);
		assertEquals(getClass().getSimpleName(), actual[1]);
	}

	private void sampleMethod(Object par1, Object par2) {
	}

	private void setupMethodStub(Class<?> clazz) throws NoSuchMethodException {
		Map<String, Method> map = new HashMap<String, Method>();
		map.put("sampleMethod", clazz.getDeclaredMethod("sampleMethod", Object.class, Object.class));
		when(directMethodFinder.getDirectMethods(clazz)).thenReturn(map.values());
	}
}
