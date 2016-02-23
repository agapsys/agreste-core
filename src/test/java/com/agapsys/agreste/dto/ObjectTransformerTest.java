/*
 * Copyright 2016 Agapsys Tecnologia Ltda-ME.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.agapsys.agreste.dto;

import java.util.LinkedList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class ObjectTransformerTest {
	
	private static class MyDestClass {
		
		private final String str;
		
		public MyDestClass(Integer i) {
			str = "mdc_" + i * 4;
		}

		@Override
		public String toString() {
			return str;
		}
	}

	private final List<Integer> srcList;
	private final ObjectTransformer<Integer, String> intStrTransformer = new ObjectTransformer<Integer, String>() {

			@Override
			public String getFrom(Integer srcObj) {
				return String.format("hi: %d", srcObj * 3);
			}
		};
	private final CollectionFilter evenFilter = new CollectionFilter<Integer>() {

			@Override
			public boolean process(Integer srcObj) {
				return srcObj != 0 && srcObj % 2 == 0;
			}
		};
	
	public ObjectTransformerTest() {
		srcList = new LinkedList();

		for (int i = 0; i < 5; i++) {
			srcList.add(i);
		}
	}

	@Test
	public void transformWithoutFiltering() {

		final String EXPECTED_RESULT = "[hi: 0, hi: 3, hi: 6, hi: 9, hi: 12]";

		String result;

		result = ObjectTransformer.<String>getCollection(srcList, intStrTransformer).toString();
		Assert.assertEquals(EXPECTED_RESULT, result);

		result = ObjectTransformer.<String>getCollection(srcList, intStrTransformer, null).toString();
		Assert.assertEquals(EXPECTED_RESULT, result);

		result = intStrTransformer.getCollection(srcList).toString();
		Assert.assertEquals(EXPECTED_RESULT, result);

		result = intStrTransformer.getCollection(srcList, (CollectionFilter)null).toString();
		Assert.assertEquals(EXPECTED_RESULT, result);
	}

	@Test
	public void transformWithFiltering() {
		final String EXPECTED_RESULT = "[hi: 6, hi: 12]";

		String result;

		result = ObjectTransformer.<String>getCollection(srcList, intStrTransformer, evenFilter).toString();
		Assert.assertEquals(EXPECTED_RESULT, result);

		result = intStrTransformer.getCollection(srcList, evenFilter).toString();
		Assert.assertEquals(EXPECTED_RESULT, result);
	}
	
	@Test
	public void transformUsingConstructor() {
		final String EXPECTED_RESULT = "[mdc_0, mdc_4, mdc_8, mdc_12, mdc_16]";
		String result = ObjectTransformer.getCollection(MyDestClass.class, srcList).toString();
		Assert.assertEquals(EXPECTED_RESULT, result);
	}
}
