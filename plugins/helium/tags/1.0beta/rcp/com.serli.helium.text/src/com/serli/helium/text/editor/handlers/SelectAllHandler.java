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

package com.serli.helium.text.editor.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

import com.serli.helium.text.editor.TextEditorPart;

/**
 * The select all handler
 * 
 * @author Kevin Pollet
 */

public class SelectAllHandler extends AbstractHandler {

/*------------------------------------------------------------------------------------------------------------------------------------------*/	

	public SelectAllHandler(){}
	
/*------------------------------------------------------------------------------------------------------------------------------------------*/	
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		IEditorPart part = HandlerUtil.getActiveEditor( event );
		
		if( part != null && part instanceof TextEditorPart ){
			
			TextEditorPart texteditor = (TextEditorPart)part;
			texteditor.selectAllText();
		}
		
		
		return null;
	}
	
/*------------------------------------------------------------------------------------------------------------------------------------------*/	

}
