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

package com.serli.helium.ui.workbench;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.serli.helium.ui.workbench.constants.IJwtgenWorkbenchConstants;

/**
 * The activator class controls the plug-in life cycle
 */

public class JWTGenWorkbenchActivator extends AbstractUIPlugin {
	
/*------------------------------------------------------------------------------------------------------------------------------*/	
	
	private static JWTGenWorkbenchActivator plugin;

/*------------------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * The constructor
	 */
	
	public JWTGenWorkbenchActivator() {}
	
/*------------------------------------------------------------------------------------------------------------------------------*/	

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

/*------------------------------------------------------------------------------------------------------------------------------*/	
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

/*------------------------------------------------------------------------------------------------------------------------------*/	

	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		
		reg.put("jetty", JWTGenWorkbenchActivator.imageDescriptorFromPlugin( IJwtgenWorkbenchConstants.PLUGIN_ID, "icons/jetty.png" ) );
	}
	
/*------------------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	
	public static JWTGenWorkbenchActivator getDefault() {
		
		return plugin;
	}
	
/*------------------------------------------------------------------------------------------------------------------------------*/	

}
