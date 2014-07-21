package com.framework.library.xml;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class Parser{
	private Map<String, String> keys = new HashMap<String, String>();
	
	public Parser( Map<String, String> keys){
		this.keys = keys;
	}
	public Object parseData(InputStream inputStream, Object object){
		try{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder db = factory.newDocumentBuilder();
		    
		    Document doc = db.parse(inputStream);
		   
            Class classs = object.getClass();
            
            //AHORA SE HA AÑADIDO EL CONTROL POR SI ES DIRECTAMENTE UN STRING
//            NodeList nodeList = doc.getElementsByTagName(keys.get(listObj.getSimpleName().toLowerCase()));
//            if(nodeList != null && nodeList.getLength() > 0){
//                Element body = (Element) nodeList.item(0);
//
//                object = parse(body, object);
//            }
            NodeList nodeList = doc.getElementsByTagName(keys.get(classs.getSimpleName().toLowerCase()));
            if(nodeList != null && nodeList.getLength() > 0){
                Element body = (Element) nodeList.item(0);

                if(classs == String.class){
                	return getCharacterDataFromElement(body);
                }else{
                	object = parse(body, object);
                }  
            }else{
            	object = parse(doc.getDocumentElement(), object);
            }
		}catch(Exception e){
			//Functions.log(e);
		}
		
		return object;
	}
	
	public Object parseData(String xmlString, Object object){				
		try{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder db = factory.newDocumentBuilder();
		    InputSource inputSource = new org.xml.sax.InputSource();
		 
		    inputSource.setCharacterStream(new java.io.StringReader(xmlString));
		    Document doc = db.parse(inputSource);
		   
            Class classs = object.getClass();
            
            //AHORA SE HA AÑADIDO EL CONTROL POR SI ES DIRECTAMENTE UN STRING
//          NodeList nodeList = doc.getElementsByTagName(keys.get(listObj.getSimpleName().toLowerCase()));
//          if(nodeList != null && nodeList.getLength() > 0){
//              Element body = (Element) nodeList.item(0);
//
//              object = parse(body, object);
//          }
          NodeList nodeList = doc.getElementsByTagName(keys.get(classs.getSimpleName().toLowerCase()));
          if(nodeList != null && nodeList.getLength() > 0){
              Element body = (Element) nodeList.item(0);

              if(classs == String.class){
              	return getCharacterDataFromElement(body);
              }else{
              	object = parse(body, object);
              }  
          }else{
              object = parse(doc.getDocumentElement(), object);
          }
		}catch(Exception e){
			//Functions.log(e);
		}
		
		return object;
	}
	
	public Object parseData(NodeList nodeList, Object object, String parentName){				
		try{
        	if(nodeList != null && nodeList.getLength() > 0){
        		for(int j=0; j<nodeList.getLength(); j++){
        			Node parent = nodeList.item(j).getParentNode();
        			if(parent.getNodeName().equals(parentName) == true){
        				Element body = (Element) nodeList.item(j);
        				object = parse(body, object);
        				
        				break;
        			}
        		}
        	}
        	
            
		}catch(Exception e){
			//Functions.log(e);
		}
		
		return object;
	}
	
	public Object parseData(Element element, Object object){				
		try{
            object = parse(element, object);
		}catch(Exception e){
			//Functions.log(e);
		}
		
		return object;
	}
	
	public Object parse(Element body, Object object){
		try{
            Class obj = object.getClass();

            Field[] fields = obj.getDeclaredFields();
            
            for(int i=0; i<fields.length; i++){
            	Field field = fields[i];
            	
            	field.setAccessible(true);
            	
                Class classType = field.getType();
                
                if(classType == String.class){
	                try{
	    	        	String string = "";
	    	        	
	    	        	NodeList nodes = body.getElementsByTagName(keys.get(field.getName().toLowerCase()));
	    	        	
	    	        	if(nodes != null && nodes.getLength() > 0){
	    	        		for(int j=0; j<nodes.getLength(); j++){
	    	        			Node parent = nodes.item(j).getParentNode();
	    	        			if((parent.getNodeName().equals(keys.get(object.getClass().getSimpleName().toLowerCase())) == true) || body.isSameNode(nodes.item(j)) == true){
	    	        				string = getCharacterDataFromElement((Element)nodes.item(j));
	    	        				
	    	        				break;
	    	        			}
	    	        		}
	    	        	}else{
	    	        		string = body.getAttribute(keys.get(field.getName().toLowerCase()));
	    	        		
	    	            	if(string == null || string.equals("") == true){
	    	            		string = getCharacterDataFromElement(body);
	    	            	}
	    	        	}
	    	        	
	                	field.set(object, string);
        	        }catch(Exception e){
        	        	//Functions.log(e);
        	        }
                }else if(classType == int.class){
                	
                }else if(classType == long.class){
                	
                }else if(classType == boolean.class){
                	
                }else if(classType == List.class){     
	                try{
	                	NodeList nodes = body.getElementsByTagName(keys.get(field.getName().toLowerCase()));
	                	
	                	if(nodes != null && nodes.getLength() > 0){
	    	        		for(int j=0; j<nodes.getLength(); j++){
	    	        			Node parent = nodes.item(j).getParentNode();
	    	        			if((parent.getNodeName().equals(keys.get(object.getClass().getSimpleName().toLowerCase())) == true) || body.isSameNode(nodes.item(j)) == true){
	    	        				Class listObjectType = getListObjectType(field);
	    	        				List<Object> list = getList(nodes, listObjectType, field, object);
	    		                	
	    		                	field.set(object, list);
	    	        				
	    	        				break;
	    	        			}
	    	        		}
	    	        	}
        	        }catch(Exception e){
        	        	//Functions.log(e);
        	        }       	
                }else{
	                try{
	                	NodeList nodes = body.getElementsByTagName(keys.get(field.getName().toLowerCase()));
	                	
	                	if(nodes != null && nodes.getLength() > 0){
	    	        		for(int j=0; j<nodes.getLength(); j++){
	    	        			Node parent = nodes.item(j).getParentNode();
	    	        			if((parent.getNodeName().equals(keys.get(object.getClass().getSimpleName().toLowerCase())) == true) || body.isSameNode(nodes.item(j)) == true){
	    		                	Object child = createObject(classType);
	    		                	child = parseData(nodes, child, parent.getNodeName());
	    		                	
	    		                	field.set(object, child);
	    	        				
	    	        				break;
	    	        			}
	    	        		}
	    	        	}
        	        }catch(Exception e){
        	        	//Functions.log(e);
        	        }   
                }
            }
		}catch(Exception e){
			//Functions.log(e);
		}
		
		return object;
	}
	
	//ESTO RECUPERA EL STRING DE EL NODELIST
//	public String getString(NodeList nodeList){
//		try{
//			Element element = (Element) nodeList.item(0);
//			
//			return getCharacterDataFromElement(element);
//		}catch(Exception e){
//			//Functions.log(e);
//			return "";
//		}
//	}
	
	public int getInt(NodeList nodeList){
		return 0;
	}
	
	public boolean getBoolean(NodeList nodeList){
		return false;
	}
	
	public List<Object> getList(NodeList nodeList, Class listObjectType, Field field, Object object){
		List<Object> list = new ArrayList<Object>();
		for(int i=0; i<nodeList.getLength(); i++){
			
			Node parentNode = nodeList.item(i).getParentNode();
			if(parentNode.getNodeName().equals(keys.get(object.getClass().getSimpleName().toLowerCase())) == true){
				Element element = (Element) nodeList.item(i);
				
		        if(listObjectType == String.class){
	                try{
	    	        	String string = "";
	    	        	
	    	        	NodeList nodes = element.getElementsByTagName(keys.get(field.getName().toLowerCase()));
	    	        	
	    	        	if(nodes != null && nodes.getLength() > 0){
	    	        		for(int j=0; j<nodes.getLength(); j++){
	    	        			Node parent = nodes.item(j).getParentNode();
	    	        			if((parent.getNodeName().equals(keys.get(object.getClass().getSimpleName().toLowerCase())) == true) || element.isSameNode(nodes.item(j)) == true){
	    	        				string = getCharacterDataFromElement((Element)nodes.item(j));
	    	        				
	    	        				break;
	    	        			}
	    	        		}
	    	        	}else{
	    	        		string = element.getAttribute(keys.get(field.getName().toLowerCase()));
	    	        		
	    	            	if(string == null || string.equals("") == true){
	    	            		string = getCharacterDataFromElement(element);
	    	            	}
	    	        	}
	    	        	
	                	list.add(string);
	    	        }catch(Exception e){
	    	        	//Functions.log(e);
	    	        }
	            }else if(listObjectType == int.class){
	            	
	            }else if(listObjectType == boolean.class){
	            	
	            }else if(listObjectType == List.class){   
	            	try{
	                	Class listObjectTypes = getListObjectType(field);
	                	
	                	List<Object> lists = getList(element.getElementsByTagName(keys.get(field.getName().toLowerCase())), listObjectTypes, field, object);
	                	
	                	field.set(object, lists);
	            	}catch(Exception e){
	            		//Functions.log(e);
	            	}	
	            }else{
	            	try{
	                	Object child = createObject(listObjectType);
	                	child = parseData(element, child);
	                	
	                	list.add(child);
	            	}catch(Exception e){
	            		//Functions.log(e);
	            	}	
	            }
			}
		}
		
		return list;
	}	
	
	public Object createObject(Class classs){
		Object objetine = null;
		
		try{
	        Constructor constructor = classs.getConstructor(null);
	        objetine = constructor.newInstance(null);
		}catch(Exception e){
			//Functions.log(e);
		}
        
        return objetine;
	}
	
	public Class<?> getListObjectType(Field field){
		try{
			ParameterizedType stringListType = (ParameterizedType) field.getGenericType();
			Class<?> stringListClass = (Class<?>) stringListType.getActualTypeArguments()[0];
			return stringListClass;
		}catch(Exception e){
			//Functions.log(e);
		}
		
		return null;
	}
	
	public String getCharacterDataFromElement(Element e) {
		String result = "";
		try{
			if(e == null){
				return result;
			}
			
			NodeList childs = e.getChildNodes();
			if(childs == null){
				return result;
			}
			for(int i=0; i<childs.getLength(); i++){
				Node child = childs.item(i);
				
				if (child instanceof CharacterData) {
					CharacterData cd = (CharacterData) child;
					result = result + cd.getData();
				}
			}
		}catch(Exception ex){
			//Functions.log(ex);
		}

		return result.trim();
	}
}
