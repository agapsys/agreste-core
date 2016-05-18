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

package com.agapsys.agreste;

import com.agapsys.rcf.LazyInitializer;
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

public class ParamMapSerializer {
	// CLASS SCOPE =============================================================
	public static class SerializerException extends Exception {

		public SerializerException() {}
		
		public SerializerException(String message, Object...args) {
			super(args.length > 0 ? String.format(message, args) : message);
		}
	}
	
	public static interface TypeSerializer<T> {
		public String toString(T srcObject);

		public T getObject(String str) throws SerializerException;
	}
	
	public abstract static class DefaultTypeSerializer<T> implements TypeSerializer<T> {

		@Override
		public String toString(T srcObject) {
			return srcObject.toString();
		}
		
	}
	
	public static class StringSerializer extends DefaultTypeSerializer<String> {

		@Override
		public String toString(String srcObject) {
			try {
				return URLEncoder.encode(srcObject, "utf-8");
			} catch (UnsupportedEncodingException ex) {
				throw new RuntimeException(ex);
			}
		}
		

		@Override
		public String getObject(String str) throws SerializerException {
			try {
				return URLDecoder.decode(str, "utf-8");
			} catch (UnsupportedEncodingException ex) {
				throw new RuntimeException(ex);
			}
		}
	}
	
	public static class BooleanSerializer extends DefaultTypeSerializer<Boolean> {

		@Override
		public Boolean getObject(String str) throws SerializerException {
			if (str == null || str.trim().isEmpty())
				return null;
			
			switch (str) {
				case "true":
					return true;
					
				case "false":
					return false;
					
				default:
					throw new SerializerException("Invalid boolean value: %s", str);
			}
		}
		
	}
	
	public static class ShortSerializer extends DefaultTypeSerializer<Short> {

		@Override
		public Short getObject(String str) throws SerializerException {
			if (str == null || str.trim().isEmpty())
				return null;
			
			try {
				return Short.parseShort(str);
			} catch (NumberFormatException ex) {
				throw new SerializerException(ex.getMessage());
			}
		}
		
	}
	
	public static class IntegerSerializer extends DefaultTypeSerializer<Integer> {

		@Override
		public Integer getObject(String str) throws SerializerException {
			if (str == null || str.trim().isEmpty())
				return null;
			
			try {
				return Integer.parseInt(str);
			} catch (NumberFormatException ex) {
				throw new SerializerException(ex.getMessage());
			}
		}
		
	}
	
	public static class LongSerializer extends DefaultTypeSerializer<Long> {

		@Override
		public Long getObject(String str) throws SerializerException {
			if (str == null || str.trim().isEmpty())
				return null;
			
			try {
				return Long.parseLong(str);
			} catch (NumberFormatException ex) {
				throw new SerializerException(ex.getMessage());
			}
		}
		
	}
	
	public static class FloatSerializer extends DefaultTypeSerializer<Float> {

		@Override
		public Float getObject(String str) throws SerializerException {
			if (str == null || str.trim().isEmpty())
				return null;
			
			try {
				return Float.parseFloat(str);
			} catch (NumberFormatException ex) {
				throw new SerializerException(ex.getMessage());
			}
		}
		
	}
	
	public static class DoubleSerializer extends DefaultTypeSerializer<Double> {

		@Override
		public Double getObject(String str) throws SerializerException {
			if (str == null || str.trim().isEmpty())
				return null;
			
			try {
				return Double.parseDouble(str);
			} catch (NumberFormatException ex) {
				throw new SerializerException(ex.getMessage());
			}
		}
		
	}
	
	public static class BigDecimalSerializer extends DefaultTypeSerializer<BigDecimal> {

		@Override
		public BigDecimal getObject(String str) throws SerializerException {
			if (str == null || str.trim().isEmpty())
				return null;
			
			try {
				return new BigDecimal(str);
			} catch (Exception ex) {
				throw new SerializerException(ex.getMessage());
			}
		}
		
	}
	
	public static class TimestampSerializer extends DefaultTypeSerializer<Date> {

		LazyInitializer<SimpleDateFormat> sdf = new LazyInitializer<SimpleDateFormat>() {

			@Override
			protected SimpleDateFormat getLazyInstance() {
				return new SimpleDateFormat(getFormatPattern());
			}
		};
		
