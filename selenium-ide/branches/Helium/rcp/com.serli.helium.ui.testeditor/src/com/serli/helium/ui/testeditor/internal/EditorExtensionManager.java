/* 
* Copyright 2009 SERLI
*
* This file is part of Helium.
*
* Helium is free software: you can redistribute it and/or modify it under the terms
* of the GNU General Public License as published by the Free Software Foundation, 
* either version 3 of the License, or(at your option) any later version.
*
* Helium is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
* See the GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License along with Helium.
*
* If not, see <http://www.gnu.org/licenses/>.
*/

package com.serli.helium.ui.testeditor.internal;

import java.io.ByteArrayInputStream;
import java.util.HashMap;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import com.serli.helium.ui.testeditor.constants.ITestEditorConstants;

/**
 * 
 * The editor extension manager
 * 
 * @author Kevin Pollet
 */

public class EditorExtensionManager {

/*------------------------------------------------------------------------------------------------------------------------------------*/	
	
	private static EditorExtensionManager instance; 
	private HashMap< String,String > editor_model_list;
	
	static{
	
		instance = null;
	}
	
/*------------------------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * Get the extension manager instance
	 * 
	 * @return the instance
	 */
	
	public static EditorExtensionManager getInstance(){
	
		if( instance == null )
			instance = new EditorExtensionManager();
		
		return instance;
	}

/*------------------------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * The editor extension manager default constructor
	 */
	
	private EditorExtensionManager(){
		
		this.editor_model_list = new HashMap< String,String >();
	}

/*------------------------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * Get the editor id corresponding to this
	 * model id
	 * 
	 * @param the model id
	 * @return the editor id or null if none
	 */
	
	public String getEditorId( String model_id ){
		
		return this.editor_model_list.get( model_id );
	}
	
/*------------------------------------------------------------------------------------------------------------------------------------*/	

	/**
	 * A useful method to create dynamic extension
	 * 
	 * @param this method could be called only one times
	 */
	
	public void createDynamicEditorExtension(){ 


		IExtensionRegistry registry = Platform.getExtensionRegistry();	
		IExtensionPoint point = registry.getExtensionPoint( ITestEditorConstants.PLUGIN_ID, ITestEditorConstants.EXTENSION_POINT_NAME );
		IExtension[] extension = point.getExtensions();
	
		for( int i = 0 ; i < extension.length ; i++ ){
	
			IConfigurationElement[] elts = extension[i].getConfigurationElements();
			
			for( int j = 0 ; j < elts.length ; j++ ){
					
				if( elts[j].getName().equals("streamEditor")  ){
							
					//POPULATE HASHMAP
					
						String editor_id = elts[j].getAttribute("id");
						String model_id = elts[j].getAttribute("modelId");
						
						this.editor_model_list.put( model_id, editor_id );
					
					//INJECT DYNAMIC EXTENSION IN THE REGISTRY
					
						StringBuffer buffer = new StringBuffer();
						
						buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
						buffer.append("<plugin>\n");
						buffer.append("<extension point=\"org.eclipse.ui.editors\">\n");
						buffer.append("<editor\n");
						buffer.append("id=\""+ elts[j].getAttribute("id") +"\"\n");
						buffer.append("name=\""+ elts[j].getAttribute("name") +"\"\n");
						
						if( elts[j].getAttribute("icon") != null ) 
							buffer.append("icon=\""+ elts[j].getAttribute("icon") +"\"\n");
						
						buffer.append("class=\""+ elts[j].getAttribute("class") +"\"");
						
						if( elts[j].getAttribute("contributorClass") != null ) 
							buffer.append("\ncontributorClass=\""+ elts[j].getAttribute("contributorClass") +"\"");
						
						buffer.append(">\n");
						buffer.append("</editor>\n");
						buffer.append("</extension>\n");
						buffer.append("</plugin>\n");
						
						registry.addContribution( new ByteArrayInputStream( buffer.toString().getBytes() ), 
												  extension[i].getContributor(), false, null, null, null );
					
						
				}
				
					
			}	
			
				
		}	
		

	}
	
/*------------------------------------------------------------------------------------------------------------------------------------*/	
	
	
}
