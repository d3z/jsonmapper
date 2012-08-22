package com.joocy.jsonmapper;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class JSONMapper {

	protected static Logger logger = Logger.getLogger("JSONMapper");

    public static <T> T fromJSON(Class<T> modelClass, final String jsonString) {
        try {
            JSONObject jsonObject = (JSONObject) new JSONTokener(jsonString).nextValue();
            return fromJSON(modelClass, jsonObject);
        }
        catch (JSONException je) {
            return null;
        }
    }

	@SuppressWarnings("unchecked")
	public static <T> T fromJSON(Class<T> modelClass, final JSONObject jsonObj) {
        T modelInstance = null;
        try {
            modelInstance = modelClass.newInstance();
        }
        catch (IllegalAccessException iae) {System.err.println(iae);}
        catch (InstantiationException ie) {System.err.println(ie);}

		Field[] fields = modelClass.getDeclaredFields();
        String property;
        String setterName;

		for (Field field : fields) {
			Annotation annotation = field.getAnnotation(JSONProperty.class);
			if (annotation == null) continue;
			property = ((JSONProperty)annotation).value();
			@SuppressWarnings("rawtypes")
			Class fieldType = field.getType();
			setterName = getSetter(field.getName());
			try {
				Method setterMethod = modelClass.getDeclaredMethod(setterName, new Class[]{fieldType});
				Object[] args = new Object[1];
				if (fieldType.isEnum()) {
					String value = jsonObj.optString(property, null);
					args[0] = (value == null) ? null : Enum.valueOf(fieldType, value);
				}
				else if (fieldType.getName().equals("java.lang.String")) {
					args[0] = jsonObj.optString(property, "");
				}
				else if (fieldType.getName().equals("int")) {
					args[0] = jsonObj.optInt(property, -1);
				}
				else if (fieldType.getName().equals("boolean")) {
					args[0] = jsonObj.optBoolean(property, false);
				}
				setterMethod.invoke(modelInstance, args);
			}
			catch (NoSuchMethodException ne) { logger.log(Level.INFO, "Could not find setter for property: " + property); }
			catch (InvocationTargetException ie) { logger.log(Level.INFO, "Could not call setter: " + setterName); }
			catch (IllegalAccessException iae) { logger.log(Level.INFO, "Could not access setter: " + setterName); }
		}
		return modelInstance;
	}

	public static <T> String toJSON(T modelInstance) {
		StringBuffer buffer = new StringBuffer("{");
		Class<?> modelClass = modelInstance.getClass();
		Object[] args = {};
		for (Field field : modelClass.getDeclaredFields()) {
			Annotation annotation = field.getAnnotation(JSONProperty.class);
			if (annotation == null) continue;
			String property = ((JSONProperty)annotation).value();
			@SuppressWarnings("rawtypes")
			Class fieldType = field.getType(); 
			String getterName = getGetter(field.getName(), fieldType.getName().equals("boolean"));
			try {
				Method getterMethod = modelClass.getDeclaredMethod(getterName, new Class<?>[]{});
				if (fieldType.isEnum()) {
					@SuppressWarnings("rawtypes")
					Enum e = (Enum) getterMethod.invoke(modelInstance, args);
					buffer.append(jsonPair(property, e.name())).append(",");
				}
				else if (fieldType.getName().equals("java.lang.String")) {
					String value = (String) getterMethod.invoke(modelInstance, args);
					buffer.append(jsonPair(property, value)).append(",");
				}
				else if (fieldType.getName().equals("int")) {
					Integer value = (Integer) getterMethod.invoke(modelInstance, args);
					buffer.append(jsonPair(property, value.intValue())).append(",");
				}
				else if (fieldType.getName().equals("boolean")) {
					Boolean value = (Boolean) getterMethod.invoke(modelInstance, args);
					buffer.append(jsonPair(property, value.booleanValue())).append(",");
				}
			}
			catch (NoSuchMethodException ne) { logger.log(Level.INFO, "Could not find getter for property.", ne); }
			catch (InvocationTargetException ie) { logger.log(Level.INFO, "Could not call getter.", ie); }
			catch (IllegalAccessException iae) { logger.log(Level.INFO, "Could not access getter.", iae); }		
		}
		buffer.deleteCharAt(buffer.length()-1);
		buffer.append("}");
		return buffer.toString();
	}

	private static String getSetter(String name) {
		name = name.substring(0,1).toUpperCase() + name.substring(1);
		return String.format("set%s", name);
	}	

	private static String getGetter(String name, boolean isBoolean) {
		name = name.substring(0,1).toUpperCase() + name.substring(1);
		return String.format("%s%s", (isBoolean) ? "is" : "get", name);		
	}

	private static String jsonPair(final String key, final String value) {
		return String.format("\"%s\":\"%s\"", key, value);
	}

	private static String jsonPair(final String key, final int value) {
		return jsonPair(key, String.valueOf(value));
	}

	private static String jsonPair(final String key, final boolean value) {
		return jsonPair(key, String.valueOf(value));
	}

}