		protected String getFormatPattern() {
			 return "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
		}
		
		@Override
		public Date getObject(String str) throws SerializerException {
			if (str == null || str.trim().isEmpty())
				return null;
			
			try {
				return sdf.getInstance().parse(str);
			} catch (ParseException ex) {
				throw new SerializerException(ex.getMessage());
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
	private final Map<Class, TypeSerializer> typeSerializerMap = new LinkedHashMap<>();
	
	public ParamMapSerializer() {
		typeSerializerMap.put(String.class, new StringSerializer());
		
		BooleanSerializer booleanSerializer = new BooleanSerializer();
		typeSerializerMap.put(Boolean.class, booleanSerializer);
		typeSerializerMap.put(boolean.class, booleanSerializer);
		
		ShortSerializer shortSerializer = new ShortSerializer();
		typeSerializerMap.put(Short.class, shortSerializer);
		typeSerializerMap.put(short.class, shortSerializer);
		
		IntegerSerializer integerSerializer = new IntegerSerializer();
		typeSerializerMap.put(Integer.class, integerSerializer);
		typeSerializerMap.put(int.class,     integerSerializer);
		
		LongSerializer longSerializer = new LongSerializer();
		typeSerializerMap.put(Long.class, longSerializer);
		typeSerializerMap.put(long.class, longSerializer);
		
		FloatSerializer floatSerializer = new FloatSerializer();
		typeSerializerMap.put(Float.class, floatSerializer);
		typeSerializerMap.put(float.class, floatSerializer);
		
		DoubleSerializer doubleSerializer = new DoubleSerializer();
		typeSerializerMap.put(Double.class, doubleSerializer);
		typeSerializerMap.put(double.class, doubleSerializer);
		
		typeSerializerMap.put(BigDecimal.class, new BigDecimalSerializer());
		typeSerializerMap.put(Date.class,       new TimestampSerializer());
	}
	
	public final void registerTypeSerializer(Class<?> type, TypeSerializer typeSerializer) {
		if (type == null)
			throw new IllegalArgumentException("Null type");
		
		if (typeSerializer == null)
			throw new IllegalArgumentException("Null type serializer");
		
		typeSerializerMap.put(type, typeSerializer);
	}
	
	public <T> T getParameter(String paramValue, Class<T> targetClass) throws SerializerException {
		TypeSerializer serializer = typeSerializerMap.get(targetClass);
			
		if (serializer == null)
			throw new RuntimeException("Missing serializer for " + targetClass.getName());

		return (T) serializer.getObject(paramValue);
	}
	
	public <T> T getObject(Map<String, String[]> paramMap, Class<T> targetClass) throws SerializerException {
		if (targetClass == null)
			throw new IllegalArgumentException("Null target class");
		
		if (paramMap == null)
			throw new IllegalArgumentException("Null field map");
		
		T targetObject;
		
		try {
			targetObject = targetClass.newInstance();			
		} catch (InstantiationException | IllegalAccessException ex) {
			throw new RuntimeException(ex);
		}
		
		for (Field field : targetClass.getFields()) {
			String value[] = paramMap.get(field.getName());
			
			if (value != null) {
				try {
					field.set(targetObject, getParameter(value[0], field.getType()));
				} catch (IllegalArgumentException | IllegalAccessException ex) {
					throw new RuntimeException(ex);
				}
			}
		}
		
		return targetObject;
	}
	
	public Map<String, String> toParamMap(Object object) {		
		if (object == null)
			throw new IllegalArgumentException("Null object");
		
		Map<String, String> map = new LinkedHashMap<>();
						
		for (Field field : object.getClass().getFields()) {
			Object fieldValue;
			
			try {
				fieldValue = field.get(object);
			} catch (IllegalArgumentException | IllegalAccessException ex) {
				throw new RuntimeException(ex);
			}
			
			if (fieldValue == null) {
				map.put(field.getName(), null);
			} else {
				TypeSerializer serializer = typeSerializerMap.get(field.getType());
			
				if (serializer == null)
					throw new RuntimeException("Missing serializer for " + field.getType().getName());
				
				map.put(field.getName(), serializer.toString(fieldValue));
			}
		}
		
		return map;
	}
	// =========================================================================
}
