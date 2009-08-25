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

package com.serli.helium.core.server;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import com.serli.helium.core.server.constants.IServerConstants;

/**
 * The jetty configuration preference initializer.
 * This initializer initialize the default preference (server name, server port)
 * for the jetty server.
 * 
 * @author Kevin Pollet
 */

public class PreferenceInitializer extends AbstractPreferenceInitializer {

/*----------------------------------------------------------------------------------------------------*/	
		
	private IEclipsePreferences preferences;
	
/*----------------------------------------------------------------------------------------------------*/	
	
	public PreferenceInitializer() {
	
		this.preferences = new DefaultScope().getNode( IServerConstants.PLUGIN_ID );
	}

/*----------------------------------------------------------------------------------------------------*/	
	
	@Override
	public void initializeDefaultPreferences() {
		
		this.preferences.put(IServerConstants.PREF_NAME , "localhost");
		this.preferences.putInt(IServerConstants.PREF_PORT, 12345);
	}
	
/*----------------------------------------------------------------------------------------------------*/	
	
}
