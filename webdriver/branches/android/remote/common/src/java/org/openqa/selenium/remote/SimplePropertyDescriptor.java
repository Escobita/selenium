/*
Copyright 2007-2009 WebDriver committers
Copyright 2007-2009 Google Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/


package org.openqa.selenium.remote;

import java.lang.reflect.Method;
import java.util.Hashtable;

public class SimplePropertyDescriptor {
	  private String name;
	  private Method readMethod, writeMethod;
	  
	  public SimplePropertyDescriptor() {};
	  
	  public SimplePropertyDescriptor(String _name, Method _readMethod, Method _writeMethod) {
		  name = _name;
		  readMethod = _readMethod;
		  writeMethod = _writeMethod;
	  }
	  
	  public String getName() {
		  return name;
	  }
	  
	  public Method getReadMethod() {
		  return readMethod;  
	  }
	  
	  public Method getWriteMethod() {
		  return writeMethod;  
	  }
	  
	  public static SimplePropertyDescriptor[] getPropertyDescriptors(Class<? extends Object> _class) {
		  Hashtable<String, SimplePropertyDescriptor> properties =
			  new Hashtable<String, SimplePropertyDescriptor>();
		  
		  for (Method m : _class.getMethods()) {
			  if (m.getName().startsWith("is")) {
				  String propertyName = uncapitalize(m.getName().substring(2));
				  if (properties.containsKey(propertyName))
					  properties.get(propertyName).readMethod = m;
				  else
					  properties.put(propertyName, new SimplePropertyDescriptor(propertyName, m, null));
			  }
			  
			  if (m.getName().length() <= 3)
				  continue;
			  String propertyName = uncapitalize(m.getName().substring(3));
			  if (m.getName().startsWith("get")) {
				  if (properties.containsKey(propertyName))
					  properties.get(propertyName).readMethod = m;
				  else
					  properties.put(propertyName, new SimplePropertyDescriptor(propertyName, m, null));
			  }
			  if (m.getName().startsWith("set")) {
				  if (properties.containsKey(propertyName))
					  properties.get(propertyName).writeMethod = m;
				  else
					  properties.put(propertyName, new SimplePropertyDescriptor(propertyName, null, m));
			  }
		  }
		  
		  SimplePropertyDescriptor[] pdsArray = new SimplePropertyDescriptor[properties.size()];
		  return properties.values().toArray(pdsArray);
	  }
	  
	  private static String uncapitalize(String s) {
		  return s.substring(0, 1).toLowerCase() + s.substring(1);
	  }
}
