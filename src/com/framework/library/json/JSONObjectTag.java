package com.framework.library.json;

import com.framework.library.model.IModel;

public class JSONObjectTag implements IModel{
	public String open 	= "";
	public String close = "";
	public String value	= "";
	
	public JSONObjectTag(String open, String close, String value){
		this.open = open;
		this.close = close;
		this.value = value;
	}
	
	public JSONObjectTag(){
		
	}
	
	public String getOpen() {
		return open;
	}

	public void setOpen(String open) {
		this.open = open;
	}

	public String getClose() {
		return close;
	}

	public void setClose(String close) {
		this.close = close;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public long getId() {
		return 0;
	}

	@Override
	public void setId(long id) {}
}
