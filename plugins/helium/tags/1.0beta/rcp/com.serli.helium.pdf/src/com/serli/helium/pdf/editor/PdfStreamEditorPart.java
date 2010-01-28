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

package com.serli.helium.pdf.editor;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Panel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

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
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.menus.IMenuService;
import org.eclipse.ui.operations.RedoActionHandler;
import org.eclipse.ui.operations.UndoActionHandler;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;
import org.eclipse.ui.services.IEvaluationService;
import org.jpedal.examples.simpleviewer.ISelectionListener;
import org.jpedal.examples.simpleviewer.SelectionEvent;

import com.serli.helium.core.modelfactory.extension.AbstractTestModel;
import com.serli.helium.pdf.PdfActivator;
import com.serli.helium.pdf.constants.IPdfConstants;
import com.serli.helium.pdf.editor.jpedal.JPedalViewerBean;
import com.serli.helium.pdf.editor.provider.TextSelectionProvider;
import com.serli.helium.ui.testeditor.extension.IStreamEditorPart;
import com.serli.helium.ui.testeditor.extension.StreamEditorInput;

/**
 * The pdf stream editor part (the GUI who allow an user to create verifications on a PDF
 * 
 * @author Kevin Pollet
 */

public class PdfStreamEditorPart extends MultiPageEditorPart implements	IStreamEditorPart {

/*----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/	
	
	public final static String EDITOR_ID = "com.serli.helium.pdf.PdfStreamEditor";
	
	
	private AbstractTestModel model;
	private JobChangeAdapter read_adapter;
	
	private IUndoContext undo_context;
	private UndoActionHandler undo_action;
	private RedoActionHandler redo_action;
	
	private FormToolkit toolkit;
	
	private Form pdf_form;
	private JPedalViewerBean bean;
	private Section extract_section;
	private Text extract_text;
	
	private Form source_form;
	private StyledText editor;
	
	
/*----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/	
	
	public PdfStreamEditorPart() {}

/*----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/	
	
	@Override
	public void init(IEditorSite site, IEditorInput input)	throws PartInitException {
	
		if( !(input instanceof StreamEditorInput) ) throw new PartInitException("Invalid input : Must be StreamEditorInput");
		
			this.setSite( site );
			this.setInput(input);
				
			if( input.getName().length() > 20 ) this.setPartName( input.getName().substring(0,15)+"..." );
			else this.setPartName( input.getName() );
			
			this.model = ((StreamEditorInput)input).getModel();	
		
			//Add listener
			this.read_adapter = new JobChangeAdapter(){

				@Override
				public void done(IJobChangeEvent event) {
					
					int status_code = event.getResult().getSeverity();
									
					if( status_code == IStatus.CANCEL || status_code == IStatus.ERROR ){
							
						if( Display.getCurrent() != null ){						

							IWorkbenchPage page = getSite().getPage();
							page.closeEditor( PdfStreamEditorPart.this , false );
							
						}else{
							
							Display.getDefault().asyncExec( new Runnable(){

								@Override
								public void run() {								
									
									IWorkbenchPage page = getSite().getPage();
									page.closeEditor( PdfStreamEditorPart.this , false );
								}
								
							});							
						}				
						
						
					}else if ( event.getResult().isOK() ){
					
						if( Display.getCurrent() != null ){
							
							bean.setDocument(getModel().getData(), "" );					
							editor.setText( new String(getModel().getData()) );
							
						
						}else{						
							
							Display.getDefault().asyncExec( new Runnable(){

								@Override
								public void run() {
							
									
									
									bean.setDocument(getModel().getData(), "" );
									editor.setText( new String(getModel().getData()) );
								}
								
							});
							
						}
						
					}
					
					
				}	
				
				
			};
			this.model.addJobChangeListener( this.read_adapter );
			
	}

/*----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/	

	/**
	 * Initialize undo/redo
	 */
	
	private void initUndoRedo(){
		
		this.undo_context = new ObjectUndoContext( this.model );
		this.undo_action = new UndoActionHandler( this.getSite(), this.undo_context );
		this.redo_action = new RedoActionHandler( this.getSite(), this.undo_context );
	}
	
/*----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/	
	
	@Override
	protected void createPages() {
		
		//CREATE FORM TOOLKIT
			
			this.toolkit = new FormToolkit( this.getContainer().getDisplay() );
		
		//CREATE PAGE	
	
			this.createPdfPage();
			this.createSourcePage();
			
		//INIT UNDO/REDO	
			
			this.initUndoRedo();
			
		//READ MODEL DATA
			
			IWorkbenchSiteProgressService service = (IWorkbenchSiteProgressService)this.getSite().getService(IWorkbenchSiteProgressService.class );
			service.schedule( this.model.getReadDataJob() );
	}

/*----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/	

	/**
	 * Create the pdf page
	 */
	
