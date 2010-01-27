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
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

import com.serli.helium.pdf.editor.PdfModel;
import com.serli.helium.pdf.editor.PdfStreamEditorPart;

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
				
			if( selection instanceof ITextSelection && part instanceof PdfStreamEditorPart){
							
				ITextSelection text = (ITextSelection)selection;
				
				if( text.getText() != null && !text.isEmpty() ){
									
					PdfStreamEditorPart texteditor = (PdfStreamEditorPart)part;
					
					//add and send the verification
					
						((PdfModel)texteditor.getModel()).addTestText( text.getText() );
						((PdfModel)texteditor.getModel()).sendTestSequence();
					
					//close the editor
						
						HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().closeEditor(texteditor, false ); 
					
					
					/*
					 * In this new version of RCP application the undo/redo is deleted
					 * because the test is immediately send to Selenium IDE 
					 */
					
					//create operation for (undo/redo)
						//AddTextVerificationOperation op = new AddTextVerificationOperation( (PdfModel)texteditor.getModel(), text.getText() );
						//op.addContext( texteditor.getUndoContext() );
						//texteditor.getOperationHistory().execute( op, null, null );
					
				}				
				
			}	
			
		
		} catch( Exception ex ){ throw new ExecutionException(ex.getMessage(),ex); }		
		
		
		return null;
	}
	
/*---------------------------------------------------------------------------------------------------------------------------------------------------------------*/
	
}
