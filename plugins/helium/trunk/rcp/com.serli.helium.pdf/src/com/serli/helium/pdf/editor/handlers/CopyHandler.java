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

package com.serli.helium.pdf.editor.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

import com.serli.helium.pdf.editor.PdfStreamEditorPart;

/**
 * The copy handler class
 * 
 * @author Kevin Pollet
 */

public class CopyHandler extends AbstractHandler {

/*----------------------------------------------------------------------------------------------------------------------------------*/	

	/**
	 * CopyHandler default constructor
	 */
	
	public CopyHandler(){}
	
/*----------------------------------------------------------------------------------------------------------------------------------*/	
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		IEditorPart part = HandlerUtil.getActiveEditor(event);
		
		if( part instanceof PdfStreamEditorPart ){
		
			((PdfStreamEditorPart)part).copy();
			
		}		
		
		
		return null;
	}

/*----------------------------------------------------------------------------------------------------------------------------------*/	

}
