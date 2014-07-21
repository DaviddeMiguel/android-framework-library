package com.framework.library.db;

import java.util.List;

import android.database.Cursor;

import com.framework.library.model.IModel;

public interface IDao<Type extends IModel> {
	public static final String _ID 			= "_id";
	public static final String ID			= "id";
	
	Type add(Type type);
	List<Type> addAll(List<Type> all);
	int delete(long id);
	int delete(Type type);
	int update(Type type);
	List<Type> getAll();
	Type get(long id);
	Type get(Type type);
	Type get(String id);
	long count();
	Type cursorToType(Cursor cursor);
}
