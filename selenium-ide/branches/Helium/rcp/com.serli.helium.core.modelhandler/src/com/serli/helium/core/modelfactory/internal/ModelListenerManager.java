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

package com.serli.helium.core.modelfactory.internal;

import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import com.serli.helium.commons.logging.service.ILogger;
import com.serli.helium.core.modelfactory.constants.IModelHandlerConstants;
import com.serli.helium.core.modelfactory.extension.IModelListener;
import com.serli.helium.core.modelfactory.internal.service.Logger;

/**
 * 
 * This class is responsible to call the model listener (typically the GUI) when
 * a new model is created or when all editor must be closed.
 * 
 * @author Kevin Pollet
 */

public class ModelListenerManager {

/*------------------------------------------------------------------------------------------------------------------*/	
	
	private static ModelListenerManager instance;
	private ArrayList< IModelListener > listeners;

	static{
		
		instance = null;
	}
	
/*------------------------------------------------------------------------------------------------------------------*/	
	
	public static synchronized ModelListenerManager getInstance(){ //Can be call by different thread at the same time
		
		if( instance == null ) 
			instance = new ModelListenerManager();
		
		return instance;
	}
	
/*------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * The private default constructor 
	 */
	
	private ModelListenerManager(){
		
		
		this.listeners = new ArrayList< IModelListener >();
		
		//Get the eclipse extension model listener
		
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint point = registry.getExtensionPoint( IModelHandlerConstants.PLUGIN_ID, IModelHandlerConstants.EXTENSION_LISTENER_POINT_NAME );
		IExtension ext[] = point.getExtensions();

		for( int i = 0 ; i < ext.length ; i++ ){
			
			IConfigurationElement[] elt = ext[i].getConfigurationElements();
			
			for( int j = 0 ; j < elt.length ; j++ ){
			
				if( elt[j].getName().equals("modelListener") ){
				
					try {
					
						IModelListener listener = (IModelListener) elt[j].createExecutableExtension("class");
						this.listeners.add( listener );				
					
					} catch (CoreException e) {
						
						ILogger log = Logger.getInstance().getService();
						if( log != null ) log.logError( IModelHandlerConstants.PLUGIN_ID, e.getMessage() , e);
					}					
				}
				
			}			
			
			
		}	
		
		
		
	}
	
/*------------------------------------------------------------------------------------------------------------------*/	

	/**
	 * Fire a model created event
	 * 
	 * @param model the object model
	 * @param model_id the model id
	 */
	
	public void fireModelCreated( Object model, String model_id ){
		
		for( IModelListener l: this.listeners )
			l.modelCreated(model_id, model);
	}
	
/*------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * Fire a close event
	 */
	
	public void fireClose(){
		
		for( IModelListener l: this.listeners )
			l.modelClose();
	}

/*------------------------------------------------------------------------------------------------------------------*/	
	
	
}
