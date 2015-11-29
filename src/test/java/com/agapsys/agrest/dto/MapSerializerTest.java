/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agrest.dto;

import com.agapsys.agreste.dto.MapSerializer;
import com.agapsys.agreste.dto.MapSerializer.SerializerException;
import com.agapsys.agreste.servlets.BaseServlet;
import com.agapsys.http.HttpGet;
import com.agapsys.http.HttpResponse;
import com.agapsys.sevlet.test.ApplicationContext;
import com.agapsys.sevlet.test.ServletContainer;
import com.agapsys.web.action.dispatcher.HttpExchange;
import com.agapsys.web.action.dispatcher.WebAction;
import com.agapsys.web.toolkit.BadRequestException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletResponse;
import org.junit.Assert;
import org.junit.Test;

public class MapSerializerTest {
	// CLASS SCOPE =============================================================
	// Classes -----------------------------------------------------------------
	public static class TestDto {
		public String     strField;
		public Boolean    booleanObjectField;
		public boolean    booleanField;
		public Short      shortObjectField;
		public short      shortField;
		public Integer    integerObjectField;
		public int        integerField;
		public Long       longObjectField;
		public long       longField;
		public Float      floatObjectField;
		public float      floatField;
		public Double     doubleObjectField;
		public double     doubleField;
		public Date       dateField;
		public BigDecimal bigDecimalfield;
		public UUID       uuidField;

		@Override
		public int hashCode() {
			int hash = 7;
			hash = 19 * hash + Objects.hashCode(this.strField);
			hash = 19 * hash + Objects.hashCode(this.booleanObjectField);
			hash = 19 * hash + (this.booleanField ? 1 : 0);
			hash = 19 * hash + Objects.hashCode(this.shortObjectField);
			hash = 19 * hash + this.shortField;
			hash = 19 * hash + Objects.hashCode(this.integerObjectField);
			hash = 19 * hash + this.integerField;
			hash = 19 * hash + Objects.hashCode(this.longObjectField);
			hash = 19 * hash + (int) (this.longField ^ (this.longField >>> 32));
			hash = 19 * hash + Objects.hashCode(this.floatObjectField);
			hash = 19 * hash + Float.floatToIntBits(this.floatField);
			hash = 19 * hash + Objects.hashCode(this.doubleObjectField);
			hash = 19 * hash + (int) (Double.doubleToLongBits(this.doubleField) ^ (Double.doubleToLongBits(this.doubleField) >>> 32));
			hash = 19 * hash + Objects.hashCode(this.dateField);
			hash = 19 * hash + Objects.hashCode(this.bigDecimalfield);
			hash = 19 * hash + Objects.hashCode(this.uuidField);
			return hash;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final TestDto other = (TestDto) obj;
			if (!Objects.equals(this.strField, other.strField)) {
				return false;
			}
			if (!Objects.equals(this.booleanObjectField, other.booleanObjectField)) {
				return false;
			}
			if (this.booleanField != other.booleanField) {
				return false;
			}
			if (!Objects.equals(this.shortObjectField, other.shortObjectField)) {
				return false;
			}
			if (this.shortField != other.shortField) {
				return false;
			}
			if (!Objects.equals(this.integerObjectField, other.integerObjectField)) {
				return false;
			}
			if (this.integerField != other.integerField) {
				return false;
			}
			if (!Objects.equals(this.longObjectField, other.longObjectField)) {
				return false;
			}
			if (this.longField != other.longField) {
				return false;
			}
			if (!Objects.equals(this.floatObjectField, other.floatObjectField)) {
				return false;
			}
			if (Float.floatToIntBits(this.floatField) != Float.floatToIntBits(other.floatField)) {
				return false;
			}
			if (!Objects.equals(this.doubleObjectField, other.doubleObjectField)) {
				return false;
			}
			if (Double.doubleToLongBits(this.doubleField) != Double.doubleToLongBits(other.doubleField)) {
				return false;
			}
			if (!Objects.equals(this.dateField, other.dateField)) {
				return false;
			}
			if (!Objects.equals(this.bigDecimalfield, other.bigDecimalfield)) {
				return false;
			}
			if (!Objects.equals(this.uuidField, other.uuidField)) {
				return false;
			}
			return true;
		}
	}
	
	public static class UUIDFieldSerializer implements MapSerializer.FieldSerializer<UUID> {

		@Override
		public String toString(UUID srcObject) {
			
			return String.format("%s|%s", srcObject.getLeastSignificantBits(), srcObject.getMostSignificantBits());
		}

		@Override
		public UUID getObject(String str) {
			String[] tokens = str.split(Pattern.quote("|"));
			return new UUID(Long.parseLong(tokens[1]), Long.parseLong(tokens[0]));
		}
		
	}

