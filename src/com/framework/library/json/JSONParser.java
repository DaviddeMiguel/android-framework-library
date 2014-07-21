package com.framework.library.json;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class JSONParser <IType extends Object>{
	public static final String CLOSE_OBJECT		= "}";
	public static final String CLOSE_ARRAY		= "]";
	
	public String createJsonObject(JSONObjectTag json){
		return json.getOpen() + json.getValue() + json.getClose();
	}
	
	public IType parse(String json, Class<IType> classOfT){
		Gson gson = new Gson();
		return gson.fromJson(json, classOfT);
	}
	
	@SuppressWarnings("unchecked")
	public IType parse(String json, IType type){
		Gson gson = new Gson();
		return (IType) gson.fromJson(json, type.getClass());
	}
	
	public List<IType> parse(String json){
		 Gson gson = new Gson();
		 Type collectionType = new TypeToken<List<IType>>(){}.getType();

		 return gson.fromJson(json, collectionType);
	}
}
