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

package com.serli.helium.core.server.internal;

import java.util.Properties;

import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.http.jetty.JettyConfigurator;
import org.eclipse.equinox.http.jetty.JettyConstants;

import com.serli.helium.core.server.constants.IServerConstants;
import com.serli.helium.core.server.internal.service.IServer;

/**
 * The server service implementation. This class implements the start and stop method (start
 *  and stop the embedded jetty server instance).
 * 
 * @author Kevin Pollet
 */

public class Server implements IServer {

/*------------------------------------------------------------------------------------------------------------------------------------------------*/	
	
	private static Server instance;
	
	static{
		
		instance = null;
	}
	
/*------------------------------------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * Get server instance
	 * 
	 *  @return the server instance
	 */
	
	public static IServer getInstance(){
		
		if( instance == null ) instance = new Server();
		
		return instance;
	}
	
/*------------------------------------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * The private default constructor
	 */
	
	private Server(){}
	
/*------------------------------------------------------------------------------------------------------------------------------------------------*/	
		
	@Override
	public void start() throws Exception {

		int port = Platform.getPreferencesService().getInt( IServerConstants.PLUGIN_ID, IServerConstants.PREF_PORT, 
															12345, null );

		String name = Platform.getPreferencesService().getString( IServerConstants.PLUGIN_ID, IServerConstants.PREF_NAME, 
															   "localhost", null );
		
		
		Properties prop = new Properties();
		prop.put( JettyConstants.HTTP_PORT, port );
		prop.put( JettyConstants.HTTP_HOST, name );
		
	
		JettyConfigurator.startServer( IServerConstants.PLUGIN_ID , prop );
		
	}
	
/*------------------------------------------------------------------------------------------------------------------------------------------------*/	
	
	@Override
	public void stop() throws Exception {
	
		JettyConfigurator.stopServer( IServerConstants.PLUGIN_ID );
	}

/*------------------------------------------------------------------------------------------------------------------------------------------------*/	
	
	
}