	public static class CustomMapSerializer extends MapSerializer {

		public CustomMapSerializer() {
			super();
			registerSerializer(UUID.class, new UUIDFieldSerializer());
			registerSerializer(Date.class, new MapSerializer.SimpleDateSerializer());
		}
		
		public String toString(Object obj) {		
			if (obj == null)
				throw new IllegalArgumentException("Null object");

			Map<String, String> map = toMap(obj);
			
			StringBuilder sb = new StringBuilder();

			boolean first = true;
			for (Map.Entry<String, String> entry : map.entrySet()) {

				if (entry.getValue() != null) {

					if (!first)
						sb.append("&");

					sb.append(String.format("%s=%s", entry.getKey(), entry.getValue()));
					first = false;
				}
			}

			return sb.toString();
		}
		
		public <T> T getObject(String str, Class<T> targetClass) throws SerializerException {
			Map<String, String> fieldMap = new LinkedHashMap<>();
		
			String[] tokens = str.split(Pattern.quote("&"));
			for (String token : tokens) {
				token = token.trim();
				if (token.isEmpty())
					throw new RuntimeException("Invalid string");

				String[] tokenElements = token.split(Pattern.quote("="));
				if (tokenElements.length != 2)
					throw new RuntimeException("Invalid string");

				fieldMap.put(tokenElements[0].trim(), tokenElements[1].trim());
			}

			return getObject(fieldMap, targetClass);
		}
	}
	
	@WebServlet("/*")
	public static class TestServlet extends BaseServlet {
		
		@Override
		protected MapSerializer _getMapSerializer() {
			return new CustomMapSerializer();
		}
		
		@WebAction(mapping = "/get")
		public void onGet(HttpExchange exchange) throws IOException, BadRequestException {
			TestDto dto = getParameterDto(exchange, TestDto.class);
			CustomMapSerializer mapSerializer = (CustomMapSerializer) getMapSerializer();
			exchange.getResponse().getWriter().print(mapSerializer.toString(dto));
		}
	}
	// -------------------------------------------------------------------------
	
	// Utility methods ---------------------------------------------------------
	public static Date getSimpleDate(String str) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return sdf.parse(str);
		} catch (ParseException ex) {
			throw new RuntimeException(ex);
		}
	}
	// -------------------------------------------------------------------------
	// =========================================================================

	// INSTANCE SCOPE ==========================================================
	@Test
	public void customSerializerTest() throws SerializerException {
		CustomMapSerializer mapSerializer = new CustomMapSerializer();
		mapSerializer.registerSerializer(UUID.class, new UUIDFieldSerializer());
		
		TestDto dto = new TestDto();
		dto.uuidField = new UUID(1, 2);
		String serialized = mapSerializer.toString(dto);
		Assert.assertEquals("booleanField=false&shortField=0&integerField=0&longField=0&floatField=0.0&doubleField=0.0&uuidField=2|1", serialized);
		Assert.assertEquals(dto, mapSerializer.getObject(serialized, TestDto.class));
		
		serialized = "dateField=2015-11-28";
		dto = mapSerializer.getObject(serialized, TestDto.class);
		Assert.assertEquals(getSimpleDate("2015-11-28"), dto.dateField);		
	}
	
	@Test
	public void stringSerializationTest() throws SerializerException {
		CustomMapSerializer mapSerializer = new CustomMapSerializer();
		TestDto dto = new TestDto();
		dto.strField = "Hello World! áéíóú";
		String serialized = mapSerializer.toString(dto);
		Assert.assertEquals("strField=Hello+World%21+%C3%A1%C3%A9%C3%AD%C3%B3%C3%BA&booleanField=false&shortField=0&integerField=0&longField=0&floatField=0.0&doubleField=0.0", serialized);
		Assert.assertEquals(dto, mapSerializer.getObject(serialized, TestDto.class));
	}
	
	@Test
	public void testServlet () {
		ServletContainer sc = new ServletContainer();
		ApplicationContext ac = new ApplicationContext();
		ac.registerServlet(TestServlet.class);
		sc.registerContext(ac);
		sc.startServer();
		
		HttpResponse.StringResponse resp = sc.doRequest(new HttpGet("/get?uuidField=%s&dateField=%s&strField=%s", "2|1", "2015-11-28", "Hello+World áéíóú"));
		Assert.assertEquals(HttpServletResponse.SC_OK, resp.getStatusCode());
		Assert.assertEquals("strField=Hello+World+%C3%A1%C3%A9%C3%AD%C3%B3%C3%BA&booleanField=false&shortField=0&integerField=0&longField=0&floatField=0.0&doubleField=0.0&dateField=Sat Nov 28 00:00:00 BRT 2015&uuidField=2|1", resp.getContentString());
		
		sc.stopServer();
	}
	// =========================================================================
}