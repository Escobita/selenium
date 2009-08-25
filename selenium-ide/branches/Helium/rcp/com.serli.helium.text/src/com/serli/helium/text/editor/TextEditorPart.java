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

package com.serli.helium.text.editor;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.ObjectUndoContext;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.operations.RedoActionHandler;
import org.eclipse.ui.operations.UndoActionHandler;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;

import com.serli.helium.core.modelfactory.extension.AbstractTestModel;
import com.serli.helium.text.TextActivator;
import com.serli.helium.text.constants.ITextEditorConstants;
import com.serli.helium.text.editor.provider.TextSelectionProvider;
import com.serli.helium.ui.testeditor.extension.IStreamEditorPart;
import com.serli.helium.ui.testeditor.extension.StreamEditorInput;


/**
 * The stream text editor part
 * 
 * @author Kevin Pollet
 */


public class TextEditorPart extends EditorPart implements IStreamEditorPart {

/*-------------------------------------------------------------------------------------------------------------*/	
	
	private UndoActionHandler undoAction;
	private RedoActionHandler redoAction;
	private IUndoContext undocontext;
	
	private AbstractTestModel model;
	private JobChangeAdapter job_adapter;
	
	private FormToolkit toolkit;
	private Form form;
	private StyledText editeur;
	
/*-------------------------------------------------------------------------------------------------------------*/	

	/**
	 * The default constructor
	 */
	
	public TextEditorPart() {}

/*-------------------------------------------------------------------------------------------------------------*/	
		
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {

		if( !(input instanceof StreamEditorInput) ) throw new PartInitException("Invalid input : Must be StreamEditorInput");
		
		this.setSite( site );
		this.setInput( input );
		
		if( input.getName().length() > 20 ) this.setPartName( input.getName().substring(0,17)+"..." );
		else this.setPartName( input.getName() );
		
		this.model = ((StreamEditorInput)input).getModel();
			
		//Add listener
		
		this.job_adapter = new JobChangeAdapter(){

			@Override
			public void done(IJobChangeEvent event) {
				
				int status_code = event.getResult().getSeverity();
								
				if( status_code == IStatus.CANCEL || status_code == IStatus.ERROR ){
						
					if( Display.getCurrent() != null ){						

						IWorkbenchPage page = getSite().getPage();
						page.closeEditor( TextEditorPart.this , false );
						
					}else{
						
						Display.getDefault().asyncExec( new Runnable(){

							@Override
							public void run() {								
								
								IWorkbenchPage page = getSite().getPage();
								page.closeEditor( TextEditorPart.this , false );
							}
							
						});							
					}				
					
					
				}else if ( event.getResult().isOK() ){
				
					if( Display.getCurrent() != null ){
						
						editeur.setText( new String( getModel().getData() ) );
						
					}else{						
						
						Display.getDefault().asyncExec( new Runnable(){

							@Override
							public void run() {
								
								editeur.setText( new String( getModel().getData() ) );
							}
							
						});
						
					}
					
				}
				
				
			}	
				
		};
		
		this.model.addJobChangeListener( this.job_adapter );
		
	}

/*-------------------------------------------------------------------------------------------------------------*/	

	/**
	 * Init the undo/redo
	 */
	
	private void initUndoRedo(){
		
		this.undocontext = new ObjectUndoContext(this.model);
		this.undoAction = new UndoActionHandler( this.getSite(), this.undocontext );
		this.redoAction = new RedoActionHandler( this.getSite(), this.undocontext );
	}
	
/*-------------------------------------------------------------------------------------------------------------*/	
	
	@Override
	public void createPartControl( Composite parent ) {
		
		this.toolkit = new FormToolkit( parent.getDisplay() );
		
		//Header
		
			this.form = this.toolkit.createForm( parent );
			this.form.setText("Stream Text Test Creator");
			this.form.setBusy(true);
			this.toolkit.decorateFormHeading( form );
		
		//Body
		
			FillLayout layout = new FillLayout();
			layout.marginHeight = 10;
			
			this.form.getBody().setLayout( layout );
			
			this.editeur = new StyledText( this.form.getBody(), SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL );
			this.toolkit.adapt( this.editeur , true, true );
		
		//Create context menu		
		
			MenuManager manager = new MenuManager();
			
			Menu menu = manager.createContextMenu( this.editeur );
			manager.setRemoveAllWhenShown(true);
			manager.addMenuListener( new IMenuListener(){

				@Override
				public void menuAboutToShow(IMenuManager manager) {
					
					manager.add( new Separator(IWorkbenchActionConstants.MB_ADDITIONS) );				
				}				
			});	
			
			this.editeur.setMenu( menu );
			
			
		//Initialise Undo/Redo
		
			this.initUndoRedo();
			
		//Set Editor site
	
			this.getEditorSite().setSelectionProvider( new TextSelectionProvider( this.editeur )  );
			this.getEditorSite().registerContextMenu( manager , this.getEditorSite().getSelectionProvider() );
				
		//Read stream data
		
			IWorkbenchSiteProgressService service = (IWorkbenchSiteProgressService)this.getSite().getService( IWorkbenchSiteProgressService.class );
			service.schedule( this.model.getReadDataJob() );
			
			
	}
	
/*-------------------------------------------------------------------------------------------------------------*/	

