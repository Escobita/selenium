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

package com.serli.helium.commons.logging.internal;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.serli.helium.commons.logging.LoggingPlugin;
import com.serli.helium.commons.logging.service.ILogger;

/**
 * This is the logger implementation class. This service is provided
 * to all other plug-in with OSGI declarative service. This class wraps
 * the native Eclipse plug-in system.
 * 
 * @author Kevin Pollet
 */

public class LoggerImpl implements ILogger {

/*----------------------------------------------------------------------------------------------------------------*/	

	private ILog log_system;
		
/*----------------------------------------------------------------------------------------------------------------*/	

	/**
	 * The default constructor 
	 */
	
	public LoggerImpl(){
		
		this.log_system = LoggingPlugin.getDefault().getLog();
	}
		
/*----------------------------------------------------------------------------------------------------------------*/	

	/**
	 * @see ILogger#addLogListener(ILogListener)
	 */
	
	@Override
	public void addLogListener(ILogListener listener) {
	
		this.log_system.addLogListener(listener);
	}

/*----------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * @see ILogger#removeLogListener(ILogListener)
	 */
	
	@Override
	public void removeLogListener(ILogListener listener) {
		
		this.log_system.removeLogListener(listener);
	}
	
/*----------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 *@see ILogger#logError(String, String, Throwable) 
	 */
	
	@Override
	public void logError(String plugId, String message, Throwable exception) {
		
		this.log_system.log( new Status( IStatus.ERROR, plugId, message, exception ) );
		
	}

/*----------------------------------------------------------------------------------------------------------------*/	

	/**
	 *@see ILogger#logInfo(String, String, Throwable) 
	 */
	
	@Override
	public void logInfo(String plugId, String message, Throwable exception) {
		
		this.log_system.log( new Status( IStatus.INFO, plugId, message, exception ) );
		
	}

/*----------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 *@see ILogger#logWarning(String, String, Throwable) 
	 */
	
	@Override
	public void logWarning(String plugId, String message, Throwable exception) {
		
		this.log_system.log( new Status( IStatus.WARNING, plugId, message, exception ) );
		
	}

/*----------------------------------------------------------------------------------------------------------------*/	


}
