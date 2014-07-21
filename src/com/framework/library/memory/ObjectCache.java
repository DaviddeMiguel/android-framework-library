package com.framework.library.memory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import android.content.Context;

import com.framework.library.util.Functions;

public class ObjectCache implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private ObjectCache(){}

	private static class SingletonHelper {
		final static ObjectCache fInstance = new ObjectCache();
	}
	
	public static ObjectCache getInstance() {
		return SingletonHelper.fInstance;
	}
	
	public boolean saveObject(Context context, String objectName, Object obj) {

		final File cacheDir = context.getCacheDir();
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;

		if (!cacheDir.exists()) {
			cacheDir.mkdirs();
		}

		final File cacheFile = new File(cacheDir, objectName);

		try {
			if(cacheFile != null){
				fos = new FileOutputStream(cacheFile);
				oos = new ObjectOutputStream(fos);
				oos.writeObject(obj);	
			}
		} catch (Exception e) {
			Functions.log(e);

		} finally {
			try {
				if (oos != null) {
					oos.close();
				}
				if (fos != null) {
					fos.close();
				}
			} catch (Exception e) {
				Functions.log(e);
			}
		}
		return true;
	}

	
	public boolean deleteObject(Context context, String objectName) {
		try{
			final File cacheDir = context.getCacheDir();
			final File cacheFile = new File(cacheDir, objectName);
			return (cacheFile.delete());
		}catch(Exception e){
			Functions.log(e);
			return false;
		}
	}

	public Object getObject(Context context, String objectName) {

		final File cacheDir = context.getCacheDir();
		final File cacheFile = new File(cacheDir, objectName);

		Object objeto = null;
		FileInputStream fis = null;
		ObjectInputStream is = null;

		if(cacheFile != null){
			try {
	
				fis = new FileInputStream(cacheFile);
				is = new ObjectInputStream(fis);
				objeto = (Object) is.readObject();
			} catch (Exception e) {
				Functions.log(e);
	
			} finally {
				try {
					if (fis != null) {
						fis.close();
					}
					if (is != null) {
						is.close();
					}
	
				} catch (Exception e) {
					Functions.log(e);
				}
			}
		}

		return objeto;
	}
}
