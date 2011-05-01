package com.github.gimmi.extdirectservlet;

import com.google.gson.*;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertSame;
import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DirectRequestProcessorTest {
	Method sampleMethod;
	DirectRequestProcessor target;
	DirectMethodFinder directMethodFinder;

	@Before
	public void setUp() throws Exception {
		sampleMethod = this.getClass().getDeclaredMethod("sampleMethod", Integer.class, String.class, Boolean.class, String[].class, JsonElement.class);

		directMethodFinder = mock(DirectMethodFinder.class);
		target = new DirectRequestProcessor(directMethodFinder);
	}

	@Test
	public void should_decode_simple_parameters() throws Exception {
		when(directMethodFinder.getDirectMethod(getClass(), "sampleMethod")).thenReturn(sampleMethod);
		JsonArray data = new JsonArray();
		data.add(new JsonPrimitive(123));
		data.add(new JsonPrimitive("a string"));
		data.add(new JsonPrimitive(false));
		JsonArray p4 = new JsonArray();
		p4.add(new JsonPrimitive("str"));
		data.add(p4);
		JsonObject p5 = new JsonObject();
		data.add(p5);

		Object[] actual = target.getInvocationArgs(new Gson(), sampleMethod, data);

		assertEquals(5, actual.length);
		assertEquals(123, actual[0]);
		assertEquals("a string", actual[1]);
		assertEquals(false, actual[2]);
		assertArrayEquals(new String[]{"str"}, (Object[]) actual[3]);
		assertSame(p5, actual[4]);
	}

	private void sampleMethod(Integer p1, String p2, Boolean p3, String[] p4, JsonElement p5) {
	}
}
