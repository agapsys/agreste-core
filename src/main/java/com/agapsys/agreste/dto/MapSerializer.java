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

public class MapSerializer {
	// CLASS SCOPE =============================================================
	public static class SerializerException extends Exception {

		public SerializerException() {}
		
		public SerializerException(String message, Object...args) {
			super(args.length > 0 ? String.format(message, args) : message);
		}
	}
	
	public static interface FieldSerializer<T> {
		public String toString(T srcObject);

		public T getObject(String str) throws SerializerException;
	}
	
	public abstract static class DefaultFieldSerializer<T> implements FieldSerializer<T> {

		@Override
		public String toString(T srcObject) {
			return srcObject.toString();
		}
		
	}
	
	public static class StringSerializer extends DefaultFieldSerializer<String> {

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
	
	public static class BooleanSerializer extends DefaultFieldSerializer<Boolean> {

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
	
	public static class ShortSerializer extends DefaultFieldSerializer<Short> {

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
	
	public static class IntegerSerializer extends DefaultFieldSerializer<Integer> {

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
	
	public static class LongSerializer extends DefaultFieldSerializer<Long> {

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
	
	public static class FloatSerializer extends DefaultFieldSerializer<Float> {

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
	
	public static class DoubleSerializer extends DefaultFieldSerializer<Double> {

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
	
	public static class BigDecimalSerializer extends DefaultFieldSerializer<BigDecimal> {

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
	
	public static class TimestampSerializer extends DefaultFieldSerializer<Date> {

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
	private final Map<Class, FieldSerializer> fieldSerializerMap = new LinkedHashMap<>();
	
	public MapSerializer() {
		fieldSerializerMap.put(String.class,     new StringSerializer());
		
		BooleanSerializer booleanSerializer = new BooleanSerializer();
		fieldSerializerMap.put(Boolean.class, booleanSerializer);
		fieldSerializerMap.put(boolean.class, booleanSerializer);
		
		ShortSerializer shortSerializer = new ShortSerializer();
		fieldSerializerMap.put(Short.class, shortSerializer);
		fieldSerializerMap.put(short.class, shortSerializer);
		
		IntegerSerializer integerSerializer = new IntegerSerializer();
		fieldSerializerMap.put(Integer.class, integerSerializer);
		fieldSerializerMap.put(int.class,     integerSerializer);
		
		LongSerializer longSerializer = new LongSerializer();
		fieldSerializerMap.put(Long.class, longSerializer);
		fieldSerializerMap.put(long.class, longSerializer);
		
		FloatSerializer floatSerializer = new FloatSerializer();
		fieldSerializerMap.put(Float.class, floatSerializer);
		fieldSerializerMap.put(float.class, floatSerializer);
		
		DoubleSerializer doubleSerializer = new DoubleSerializer();
		fieldSerializerMap.put(Double.class, doubleSerializer);
		fieldSerializerMap.put(double.class, doubleSerializer);
		
		fieldSerializerMap.put(BigDecimal.class, new BigDecimalSerializer());
		fieldSerializerMap.put(Date.class,       new TimestampSerializer());
	}
	
	public void registerSerializer(Class<?> type, FieldSerializer serializer) {
		if (type == null)
			throw new IllegalArgumentException("Null type");
		
		if (serializer == null)
			throw new IllegalArgumentException("Null type");
		
		fieldSerializerMap.put(type, serializer);
	}
	
	public <T> T getObject(Map<String, String> fieldMap, Class<T> targetClass) throws SerializerException {
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
				FieldSerializer serializer = fieldSerializerMap.get(field.getType());
			
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
	
	public Map<String, String> toMap(Object object) {		
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
				FieldSerializer serializer = fieldSerializerMap.get(field.getType());
			
				if (serializer == null)
					throw new RuntimeException("Missing serializer for " + field.getType().getName());
				
				map.put(field.getName(), serializer.toString(fieldValue));
			}
		}
		
		return map;
	}
	// =========================================================================
}
