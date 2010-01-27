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
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

import com.serli.helium.text.editor.TextEditorPart;
import com.serli.helium.text.editor.TextModel;

/**
 * The add text verification handler
 * 
 * @author Kevin Pollet
 */


public class AddTextVerificationHandler extends AbstractHandler {

/*---------------------------------------------------------------------------------------------------------------------------------------------------------------*/	

	public AddTextVerificationHandler(){}
	
/*---------------------------------------------------------------------------------------------------------------------------------------------------------------*/	
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		try {
						
			ISelection selection = HandlerUtil.getCurrentSelection(event);
			IEditorPart part = HandlerUtil.getActiveEditor(event);
				
			if( selection instanceof ITextSelection && part instanceof TextEditorPart){
							
				ITextSelection text = (ITextSelection)selection;
				
				if( text.getText() != null && !text.isEmpty() ){
									
					TextEditorPart texteditor = (TextEditorPart)part;
					
					//Get the editor model
					
						TextModel model = (TextModel)texteditor.getModel();
					
					//Send the verification	
						
						model.addTestText( text.getText() );
						model.sendTestSequence();
					
					//Close the editor	
						
						HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().closeEditor( texteditor , false );
					
					
					/*
					 * In this Eclipse RCP application refactoring no undo/redo is created.
					 * Because the verification is immediately send to Selenium IDE.
					 */
					
						//AddTextVerificationOperation op = new AddTextVerificationOperation( (TextModel)texteditor.getModel(), text.getText() );
						//op.addContext( texteditor.getUndoContext() );
										
						//texteditor.getOperationHistory().execute( op, null, null );
				}
				
				
			}	
			
		
		} catch( Exception ex ){ throw new ExecutionException(ex.getMessage(),ex); }		
		
		
		return null;
	}
	
/*---------------------------------------------------------------------------------------------------------------------------------------------------------------*/
	
}