	private void createPdfPage(){
		
		//HEAD
		
			this.pdf_form = this.toolkit.createForm( this.getContainer() );
			this.pdf_form.setText("Pdf Text Test Creator" );
			this.toolkit.decorateFormHeading( this.pdf_form );
		
			ToolBarManager manager = (ToolBarManager)this.pdf_form.getToolBarManager();
			
			IMenuService service = (IMenuService)this.getSite().getService(IMenuService.class);
			service.populateContributionManager( manager, "toolbar:com.serli.helium.picture.PdfStreamEditor.pdf.form.toolbar" );
			
			manager.update( true );
			
		//BODY
			
			this.pdf_form.getBody().setLayout( new GridLayout(1,false) );	
			
			Composite composite = toolkit.createComposite( this.pdf_form.getBody(), SWT.EMBEDDED | SWT.NO_BACKGROUND );
			composite.setLayoutData( new GridData(SWT.FILL, SWT.FILL, true, true) );
			
			this.bean = new JPedalViewerBean();
			this.bean.addSelectionListener( new ISelectionListener(){

				@Override
				public void newSelection(SelectionEvent evt) {
					
					PdfModel m = (PdfModel)model;
					
					m.setExtraction( evt.getNumPage(), evt.getSelectedText(), evt.getSelection() );
				}
				
			});
				
			Panel panel = new Panel();
			panel.setLayout( new BorderLayout() );
			panel.add( this.bean, BorderLayout.CENTER );
			
			Frame frame = SWT_AWT.new_Frame( composite );
			frame.add( panel);
			
			this.extract_section = toolkit.createSection( this.pdf_form.getBody(), Section.TITLE_BAR | Section.TWISTIE );
			this.extract_section.setText("Extracted text");
			this.extract_section.setLayoutData( new GridData(SWT.FILL, SWT.CENTER, true, false) );
			
			//Create section toolbar
			ToolBarManager section_manager = new ToolBarManager( SWT.FLAT );
			ToolBar bar = section_manager.createControl( this.extract_section );
			service.populateContributionManager(section_manager, "toolbar:com.serli.helium.picture.PdfStreamEditor.pdf.form.toolbar.section");
			
			this.extract_section.setTextClient( bar );
			section_manager.update(true);
			
			
			Composite compo = this.toolkit.createComposite( this.extract_section, SWT.NONE );
			compo.setLayout( new GridLayout(1,false) );
			
			GridData data = new GridData( SWT.FILL, SWT.CENTER, true, false );
			data.heightHint = 200;
			
			this.extract_text = toolkit.createText( compo, "", SWT.READ_ONLY | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL );
			this.extract_text.setLayoutData( data );
			
			((PdfModel)this.model).addPropertyChangeListener( new PropertyChangeListener(){

				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					
					if ( evt.getPropertyName().equals( PdfModel.TEXT_PROPERTY) ){
					
						final String value = (String)evt.getNewValue();
						
						IEvaluationService service = (IEvaluationService)getSite().getService( IEvaluationService.class );
						service.requestEvaluation( "com.serli.helium.pdf.ExtractPropertyTester.Is_Empty" );
						
						Display.getDefault().asyncExec( new Runnable(){
	
							@Override
							public void run() {
															
									extract_section.setExpanded( value != null );
									extract_text.setText( value == null ? "" : value );	
							}
							
						});
						
						
					}
				
				}
				
			});
						
			this.extract_section.setClient( compo );
			
		//ADD PAGE
			
			int index = this.addPage( this.pdf_form );
			this.setPageText(index, "Pdf" );		
			
	}
	
