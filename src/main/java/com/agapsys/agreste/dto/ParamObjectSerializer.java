/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agreste.dto;

import com.agapsys.web.action.dispatcher.LazyInitializer;
import com.agapsys.web.toolkit.BadRequestException;
import com.agapsys.web.toolkit.ObjectSerializer;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ParamObjectSerializer implements ObjectSerializer {
	
	// CLASS SCOPE =============================================================
	public static interface ParamSerializer<T> {
		public String toString(T srcObject);

		public T getObject(String str);
	}
	
	public abstract static class DefaultSerializer<T> implements ParamSerializer<T> {

		@Override
		public String toString(T srcObject) {
			return srcObject.toString();
		}
		
	}
	
	public static class StringSerializer extends DefaultSerializer<String> {

		@Override
		public String toString(String srcObject) {
			try {
				return URLEncoder.encode(srcObject, "utf-8");
			} catch (UnsupportedEncodingException ex) {
				throw new RuntimeException(ex);
			}
		}
		

		@Override
		public String getObject(String str) {
			try {
				return URLDecoder.decode(str, "utf-8");
			} catch (UnsupportedEncodingException ex) {
				throw new RuntimeException(ex);
			}
		}
	}
	
	public static class BooleanSerializer extends DefaultSerializer<Boolean> {

		@Override
		public Boolean getObject(String str) {
			return Boolean.parseBoolean(str);
		}
		
	}
	
	public static class ShortSerializer extends DefaultSerializer<Short> {

		@Override
		public Short getObject(String str) {
			return Short.parseShort(str);
		}
		
	}
	
	public static class IntegerSerializer extends DefaultSerializer<Integer> {

		@Override
		public Integer getObject(String str) {
			return Integer.parseInt(str);
		}
		
	}
	
	public static class LongSerializer extends DefaultSerializer<Long> {

		@Override
		public Long getObject(String str) {
			return Long.parseLong(str);
		}
		
	}
	
	public static class FloatSerializer extends DefaultSerializer<Float> {

		@Override
		public Float getObject(String str) {
			return Float.parseFloat(str);
		}
		
	}
	
	public static class DoubleSerializer extends DefaultSerializer<Double> {

		@Override
		public Double getObject(String str) {
			return Double.parseDouble(str);
		}
		
	}
	
	public static class BigDecimalSerializer extends DefaultSerializer<BigDecimal> {

		@Override
		public BigDecimal getObject(String str) {
			return new BigDecimal(str);
		}
		
	}
	
	public static class TimestampSerializer extends DefaultSerializer<Date> {

		LazyInitializer<SimpleDateFormat> sdf = new LazyInitializer<SimpleDateFormat>() {

			@Override
			protected SimpleDateFormat getLazyInstance(Object... params) {
				return new SimpleDateFormat(getFormatPattern());
			}
		};
		
		protected String getFormatPattern() {
			 return "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
		}
		
		@Override
		public Date getObject(String str) {
			try {
				return sdf.getInstance().parse(str);
			} catch (ParseException ex) {
				throw new RuntimeException(ex);
			}
		}
		
	}
	
	public static class SimpleDateSerializer extends TimestampSerializer {

		@Override
		protected String getFormatPattern() {
			return "yyyy-MM-dd";
		}
	}
	// =========================================================================

	// INSTANCE SCOPE ==========================================================
	private final Map<Class, ParamSerializer> serializerMap = new LinkedHashMap<>();
	
	public ParamObjectSerializer() {
		serializerMap.put(String.class,     new StringSerializer());
		
		BooleanSerializer booleanSerializer = new BooleanSerializer();
		serializerMap.put(Boolean.class, booleanSerializer);
		serializerMap.put(boolean.class, booleanSerializer);
		
		ShortSerializer shortSerializer = new ShortSerializer();
		serializerMap.put(Short.class, shortSerializer);
		serializerMap.put(short.class, shortSerializer);
		
		IntegerSerializer integerSerializer = new IntegerSerializer();
		serializerMap.put(Integer.class, integerSerializer);
		serializerMap.put(int.class,     integerSerializer);
		
		LongSerializer longSerializer = new LongSerializer();
		serializerMap.put(Long.class, longSerializer);
		serializerMap.put(long.class, longSerializer);
		
		FloatSerializer floatSerializer = new FloatSerializer();
		serializerMap.put(Float.class, floatSerializer);
		serializerMap.put(float.class, floatSerializer);
		
		DoubleSerializer doubleSerializer = new DoubleSerializer();
		serializerMap.put(Double.class, doubleSerializer);
		serializerMap.put(double.class, doubleSerializer);
		
		serializerMap.put(BigDecimal.class, new BigDecimalSerializer());
		serializerMap.put(Date.class,       new TimestampSerializer());
	}
	
	public ParamObjectSerializer registerSerializer(Class<?> type, ParamSerializer serializer) {
		if (type == null)
			throw new IllegalArgumentException("Null type");
		
		if (serializer == null)
			throw new IllegalArgumentException("Null type");
		
		serializerMap.put(type, serializer);
		return this;
	}
	
	private Map<String, String> getFieldMap(Map<String, String[]> requestMap) {
		Map<String, String> fieldMap = new LinkedHashMap<>();
		
		for (Map.Entry<String, String[]> entry : requestMap.entrySet()) {
			fieldMap.put(entry.getKey(), entry.getValue()[0]);
		}
		
		return fieldMap;
	}
	
	private Map<String, String> getFieldMap(String str) {
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
		
		return fieldMap;
	}
	
	public <T> T getObject(Map<String, String> fieldMap, Class<T> targetClass) {
		if (targetClass == null)
			throw new IllegalArgumentException("Null target class");
		
		if (fieldMap == null)
			throw new IllegalArgumentException("Null field map");
		
		T targetObject;
		
		try {
			targetObject = targetClass.newInstance();			
		} catch (InstantiationException | IllegalAccessException ex) {
			throw new RuntimeException(ex);
		}
		
		for (Field field : targetClass.getFields()) {
			String value = fieldMap.get(field.getName());
			
			if (value != null) {
				ParamSerializer serializer = serializerMap.get(field.getType());
			
				if (serializer == null)
					throw new RuntimeException("Missing serializer for " + field.getType().getName());
				
				try {
					field.set(targetObject, serializer.getObject(value));
				} catch (IllegalArgumentException | IllegalAccessException ex) {
					throw new RuntimeException(ex);
				}
			}
		}
		
		return targetObject;
	}
	
	public <T> T getObject(String str, Class<T> targetClass) {
		return getObject(getFieldMap(str), targetClass);
	}
	
	public String toString(Object object) {		
		if (object == null)
			throw new IllegalArgumentException("Null object");
		
		StringBuilder sb = new StringBuilder();
		
		boolean first = true;
		
		for (Field field : object.getClass().getFields()) {
			Object fieldValue;
			
			try {
				fieldValue = field.get(object);
			} catch (IllegalArgumentException | IllegalAccessException ex) {
				throw new RuntimeException(ex);
			}
			
			if (fieldValue != null) {
				ParamSerializer serializer = serializerMap.get(field.getType());
			
				if (serializer == null)
					throw new RuntimeException("Missing serializer for " + field.getType().getName());
				
				if (!first)
					sb.append("&");

				sb.append(String.format("%s=%s", field.getName(), serializer.toString(fieldValue)));
				first = false;
			}
		}
		
		return sb.toString();
	}
	
	@Override
	public <T> T readObject(HttpServletRequest req, Class<T> targetClass) throws BadRequestException {
		if (req == null)
			throw new IllegalArgumentException("Null request");
		
		Map<String, String> fieldMap = getFieldMap(req.getParameterMap());
		return getObject(fieldMap, targetClass);
	}

	@Override
	public void writeObject(HttpServletResponse resp, Object object) {
		if (resp == null)
			throw new IllegalArgumentException("Null response");
		
		String contents = toString(object);
		try {
			resp.getWriter().print(contents);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}
	// =========================================================================
}
