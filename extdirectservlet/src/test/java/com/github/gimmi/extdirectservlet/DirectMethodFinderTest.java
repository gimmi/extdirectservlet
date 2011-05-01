package com.github.gimmi.extdirectservlet;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class DirectMethodFinderTest {
	DirectMethodFinder target;

	@Before
	public void setUp() throws Exception {
		target = new DirectMethodFinder();
	}

	public static class TestClassBase {
		private void privateBaseMethod() {
		}

		protected void protectedBaseMethod() {
		}

		public void publicBaseMethod() {
		}

		void packageBaseMethod() {
		}

		@ExtDirect
		private void privateDirectBaseMethod() {
		}

		@ExtDirect
		protected void protectedDirectBaseMethod() {
		}

		@ExtDirect
		public void publicDirectBaseMethod() {
		}

		@ExtDirect
		void packageDirectBaseMethod() {
		}
	}

	public static interface TestInterface {
		void interfaceMethod();
		@ExtDirect
		void interfaceDirectMethod();
	}

	public static class TestClass extends TestClassBase implements TestInterface {
		private void privateMethod() {
		}

		protected void protectedMethod() {
		}

		public void publicMethod() {
		}

		void packageMethod() {
		}

		@ExtDirect
		private void privateDirectMethod() {
		}

		@ExtDirect
		protected void protectedDirectMethod() {
		}

		@ExtDirect
		public void publicDirectMethod() {
		}

		@ExtDirect
		void packageDirectMethod() {
		}

		@Override
		public void interfaceMethod() {
		}

		@Override
		public void interfaceDirectMethod() {
		}
	}

	@Test
	public void should_consider_only_decorated_declared_methods() throws Exception {
		Map<String, Method> actual = target.getDirectMethodMap(TestClass.class);
		assertEquals(4, actual.size());
		assertTrue(actual.containsKey("privateDirectMethod"));
		assertTrue(actual.containsKey("protectedDirectMethod"));
		assertTrue(actual.containsKey("publicDirectMethod"));
		assertTrue(actual.containsKey("packageDirectMethod"));
	}
}
