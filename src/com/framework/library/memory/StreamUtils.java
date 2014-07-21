package com.framework.library.memory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.http.NameValuePair;

import com.framework.library.util.Functions;

public class StreamUtils {
	private static final int BUFFER = 2048;
	
	public static final String UTF_8 = "UTF-8";
	public static final String AMPERSAND = "&";
	public static final String EQUAL = "=";
	
    public static void CopyStream(InputStream is, OutputStream os){
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
              int count=is.read(bytes, 0, buffer_size);
              if(count==-1)
                  break;
              os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }
    
    public static void saveStream(InputStream is, OutputStream os){
        try{
            byte[] bytes=new byte[BUFFER];
            for(;;){
              int count=is.read(bytes, 0, BUFFER);
              if(count==-1)
                  break;
              os.write(bytes, 0, count);
            }
        }catch(Exception e){
        	Functions.log(e);
        }
    }

    public static boolean writeStream(OutputStream os, List<NameValuePair> params){

    	BufferedWriter writer;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(os, UTF_8));
			writer.write(getQuery(params));
			writer.close();
			os.close();
			
			return true;
		} catch (UnsupportedEncodingException e) {
			Functions.log(e);
		} catch (IOException e) {
			Functions.log(e);
		}
    	
    	return false;
    }
    
    public static String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException{
	    StringBuilder result = new StringBuilder();
	    boolean first = true;

	    for (NameValuePair pair : params)
	    {
	        if (first)
	            first = false;
	        else
	            result.append(AMPERSAND);

	        result.append(URLEncoder.encode(pair.getName(), UTF_8));
	        result.append(EQUAL);
	        result.append(URLEncoder.encode(pair.getValue(), UTF_8));
	    }

	    return result.toString();
	}
}