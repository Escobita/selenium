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

package com.serli.helium.commons.logging.service;

import org.eclipse.core.runtime.ILogListener;

/**
 * This interface permits logging for a bundle
 * and contains more usable method for the eclipse
 * log API.
 * 
 * @noimplement This interface is not intended to be implemented by clients.
 *  
 * @author Kevin Pollet
 */

public interface ILogger {

/*-------------------------------------------------------------------------------------------------------------*/	

	/**
	 * Add a log listener to this log
	 * 
	 * @see org.eclipse.core.runtime.ILogListener
	 * @param listener the listener
	 */
	
	public void addLogListener( ILogListener listener );
		
/*-------------------------------------------------------------------------------------------------------------*/	

	/**
	 * Remove a log listener to this log
	 * 
	 * @see org.eclipse.core.runtime.ILogListener
	 * @param listener the listener
	 */
	
	public void removeLogListener( ILogListener listener );
		
/*-------------------------------------------------------------------------------------------------------------*/	
		
	/**
	 * Log an info message
	 * 
	 * @param plug_id the plugin id of supplier
	 * @param message the human readable message
	 * @param exception the exception (can be null )
	 */
	
	public void logInfo( String plug_id, String message, Throwable exception );
	
/*-------------------------------------------------------------------------------------------------------------*/	

	/**
	 * Log a warning message
	 * 
	 * @param plug_id the plugin id of supplier
	 * @param message the human readable message
	 * @param exception the exception (can be null )
	 */
		
	public void logWarning( String plug_id, String message, Throwable exception );
	
/*-------------------------------------------------------------------------------------------------------------*/	

	/**
	 * Log an error message
	 * 
	 * @param plug_id the plugin id of supplier
	 * @param message the human readable message
	 * @param exception the exception (can be null )
	 */
	
	
	public void logError( String plug_id, String message, Throwable exception );
	
/*-------------------------------------------------------------------------------------------------------------*/	
	
	
}