/*----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * Create the source page
	 */
	
	private void createSourcePage(){
		
		//HEAD
		
			this.source_form = this.toolkit.createForm( this.getContainer() );
			this.source_form.setText("Pdf Text Test Creator" );
			this.toolkit.decorateFormHeading( this.source_form );
					
		//BODY
			
			FillLayout layout = new FillLayout( );
			this.source_form.getBody().setLayout( layout );
			
			this.editor = new StyledText( this.source_form.getBody(), SWT.H_SCROLL | SWT.V_SCROLL | SWT.READ_ONLY );
			this.toolkit.adapt( this.editor );
			
		//CREATE CONTEXT MENU
			
			MenuManager manager = new MenuManager();
			manager.setRemoveAllWhenShown( true );
			
			manager.addMenuListener( new IMenuListener(){
	
				@Override
				public void menuAboutToShow(IMenuManager manager) {
					
					manager.add( new Separator( IWorkbenchActionConstants.MB_ADDITIONS ) ); //for menu contributions
				}
				
			});
			
			Menu context = manager.createContextMenu( this.editor);
			this.editor.setMenu( context );
			
		//PACK PAGE
			
			this.source_form.pack();
					
		//SET EDITOR SITE
			
			this.getEditorSite().setSelectionProvider( new TextSelectionProvider( this.editor ) );
			this.getEditorSite().registerContextMenu( EDITOR_ID+".source", manager, this.getEditorSite().getSelectionProvider() );	
						
		//ADD PAGE
			
			int index = this.addPage( this.source_form );
			this.setPageText(index, "Source" );	
		
	}
	
/*----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/	
	
	@Override
	protected void pageChange(int newPageIndex) {
		super.pageChange(newPageIndex);
		
		//EVALUATE ACTIVEPAGE PROPERTY TESTER 
		 
	 	 	IEvaluationService service = (IEvaluationService)getSite().getService( IEvaluationService.class );
	 	 	service.requestEvaluation("com.serli.helium.pdf.EditorPropertyTester.ActivePage");
	}
	
/*----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/	

	@Override
	public void showBusy(boolean busy) {
		
		this.pdf_form.setMessage( busy ? "Stream loading..." : null );
		this.source_form.setMessage( busy ? "Stream loading..." : null );
		
		this.pdf_form.setBusy( busy );
		this.source_form.setBusy(busy);
		
		super.showBusy(busy);
	}	
	
/*----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/	
		
	@Override
	public void doSave(IProgressMonitor monitor) {}
	
/*----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/	

	@Override
	public void doSaveAs() {}
	
/*----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/	

	@Override
	public boolean isSaveAsAllowed() {
	
		return false;
	}

/*----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/	

	/**
	 * Copy the source editor content
	 */
	
	public void copy(){
	
		this.editor.copy();
	}
	
/*----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * Select all the active page content
	 */
	
	public void selectAll(){
		
		if( this.getActivePage() == 1 ){
			
			ISelectionProvider provider = this.getSite().getSelectionProvider();
			provider.setSelection( new TextSelection( new Document(this.editor.getText()),0, this.editor.getText().length()) );
		
		}else if ( this.getActivePage() == 0 ){
			
			this.bean.selectAll();
		}
		
	}
	
/*----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * Get the editor undo context
	 * 
	 * @return editor undo context
	 */
	
	public IUndoContext getUndoContext(){
		
		return this.undo_context;
	}	
	
/*----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * Get the editor operation history
	 * 
	 * @return the operation history
	 */	
	
	public IOperationHistory getOperationHistory(){
		
		return OperationHistoryFactory.getOperationHistory();
	}
	
/*----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/
	
	@Override
	public AbstractTestModel getModel() {
	
		return this.model;
	}

/*----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/	

	@Override
	public void setFocus() {
	
		
		//SET UNDO/REDO GLOBAL ACTION HANDLER
		
			this.getEditorSite().getActionBars().setGlobalActionHandler( ActionFactory.UNDO.getId(), this.undo_action );
			this.getEditorSite().getActionBars().setGlobalActionHandler( ActionFactory.REDO.getId(), this.redo_action );
			this.getEditorSite().getActionBars().updateActionBars();
		
		//SET ACTIVE PAGE FOCUS	
			
			if( this.getActivePage() == 1 ) this.editor.setFocus();
	
	}
	
/*----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/	
	
	@Override
	public void dispose() {

		
		//REMOVE LISTENER
		
			this.model.removeJobChangeListener( this.read_adapter );
		
		//CANCEL JOB IF RUNNING	
			
			if( this.model.getReadDataJob().getState() != Job.NONE ){
			
				try{
			
					this.model.getReadDataJob().cancel();
					this.model.getReadDataJob().join();
			
				}catch( InterruptedException ex ){
			
					ILog log = PdfActivator.getDefault().getLog();
					log.log( new Status( IStatus.ERROR, IPdfConstants.PLUGIN_ID, ex.getMessage(), ex) );
				}	
			
			}
			
		//DISPOSE UNDO CONTEXT
			
			this.getOperationHistory().dispose( this.undo_context, true, true, true );
		
		//SEND RESPONSE	
		
			if( !this.model.isResponseSend() )
				this.model.sendEmptyTestSequence();
			
		//DISPOSE UI
			
			this.toolkit.dispose();
		
		
		super.dispose();
	}
	
/*----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/	
	
}
