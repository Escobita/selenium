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

package com.serli.helium.ui.testeditor;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.serli.helium.ui.testeditor.internal.EditorExtensionManager;

/**
 * The activator class controls the plug-in life cycle
 */

public class TestEditorActivator extends AbstractUIPlugin {

/*---------------------------------------------------------------------------------------------------------*/

	private static TestEditorActivator plugin;

/*---------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * The constructor
	 */
	
	public TestEditorActivator() {}
	
/*---------------------------------------------------------------------------------------------------------*/	

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
		//Create editor dynamic extension
			EditorExtensionManager.getInstance().createDynamicEditorExtension();
		
	}
	
/*---------------------------------------------------------------------------------------------------------*/	

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
		
	}

/*---------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	
	public static TestEditorActivator getDefault() {
		
		return plugin;
	}

/*---------------------------------------------------------------------------------------------------------*/
	
}
