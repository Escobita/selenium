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

package com.serli.helium.core.server.internal.service;

/**
 * The Jetty server interface. This interface permits to others plug-ins to start
 * and stop the embedded Jetty server instance.
 * 
 * @author Kevin Pollet
 */

public interface IServer {

	/**
	 * Start the jetty server 
	 * @throws Exception if an error occur
	 */
	
	public void start() throws Exception;
	
	/**
	 * Stop the jetty server 
	 * @throws Exception if an error occur
	 */
	
	public void stop() throws Exception;
	
}
