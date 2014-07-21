package com.framework.library.memory;

import java.util.HashMap;
import java.util.Map;

public class Storage {
	private static class SingletonHelper {
		final static Storage fInstance = new Storage();
	}
	
	public static Storage getInstance() {
		return SingletonHelper.fInstance;
	}
	
	private Storage() {
		storage = new HashMap<String, Object>();
	}
	
	private Map<String, Object> storage;

	public Object getObject(String key) {
		return storage.get(key);
	}

	public void setObject(String key, Object value) {
		storage.put(key, value);
	}
	
	public void remove(String key){
		storage.remove(key);
	}
	
	public void clear(){
		storage.clear();
		storage = new HashMap<String, Object>();
	}
}