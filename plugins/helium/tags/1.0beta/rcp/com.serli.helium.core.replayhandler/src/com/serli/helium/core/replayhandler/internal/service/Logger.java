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

package com.serli.helium.core.replayhandler.internal.service;

import java.util.concurrent.atomic.AtomicReference;

import org.osgi.service.component.ComponentContext;


import com.serli.helium.commons.logging.service.ILogger;

/**
 * The logger service class. When the plug-in discover the logger service he instantiate this class
 * and all plug-in class can use this logger service. She was automatically called when this logger
 * OSGI service was closed.
 * 
 * @author Kevin Pollet
 * 
 * @noinstantiate This class is not intended to be instantiated by clients.
 */

public class Logger {

/*---------------------------------------------------------------------------------------------------------*/	
	
	private AtomicReference< ILogger > logg_service; //thread can set this variable o be sure with the value
	private static Logger instance;
		
	static{
		instance = null;
	}
	
/*---------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * Constructor
	 */
	
	public Logger(){
		
		this.logg_service = new AtomicReference< ILogger >();
		this.logg_service.set( null );
		
		instance = this;
	}
	
/*---------------------------------------------------------------------------------------------------------*/	

	/**
	 * Get the logger instance
	 * 
	 * @return the logger instance
	 */
	
	public static Logger getInstance(){
		
		return instance;		
	}

/*---------------------------------------------------------------------------------------------------------*/

	/**
	 * The activate method
	 * 
	 * @param context the component context
	 */
	
	protected void activate( ComponentContext context ){
		
		instance = this;
	}
	
/*---------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * The deactivate method
	 * 
	 * @param context the component context
	 */
	
	protected void deactivate( ComponentContext context ){
		
		instance = null;
	}	
	
/*---------------------------------------------------------------------------------------------------------*/
		
	/**
	 * The bind method
	 * 
	 * @param logger the logger service
	 */
	
	protected void bind( ILogger logger ){
		
		this.logg_service.set(logger);	
	}

/*---------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * The unbind method
	 * 
	 * @param logger the logger service
	 */
	
	protected void unbind( ILogger logger ){
		
		this.logg_service.weakCompareAndSet(logger, null);
	}	

/*---------------------------------------------------------------------------------------------------------*/	

	/**
	 * Get the log service
	 * 
	 * @return the log service
	 */
	
	public ILogger getService(){

		return this.logg_service.get();
	}
	
/*---------------------------------------------------------------------------------------------------------*/	

}
