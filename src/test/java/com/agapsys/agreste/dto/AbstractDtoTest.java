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

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class AbstractDtoTest {
	// CLASS SCOPE =============================================================
	public static class OptionalDto extends AbstractDtoObject {
		public Integer intField;
		public String strField;
	}
	
	public static class RequiredDto extends AbstractDtoObject {
		@DtoRequired
		public Integer intField;
		
		@DtoRequired
		public String strField;
	}
	
	public static class PartiallyRequiredDto1 extends AbstractDtoObject {
		public Integer intField;
		
		@DtoRequired
		public String strField;
	}
	
	public static class PartiallyRequiredDto2 extends AbstractDtoObject {
		public Integer intField;
		
		@DtoRequired(acceptEmpty = true)
		public String strField;
	}
	
	public static class InnerDto extends AbstractDtoObject {
		public String strField;
		
		public RequiredDto requiredDto;
	}
	
	public static class ComplexDto extends AbstractDtoObject {
		private int intField;
		
		public InnerDto innerDto;
	}
	
	@BeforeClass
	public static void beforeClass() {
		System.out.println(String.format("=== %s ===", AbstractDtoTest.class.getSimpleName()));
	}
	
	@AfterClass
	public static void afterClass() {
		System.out.println();
	}
	// =========================================================================

	// INSTANCE SCOPE ==========================================================	
	@Test
	public void notRequired() {
		OptionalDto dto;
		DtoValidationException validationError = null;
		
		try {
			dto = new OptionalDto();
			dto.validate();
		} catch (DtoValidationException ex) {
			validationError = ex;
		}
		
		Assert.assertNull(validationError);
	}
	
	@Test
	public void required() {
		RequiredDto dto = new RequiredDto();
		DtoValidationException validationError = null;
		
		try {
			dto.validate();
		} catch (DtoValidationException ex) {
			validationError = ex;
		}
		
		Assert.assertNotNull(validationError);
		Assert.assertEquals("Required field: intField", validationError.getMessage());
		// ----------------------------
		validationError = null;
		dto.intField = 2;
		
		try {
			dto.validate();
		} catch (DtoValidationException ex) {
			validationError = ex;
		}
		
		Assert.assertNotNull(validationError);
		Assert.assertEquals("Required field: strField", validationError.getMessage());
		// ----------------------------
		validationError = null;
		dto.strField = "";
		
		try {
			dto.validate();
		} catch (DtoValidationException ex) {
			validationError = ex;
		}
		
		Assert.assertNotNull(validationError);
		Assert.assertEquals("Required field: strField", validationError.getMessage());
		// ----------------------------
		validationError = null;
		dto.strField = "test";
		
		try {
			dto.validate();
		} catch (DtoValidationException ex) {
			validationError = ex;
		}
		
		Assert.assertNull(validationError);
	}
	
	@Test
	public void partiallyRequired1() {
		PartiallyRequiredDto1 dto = new PartiallyRequiredDto1();
		DtoValidationException validationError = null;
		
		try {
			dto.validate();
		} catch (DtoValidationException ex) {
			validationError = ex;
		}
		
		Assert.assertNotNull(validationError);
		Assert.assertEquals("Required field: strField", validationError.getMessage());
		// ----------------------------
		validationError = null;
		dto.strField = "";
		
		try {
			dto.validate();
		} catch (DtoValidationException ex) {
			validationError = ex;
		}
		
		Assert.assertNotNull(validationError);
		Assert.assertEquals("Required field: strField", validationError.getMessage());
		// ----------------------------
		validationError = null;
		dto.strField = "test";
		
		try {
			dto.validate();
		} catch (DtoValidationException ex) {
			validationError = ex;
		}
		
		Assert.assertNull(validationError);
	}
	
	@Test
	public void partiallyRequired2() {
		PartiallyRequiredDto2 dto = new PartiallyRequiredDto2();
		DtoValidationException validationError = null;
		
		try {
			dto.validate();
		} catch (DtoValidationException ex) {
			validationError = ex;
		}
		
		Assert.assertNotNull(validationError);
		Assert.assertEquals("Required field: strField", validationError.getMessage());
		// ----------------------------
		validationError = null;
		dto.strField = "";
		
		try {
			dto.validate();
		} catch (DtoValidationException ex) {
			validationError = ex;
		}
		
		Assert.assertNull(validationError);
	}
	
	@Test
	public void complexDto() {
		ComplexDto dto = new ComplexDto();
		DtoValidationException validationError = null;
		
		try {
			dto.validate();
		} catch (DtoValidationException ex) {
			validationError = ex;
		}
		
		Assert.assertNull(validationError);
		// ----------------------------
		validationError = null;
		dto.innerDto = new InnerDto();
		
		try {
			dto.validate();
		} catch (DtoValidationException ex) {
			validationError = ex;
		}
		
		Assert.assertNull(validationError);
		// ----------------------------
		validationError = null;
		dto.innerDto.requiredDto = new RequiredDto();
		
		try {
			dto.validate();
		} catch (DtoValidationException ex) {
			validationError = ex;
		}
		
		Assert.assertNotNull(validationError);
		Assert.assertEquals("Required field: innerDto.requiredDto.intField", validationError.getMessage());
	}
	// =========================================================================
}
