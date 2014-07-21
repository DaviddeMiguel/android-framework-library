package com.framework.library.memory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

import com.framework.library.util.Functions;

public class FileCache {
    
    private File cacheDir;
    
    public FileCache(Context context, String path){
        //Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            cacheDir = new File(android.os.Environment.getExternalStorageDirectory(),path);
        else
            cacheDir = context.getCacheDir();
        if(!cacheDir.exists())
            cacheDir.mkdirs();
    }
    
    public File getFile(int hascode){
        String filename = String.valueOf(hascode);
        File f = new File(cacheDir, filename);
        return f;
        
    }
    
    public File getFile(String filename){
        File f = new File(cacheDir, filename);
        return f;    
    }
    
    public File getFileBypath(String path){
        File f = new File(path);
        return f;
    }
    
    public File saveFile(String fileName, String content){
    	File file = new File(cacheDir, fileName);
    	FileOutputStream fos = null;
    	try {
    		if(!file.exists()){
    			file.createNewFile();
    		}
    		
			fos = new FileOutputStream(file);
			fos.write(content.getBytes());
		} catch (IOException e) {
			Functions.log(e);
		} catch (Exception e) {
			Functions.log(e);
		}finally{
			if(fos != null){
				try {
					fos.close();
				} catch (IOException e) {
					Functions.log(e);
				}
			}
		}
    	
    	return file;
    }
    
    public File saveFile(String fileName, byte[] data){
    	File file = new File(cacheDir, fileName);
    	FileOutputStream fos = null;
    	try {
    		if(!file.exists()){
    			file.createNewFile();
    		}
			       
			fos = new FileOutputStream(file);
			fos.write(data);
		} catch (IOException e) {
			Functions.log(e);
		} catch (Exception e) {
			Functions.log(e);
		}finally{
			if(fos != null){
				try {
					fos.close();
				} catch (IOException e) {
					Functions.log(e);
				}
			}
		}
    	
    	return file;
    }
    
    /**
     * 
     * @param bitmap
     * @param fileName "foto.png"
     * @param format Bitmap.CompressFormat.PNG
     * @param compressQuality 90
     * @return
     */
    public File saveBitmap(Bitmap bitmap, String fileName, CompressFormat format, int compressQuality){
    	try{
    		File file = getFile(fileName);
    		OutputStream os = new FileOutputStream(file);
    		bitmap.compress(format, compressQuality, os);
    		return getFile(fileName);
    	}catch(Exception e){
    		return null;
    	}
    }
    
    public static byte[] getFileByteArray(String path){
    	File file = new File(path);
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
        	Functions.log(e);
        } catch (IOException e) {
        	Functions.log(e);
		} catch (Exception e) {
			Functions.log(e);
		}
    	
    	return bytes;
    }
    
    public void clear(){
        File[] files = cacheDir.listFiles();
        if(files == null)
            return;
        for(File f:files)
            f.delete();
    }
}