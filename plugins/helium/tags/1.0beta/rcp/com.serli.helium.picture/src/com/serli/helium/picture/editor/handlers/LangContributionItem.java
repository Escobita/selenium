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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.serli.helium.moteur.picture.engine.Language;
import com.serli.helium.picture.editor.PictureModel;

/**
 * The lang contribution item
 * 
 * @author Kevin Pollet
 */

public class LangContributionItem extends ContributionItem {

/*----------------------------------------------------------------------------------------------------------------------------------------*/	

	private PictureModel model;
	
/*----------------------------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * The LangContributionItem default constructor
	 */
	
	public LangContributionItem(PictureModel model ) {
				 
		this.model = model;
	}
	
/*----------------------------------------------------------------------------------------------------------------------------------------*/	
	
	@Override
	public void fill(ToolBar parent, int index) {
	
		new ToolItem( parent, SWT.SEPARATOR );
				
		ToolItem combo = new ToolItem( parent, SWT.SEPARATOR );
		
			Combo box = new Combo( parent, SWT.READ_ONLY );
			box.add("Dutch");
			box.add("English");
			box.add("French");
			box.add("German");
			box.add("Italian");
			box.add("Portugese");
			box.add("Spanish");
		
			box.select(1);
			box.setToolTipText("Change ocr language");
			box.setLayoutData( new GridData( SWT.LEFT, SWT.TOP, false, false ) );
			box.pack();
			
			box.addSelectionListener( new SelectionAdapter(){
				
				@Override
				public void widgetSelected(SelectionEvent e) {
				
					int index = ((Combo)e.getSource()).getSelectionIndex();
					
					switch( index ){
					
						case 0 : model.setLanguage( Language.Dutch ); break;
						case 1 : model.setLanguage( Language.English ); break;
						case 2 : model.setLanguage( Language.French ); break;
						case 3 : model.setLanguage( Language.German ); break;
						case 4 : model.setLanguage( Language.Italian ); break;
						case 5 : model.setLanguage( Language.Portuguese ); break;
						case 6 : model.setLanguage( Language.Spanish ); break;						
					}					
					
				}
				
			});
			
		combo.setWidth( box.getSize().x );
		combo.setControl(box);		
		
	}
	
/*----------------------------------------------------------------------------------------------------------------------------------------*/	
	
	
}
