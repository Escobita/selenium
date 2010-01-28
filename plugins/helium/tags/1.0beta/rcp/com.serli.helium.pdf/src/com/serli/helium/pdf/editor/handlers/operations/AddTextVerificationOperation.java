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

package com.serli.helium.pdf.editor.handlers.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.serli.helium.core.modelfactory.internal.xmltest.Test;
import com.serli.helium.pdf.editor.PdfModel;

/**
 * The AddTextVerification operation for undo/redo purpose
 * 
 * @author Kevin Pollet
 */

public class AddTextVerificationOperation extends AbstractOperation {

/*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/	
	
	private PdfModel model;
	private String text;
	
	private Test test;
	private int index;

/*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * The constructor
	 * 
	 * @param model the editor model
	 * @param text the text
	 */
	
	public AddTextVerificationOperation( PdfModel model, String text ) {
		super( "Add text verification" );
		
		this.model = model;
		this.text = text;
	}

/*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/	
	
	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)	throws ExecutionException {

		model.addTestText( text );
		
		this.index = model.getTestSequence().getNbTest()-1;
		this.test = model.getTestSequence().getTest( this.index ); //get the added test
		
		return Status.OK_STATUS;
	}

/*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/	
	
	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {

		model.getTestSequence().insertTest( this.test, this.index );
		
		return Status.OK_STATUS;
	}

/*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/	
	
	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)	throws ExecutionException {
	
		this.index = model.getTestSequence().getAllTest().indexOf( this.test );
		model.getTestSequence().removeTest( this.index );
		
		return Status.OK_STATUS;
	}

/*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/	

}
