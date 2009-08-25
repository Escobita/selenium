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

package com.serli.helium.ui.workbench.perspective;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IPlaceholderFolderLayout;

/**
 * The JWTGen perspective
 * 
 * @author Kevin Pollet
 */

public class PerspectiveTestCreator implements IPerspectiveFactory {

/*----------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * The default constructor
	 */
	
	public PerspectiveTestCreator(){}
	
/*----------------------------------------------------------------------------------------------------------*/	

	@Override
	public void createInitialLayout(IPageLayout layout) {
		
		layout.setEditorAreaVisible( true );						
			
		//Progress view
		IPlaceholderFolderLayout bottom_folder = layout.createPlaceholderFolder("bottomfolder", IPageLayout.BOTTOM , 0.7f, layout.getEditorArea() );
		bottom_folder.addPlaceholder( IPageLayout.ID_PROGRESS_VIEW );		
		
		//Views list
		layout.addShowViewShortcut( IPageLayout.ID_PROGRESS_VIEW );
	}
	
/*----------------------------------------------------------------------------------------------------------*/	

}
