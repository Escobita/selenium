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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

import com.serli.helium.picture.editor.PictureModel;
import com.serli.helium.picture.editor.PictureStreamEditorPart;

/**
 * The AddPictureTextVerificationHandler
 * 
 * @author Kevin Pollet
 */

public class AddPictureTextVerificationHandler extends AbstractHandler {

/*------------------------------------------------------------------------------------------------------------*/	

	/**
	 * The handler default constructor
	 */
	
	public AddPictureTextVerificationHandler(){}	
	
/*------------------------------------------------------------------------------------------------------------*/	
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
			IEditorPart part = HandlerUtil.getActiveEditor(event);
			
			if(  part instanceof PictureStreamEditorPart ){
			
				PictureStreamEditorPart editor = (PictureStreamEditorPart)part;
				
				//Add and send the verification
				
					((PictureModel)editor.getModel()).addPictureTestText();
					((PictureModel)editor.getModel()).sendTestSequence();
				
				//Close the editor	
					
					HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().closeEditor(editor, false);
						
				/*
				 * In this new version of RCP application the undo/redo is deleted
				 * because the test is immediately send to Selenium IDE 
				 */
				
				//Add undo/redo operation
					//AddPictureTextVerificationOperation op = new AddPictureTextVerificationOperation( (PictureModel)editor.getModel() ); 	
					//op.addContext( editor.getUndoContext() );
					//editor.getOperationHistory().execute( op, null, null );		
				
			}		
		
		return null;
	}

/*------------------------------------------------------------------------------------------------------------*/	
	
}
