/*
    Copyright (c) 2006 Redstone Handelsbolag

    This library is free software; you can redistribute it and/or modify it under the terms
    of the GNU Lesser General Public License as published by the Free Software Foundation;
    either version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
    without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License along with this
    library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
    Boston, MA  02111-1307  USA
*/

package com.caterpillar.xmlrpc.core;

import android.text.TextUtils;

import org.simpleframework.xml.Root;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 *  <p>A Struct represents an XML-RPC struct in its Java form. Essentially,
 *  it's just a plain java.util.HashMap with utility methods for extracting
 *  members of any of the data types supported by the XML-RPC library.
 *  The class does not introduce any new field members which makes it
 *  no more expensive than a regular HashMap.</p>
 *
 *  <p>To extract nested values from the struct, use the new simplified
 *  accessors which perform the casting for you:</p>
 *  
 *  <pre>
 *  Date date = myStruct.getArray( "someListOfStructs" ).getStruct( 0 ).getDate( "someDate" );
 *  </pre>
 *  
 *  @author Greger Olsson
 */

@Root(strict=false)
public class XmlRpcStruct extends LinkedHashMap<String,Object>
{
    /**
     *  Returns the String with the given key from the Struct.
     *
     *  @param key The key of the value to extract.
     *
     *  @throws ClassCastException if the value with the given key is not a String.
     */
	
	public static final String TYPE_UND = "und";
	
	public static final String TYPE_VALUE = "value";
	public static final String TYPE_POSTAL = "postal";
	public static final String TYPE_TID = "tid";

    public String getString( Object key )
    {
        return ( String ) get( key );
    }


    /**
     *  Returns the boolean with the given key from the Struct.
     *
     *  @param key The key of the value to extract.
     *
     *  @throws ClassCastException if the value with the given key is not a Boolean.
     *  @throws NullPointerException if a value with the given key does not exist,
     */

    public boolean getBoolean( Object key )
    {
        return ( ( Boolean ) get( key ) ).booleanValue();
    }


    /**
     *  Returns the Boolean wrapper with the given key from the Struct.
     *
     *  @param key The key of the value to extract.
     *
     *  @throws ClassCastException if the value with the given key is not a Boolean.
     */

    public Boolean getBooleanWrapper( Object key )
    {
        return ( Boolean ) get( key );
    }


    /**
     *  Returns the integer with the given key from the Struct.
     *
     *  @param key The key of the value to extract.
     *
     *  @throws ClassCastException if the value with the given key is not a Integer.
     *  @throws NullPointerException if a value with the given key does not exist,
     */

    public int getInteger( Object key )
    {
        return ( ( Integer ) get( key ) ).intValue();
    }


    /**
     *  Returns the Integer wrapper with the given key from the Struct.
     *
     *  @param key The key of the value to extract.
     *
     *  @throws ClassCastException if the value with the given key is not a Integer.
     */

    public Integer getIntegerWrapper( Object key )
    {
        return ( Integer ) get( key );
    }


    /**
     *  Returns the double with the given key from the Struct.
     *
     *  @param key The key of the value to extract.
     *
     *  @throws ClassCastException if the value with the given key is not a Double.
     *  @throws NullPointerException if a value with the given key does not exist,
     */

    public double getDouble( Object key )
    {
        return ( ( Double ) get( key ) ).doubleValue();
    }


    /**
     *  Returns the Double wrapper with the given key from the Struct.
     *
     *  @param key The key of the value to extract.
     *
     *  @throws ClassCastException if the value with the given key is not a Double.
     */

    public Double getDoubleWrapper( Object key )
    {
        return ( Double ) get( key );
    }


    /**
     *  Returns the Array with the given key from the Struct.
     *
     *  @param key The key of the value to extract.
     *
     *  @throws ClassCastException if the value with the given key is not a Array.
     *  @throws NullPointerException if a value at the given key does not exist.
     */

    public XmlRpcArray getArray( Object key )
    {
        return ( XmlRpcArray ) get( key );
    }


    /**
     *  Returns the Struct with the given key from the Struct.
     *
     *  @param key The key of the value to extract.
     *
     *  @throws ClassCastException if the value with the given key is not a Struct.
     */

    public XmlRpcStruct getStruct( Object key )
    {
        return ( XmlRpcStruct ) get( key );
    }


    /**
     *  Returns the Date with the given key from the Struct.
     *
     *  @param key The key of the value to extract.
     *
     *  @throws ClassCastException if the value with the given key is not a Date.
     */

    public Date getDate( Object key )
    {
        return ( Date ) get( key );
    }


    /**
     *  Returns the long integer timestamp with the given key from the Struct.
     *  The timestamp represents the number of milliseconds since midnight jan 01, 1970,
     *  as returned by Date.getTime().
     *
     *  @param key The key of the value to extract.
     *
     *  @throws ClassCastException if the value with the given key is not a Date.
     */

    public long getTimestamp( Object key )
    {
        Date result = ( Date ) get( key );
        return result != null ? result.getTime() : 0;
    }


    /**
     *  Returns the byte[] with the given key from the Struct.
     *
     *  @param key The key of the value to extract.
     *
     *  @throws ClassCastException if the value with the given key is not a byte[].
     */

    public byte[] getBinary( Object key )
    {
        return ( byte[] ) get( key );
    }

    
    /**
     *  Returns the byte[] with the given key from the Struct, as an
     *  input stream (currently, a java.io.ByteArrayInputStream).
     *
     *  @param key The key of the value to extract.
     *
     *  @throws ClassCastException if the value with the given key is not a byte[].
     */

    public InputStream getBinaryAsStream( Object key )
    {
        byte[] result = ( byte[] ) get( key );
        return result != null ? new ByteArrayInputStream( result ) : null;
    }

    public static XmlRpcStruct prepareParamValue(List<? extends Object> data, String type) {
        XmlRpcStruct arr1 = new XmlRpcStruct();

        for(int i=0; i < data.size(); i++){
            XmlRpcStruct arr111 = new XmlRpcStruct();
            arr111.put(TextUtils.isEmpty(type) ? XmlRpcStruct.TYPE_VALUE : type, data.get(i));
            XmlRpcStruct arr11 = new XmlRpcStruct();
            arr11.put(String.valueOf(i), arr111);

            arr1.put(XmlRpcStruct.TYPE_UND, arr11);
        }

        return arr1;
    }

    public static XmlRpcArray prepareParamArray(List<? extends Object> data) {
        XmlRpcArray array = new XmlRpcArray();
        array.addAll(data);

        return array;
    }
    
    /** Serial version UID. */
    private static final long serialVersionUID = 3546359517982963250L;
}