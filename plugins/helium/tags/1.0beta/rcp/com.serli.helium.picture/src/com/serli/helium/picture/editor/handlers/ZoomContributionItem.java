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

package com.serli.helium.picture.editor.handlers;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

import com.serli.helium.picture.PictureActivator;
import com.serli.helium.picture.editor.PictureStreamEditorPart;

/**
 * The zoom contribution item
 * 
 * @author Kevin Pollet
 */

public class ZoomContributionItem extends ContributionItem {

/*-------------------------------------------------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * The ZoomContributionItem default constructors
	 */
	
	public ZoomContributionItem() {}

/*-------------------------------------------------------------------------------------------------------------------------------------------------------------*/	
	
	public ZoomContributionItem(String id) {
		super(id);
		
	}

/*-------------------------------------------------------------------------------------------------------------------------------------------------------------*/	
	
	@Override
	public void fill(ToolBar parent, int index) {
				
		new ToolItem( parent, SWT.SEPARATOR );
				
		//Zoom image
		ToolItem zoom_image = new ToolItem( parent, SWT.SEPARATOR );
			
			Label label = new Label( parent, SWT.NONE );
			label.setImage( PictureActivator.getDefault().getImageRegistry().get("zoom") );
			label.pack();
		
		zoom_image.setWidth( label.getSize().x );
		zoom_image.setControl( label );
		
		//Zoom combo
		ToolItem zoom_combo = new ToolItem( parent, SWT.SEPARATOR ); 
		
			Combo box = new Combo( parent, SWT.READ_ONLY );
			box.add("200%");
			box.add("150%");
			box.add("100%");
			box.add("50%");
			box.add("25%");
			box.pack();
		
			box.select(2);	
			box.setToolTipText("Change picture zoom");
			box.addSelectionListener( new SelectionAdapter(){
				
				@Override
				public void widgetSelected(SelectionEvent e) {
										
					int index  = ((Combo)e.getSource()).getSelectionIndex();	
					IEditorPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
					
					switch( index ){
				
						case 0 : ((PictureStreamEditorPart)part).setZoom( 200 ); break;
						case 1 : ((PictureStreamEditorPart)part).setZoom( 150 ); break;
						case 2 : ((PictureStreamEditorPart)part).setZoom( 100 ); break;
						case 3 : ((PictureStreamEditorPart)part).setZoom( 50 ); break;
						case 4 : ((PictureStreamEditorPart)part).setZoom( 25 ); break;
					
					}					
				}
				
			});
			
		zoom_combo.setWidth( box.getSize().x );
		zoom_combo.setControl( box );
		
		
	}
	
/*-------------------------------------------------------------------------------------------------------------------------------------------------------------*/	
	
	@Override
	public boolean isDynamic() {
	
		return true;
	}

/*-------------------------------------------------------------------------------------------------------------------------------------------------------------*/	
	
}