	@Override
	public void showBusy(boolean busy) {
	
		this.form.setBusy(busy);
		this.form.setMessage( busy ? "Stream loading..." : null );
		
		
		super.showBusy(busy);
	}	
	
/*-------------------------------------------------------------------------------------------------------------*/	
		
	/**
	 * Select all editor text
	 */
	
	public void selectAllText(){
				
		ISelectionProvider provider = this.getSite().getSelectionProvider();
		provider.setSelection( new TextSelection( new Document(this.editeur.getText()),0, this.editeur.getText().length()) );
	}
	
/*-------------------------------------------------------------------------------------------------------------*/	

	/**
	 * Copy the current editor text
	 */
	
	public void copy(){
		
		this.editeur.copy();
	}
	
/*-------------------------------------------------------------------------------------------------------------*/	
	
	@Override
	public void doSave(IProgressMonitor monitor){}

/*-------------------------------------------------------------------------------------------------------------*/	
	
	@Override
	public void doSaveAs(){}

/*-------------------------------------------------------------------------------------------------------------*/	

	@Override
	public void setFocus() {
		
		//Hook Undo/Redo
		
		this.getEditorSite().getActionBars().setGlobalActionHandler( ActionFactory.UNDO.getId(), this.undoAction );
		this.getEditorSite().getActionBars().setGlobalActionHandler( ActionFactory.REDO.getId(), this.redoAction );
		this.getEditorSite().getActionBars().updateActionBars();
		
		//Set editeur focus
		
		this.editeur.setFocus();
	}

/*-------------------------------------------------------------------------------------------------------------*/	

	@Override
	public boolean isDirty() {

		return false;
	}

/*-------------------------------------------------------------------------------------------------------------*/	
	
	@Override
	public boolean isSaveAsAllowed() {
		
		return false;
	}

/*-------------------------------------------------------------------------------------------------------------*/	
	
	@Override
	public AbstractTestModel getModel() {
		
		return this.model;
	}
	
/*-------------------------------------------------------------------------------------------------------------*/	

	/**
	 * Get the operation history corresponding
	 * to this editor
	 * 
	 * @return the operation history
	 */
	
	public IOperationHistory getOperationHistory(){
		
		return OperationHistoryFactory.getOperationHistory();
	}	
	
/*-------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * Get the undo context
	 * who correspond to this editor
	 * 
	 * @return the undo context
	 */
	
	public IUndoContext getUndoContext(){
		
		return this.undocontext;
	}	
	
/*-------------------------------------------------------------------------------------------------------------*/	
	
	@Override
	public void dispose() {
				
		//REMOVE LISTENER
		
			this.model.removeJobChangeListener( this.job_adapter );
		
		//CANCEL READ JOB (if running)
		
			if ( this.model.getReadDataJob().getState() != Job.NONE ){
				
				try {
				
					this.model.getReadDataJob().cancel();
					this.model.getReadDataJob().join();
					
				} catch (InterruptedException e) {
					
					ILog log = TextActivator.getDefault().getLog();
					log.log( new Status( IStatus.ERROR, ITextEditorConstants.PLUGIN_ID, e.getMessage(), e) );
				}
				
			}
			
		//SEND RESPONSE	
			
			if( !this.model.isResponseSend() )
				this.model.sendEmptyTestSequence();	
		
		//REMOVE UNDO/REDO STACK OPERATION
			
			this.getOperationHistory().dispose( this.undocontext, true, true, true);
			
		//DISPOSE SWT 	
			
			this.toolkit.dispose();
		
		super.dispose();		
	}

/*-------------------------------------------------------------------------------------------------------------*/
	
}
