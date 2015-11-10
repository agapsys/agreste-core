/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agrest.dto;

import com.agapsys.agreste.dto.AbstractDto;
import com.agapsys.agreste.dto.AbstractDto.DtoValidationException;
import com.agapsys.agreste.dto.DtoRequired;
import org.junit.Assert;
import org.junit.Test;

public class AbstractDtoTest {
	// CLASS SCOPE =============================================================
	public static class OptionalDto extends AbstractDto {
		public Integer intField;
		public String strField;
	}
	
	public static class RequiredDto extends AbstractDto {
		@DtoRequired
		public Integer intField;
		
		@DtoRequired
		public String strField;
	}
	
	public static class PartiallyRequiredDto1 extends AbstractDto {
		public Integer intField;
		
		@DtoRequired
		public String strField;
	}
	
	public static class PartiallyRequiredDto2 extends AbstractDto {
		public Integer intField;
		
		@DtoRequired(acceptEmpty = true)
		public String strField;
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
		
		validationError = null;
		dto.intField = 2;
		
		try {
			dto.validate();
		} catch (DtoValidationException ex) {
			validationError = ex;
		}
		
		Assert.assertNotNull(validationError);
		Assert.assertEquals("Required field: strField", validationError.getMessage());
		
		validationError = null;
		dto.strField = "";
		
		try {
			dto.validate();
		} catch (DtoValidationException ex) {
			validationError = ex;
		}
		
		Assert.assertNotNull(validationError);
		Assert.assertEquals("Required field: strField", validationError.getMessage());
		
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
	public void PartiallyRequired1() {
		PartiallyRequiredDto1 dto = new PartiallyRequiredDto1();
		DtoValidationException validationError = null;
		
		try {
			dto.validate();
		} catch (DtoValidationException ex) {
			validationError = ex;
		}
		
		Assert.assertNotNull(validationError);
		Assert.assertEquals("Required field: strField", validationError.getMessage());
		
		validationError = null;
		dto.strField = "";
		
		try {
			dto.validate();
		} catch (DtoValidationException ex) {
			validationError = ex;
		}
		
		Assert.assertNotNull(validationError);
		Assert.assertEquals("Required field: strField", validationError.getMessage());
		
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
	public void PartiallyRequired2() {
		PartiallyRequiredDto2 dto = new PartiallyRequiredDto2();
		DtoValidationException validationError = null;
		
		try {
			dto.validate();
		} catch (DtoValidationException ex) {
			validationError = ex;
		}
		
		Assert.assertNotNull(validationError);
		Assert.assertEquals("Required field: strField", validationError.getMessage());
		
		validationError = null;
		dto.strField = "";
		
		try {
			dto.validate();
		} catch (DtoValidationException ex) {
			validationError = ex;
		}
		
		Assert.assertNull(validationError);
	}
	// =========================================================================
}
