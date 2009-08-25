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

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;

import com.serli.helium.commons.logging.service.ILogger;
import com.serli.helium.core.modelfactory.constants.IModelHandlerConstants;
import com.serli.helium.core.modelfactory.extension.AbstractTestModel;
import com.serli.helium.core.modelfactory.internal.service.Logger;

/**
 * This class is the model factory. When a post request is receive by the test servlet this factory
 * creates a model according to the post request content-type. Then a model created event is fired.
 * Any eclipse plug-in can contribute to it with extension point by uing the modelFactory extension point.
 * 
 * @author Kevin Pollet
 */

public class ModelFactory {

/*--------------------------------------------------------------------------------------------------------------*/	

	private static ModelFactory factory; //singleton instance	
	private HashMap< String, IExtension > cache_ctype;  //< content-type, extension >
	
	static{
		
		factory = null;		
	}
	
/*--------------------------------------------------------------------------------------------------------------*/	

	/**
	 * Get a ModelFactory instance
	 * 
	 * @return the model factory
	 */
	
	public synchronized static ModelFactory getInstance(){ //Synchronised for threads
	
		if( factory == null ) 
			factory = new ModelFactory();
		
		return factory;
	}
	
/*--------------------------------------------------------------------------------------------------------------*/	
		
	/**
	 * The private factory default constructor
	 */
	
	private ModelFactory(){
				
		this.cache_ctype = new HashMap< String, IExtension >();

		//Process extension
		IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint( IModelHandlerConstants.PLUGIN_ID,  IModelHandlerConstants.EXTENSION_FACTORY_POINT_NAME );
		IExtension[] ext = point.getExtensions();
		
		for( int i = 0 ; i< ext.length ; i++ ){
		
			IConfigurationElement[] elt = ext[i].getConfigurationElements();
				
			for( int j = 0 ; j< elt.length ; j++ ){
					
				if ( elt[j].getName().equals("testModel") ){

					//Get the content type
					String ctype = elt[j].getAttribute("content-type");
					String[] ctype_split = ctype.split(",");
						
					for( int k = 0 ; k < ctype_split.length ; k++ )
						this.cache_ctype.put( ctype_split[k].toLowerCase().trim() , ext[i] ); //cache the extension
												
				}		
				
			}		
			
		}
		
		
	}
	
/*--------------------------------------------------------------------------------------------------------------*/	

	/**
	 * Create a TestModel according to the
	 * MIME type
	 * 
	 * @return a Test Model according to the request mime type
	 */
	
	public AbstractTestModel createModel( HttpServletRequest request, Bal bal ){

		AbstractTestModel res = null;
		String ctype = request.getHeader("content-type").replaceAll(";(.)*", "").trim();
		
		IExtension ext = this.cache_ctype.get(ctype);
				
		if ( ext == null && ctype.indexOf("/") != -1 ){
			
			ctype = ctype.replaceFirst( "/(.)*", "/*" ); //replace subtype with wildcard
			ext = this.cache_ctype.get( ctype ); //Check if there is a wildcard content-type
		}
		
		if( ext != null){ //Create model add call model listener

			IConfigurationElement[] elt = ext.getConfigurationElements();
										
			for( int i=0 ; res == null && i < elt.length ; i++){

				if( elt[i].getName().equals("testModel") && elt[i].getAttribute("content-type").contains(ctype) ){
						
					try{

						String model_id = elt[i].getAttribute("id");
						res = (AbstractTestModel)elt[i].createExecutableExtension("class");
						res.init(request, bal);
						res.postInit();
							
						ModelListenerManager.getInstance().fireModelCreated( res, model_id );
						
					}catch(CoreException e){

						ILogger log = Logger.getInstance().getService();
						if( log != null ) log.logError( IModelHandlerConstants.PLUGIN_ID, "error create testModel extension", e);
					}
					
				}
				
			}
			
		}
		
		
		return res;
	}
	
	
/*--------------------------------------------------------------------------------------------------------------*/	
	
	
}
