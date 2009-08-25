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

package com.serli.helium.picture.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;

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
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
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

import com.serli.helium.core.modelfactory.extension.AbstractTestModel;
import com.serli.helium.picture.PictureActivator;
import com.serli.helium.picture.constants.IPictureConstants;
import com.serli.helium.picture.editor.handlers.LangContributionItem;
import com.serli.helium.picture.editor.provider.TextSelectionProvider;
import com.serli.helium.picture.editor.widget.ImageWidget;
import com.serli.helium.ui.testeditor.extension.IStreamEditorPart;
import com.serli.helium.ui.testeditor.extension.StreamEditorInput;

/**
 * The picture stream test creator
 * editor part
 * 
 * @author Kevin Pollet
 */

public class PictureStreamEditorPart extends MultiPageEditorPart implements IStreamEditorPart {

/*---------------------------------------------------------------------------------------------------------------------------------------------*/	

	public final static String EDITOR_ID = "com.serli.helium.picture.PictureStreamEditor";
		
	private IUndoContext undo_context;
	private UndoActionHandler undoAction;
	private RedoActionHandler redoAction;
	
	private FormToolkit toolkit;
	
	private Form picture_form;
	private Image image;
	private ImageWidget image_widget;
	private Section extract_section;	
		
	private Form source_form;
	private StyledText editor;	
	
	private AbstractTestModel model;
	private JobChangeAdapter read_adapter;
	
/*---------------------------------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * The editor default constructor
	 */
	
	public PictureStreamEditorPart() {
		
		this.image = null;
	}

/*---------------------------------------------------------------------------------------------------------------------------------------------*/	
	
	@Override
	public void init(IEditorSite site, IEditorInput input)	throws PartInitException {
	
		if( !(input instanceof StreamEditorInput) ) throw new PartInitException("Invalid input : Must be StreamEditorInput");
		
		this.setSite( site );
		this.setInput(input);
			
		if( input.getName().length() > 20 ) this.setPartName( input.getName().substring(0,15)+"..." );
		else this.setPartName( input.getName() );
		
		this.model = ((StreamEditorInput)input).getModel();	
		
		//Add listener
		this.read_adapter =  new JobChangeAdapter(){

			@Override
			public void done(IJobChangeEvent event) {
				
				int status_code = event.getResult().getSeverity();
								
				if( status_code == IStatus.CANCEL || status_code == IStatus.ERROR ){
						
					if( Display.getCurrent() != null ){						

						IWorkbenchPage page = getSite().getPage();
						page.closeEditor( PictureStreamEditorPart.this , false );
						
					}else{
						
						Display.getDefault().asyncExec( new Runnable(){

							@Override
							public void run() {								
								
								IWorkbenchPage page = getSite().getPage();
								page.closeEditor( PictureStreamEditorPart.this , false );
							}
							
						});							
					}				
					
					
				}else if ( event.getResult().isOK() ){
				
					if( Display.getCurrent() != null ){
						
						ImageData data = new ImageData( new ByteArrayInputStream( getModel().getData() ) );
						image = new Image( Display.getCurrent(), data );
						
						image_widget.setImage( image );
						editor.setText( new String(getModel().getData()) );
						
					
					}else{						
						
						Display.getDefault().asyncExec( new Runnable(){

							@Override
							public void run() {
								
								ImageData data = new ImageData( new ByteArrayInputStream( getModel().getData() ) );
								image = new Image( Display.getCurrent(), data );
																
								image_widget.setImage( image );
								editor.setText( new String(getModel().getData()) );
							}
							
						});
						
					}
					
				}
				
				
			}	
			
			
		};
		this.model.addJobChangeListener( this.read_adapter );
	}

/*---------------------------------------------------------------------------------------------------------------------------------------------*/	

	/**
	 * Initialize the UNDO/REDO
	 */
	
	public void initUndoRedo(){
		
		this.undo_context = new ObjectUndoContext( this.getModel() );
		this.undoAction = new UndoActionHandler( this.getSite(), this.undo_context );
		this.redoAction = new RedoActionHandler( this.getSite(), this.undo_context );
	}
	
/*---------------------------------------------------------------------------------------------------------------------------------------------*/	
	
	@Override
	protected void createPages() {

		//CREATE FORM TOOLKITs
		
			this.toolkit = new FormToolkit( this.getContainer().getDisplay() );
		
		//CREATE PAGES
		
			this.createPicturePage();		
			this.createSourcePage();
		
		//INITIALIZE UNDO/REDO
			
			this.initUndoRedo();
			
		//READ MODEL DATA
			
			IWorkbenchSiteProgressService service = (IWorkbenchSiteProgressService)this.getSite().getService( IWorkbenchSiteProgressService.class );
			service.schedule( this.model.getReadDataJob() );
			
	}
	
/*---------------------------------------------------------------------------------------------------------------------------------------------*/	

	/**
	 * Create the picture page
	 */
	
	private void createPicturePage(){
		
		//HEAD
		
			this.picture_form = this.toolkit.createForm( this.getContainer() );
			this.picture_form.setText("Picture Text Test Creator" );
			this.toolkit.decorateFormHeading( this.picture_form );
			
			ToolBarManager tool_manager = (ToolBarManager)this.picture_form.getToolBarManager();
			
			IMenuService service = (IMenuService)this.getEditorSite().getService( IMenuService.class );
			service.populateContributionManager(tool_manager, "toolbar:com.serli.helium.picture.PictureStreamEditor.picture.form.toolbar");
			
			tool_manager.add( new LangContributionItem( (PictureModel)this.model ) );
			tool_manager.update(true);
						
		//BODY			
		
			GridLayout layout = new GridLayout( 1 , false );
			layout.marginHeight = 10;
						
			this.picture_form.getBody().setLayout( layout );
		
			//Image widget
			
				this.image_widget = new ImageWidget(this.picture_form.getBody(), SWT.H_SCROLL| SWT.V_SCROLL );
				this.image_widget.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
				toolkit.adapt( this.image_widget );
				
				this.image_widget.addSelectionListener( new SelectionAdapter(){
				
					@Override
					public void widgetSelected(SelectionEvent e) {
					
						Rectangle rect = new Rectangle(e.x,e.y,e.width,e.height );
											
						if( rect.isEmpty() ) ((PictureModel)model).removeLastExtractedText();
						else{	
							
							float zoom = image_widget.getZoom()/100.0f;
							Rectangle selection = new Rectangle( (int)(e.x/zoom),(int)(e.y/zoom),(int)(e.width/zoom),(int)(e.height/zoom) );
							((PictureModel)model).extractText(selection);
						}
						
					}
				
				});
			
			//Extract widget	
				
				this.extract_section = toolkit.createSection( this.picture_form.getBody() , Section.TITLE_BAR | Section.TWISTIE );
				this.extract_section.setText("Extracted text");
				this.extract_section.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING , true, false ) );
				
				
				//Create section toolbar
				ToolBarManager section_manager = new ToolBarManager( SWT.FLAT );
				ToolBar bar = section_manager.createControl( this.extract_section );
				service.populateContributionManager(section_manager, "toolbar:com.serli.helium.picture.PictureStreamEditor.picture.form.extractsection.toolbar");
				
				this.extract_section.setTextClient(bar);
				section_manager.update(true);
				
				//Create section content
				Composite extract_widget = this.toolkit.createComposite( extract_section );
				extract_widget.setLayout( new GridLayout(1,false) );
				
				final Text text = this.toolkit.createText( extract_widget ,"", SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.READ_ONLY );
				
				GridData data = new GridData();
				data.heightHint = 200;
				data.horizontalAlignment = SWT.FILL;
				data.grabExcessHorizontalSpace = true;
				
				text.setLayoutData( data );
				
				extract_section.setClient( extract_widget );
				
				this.model.addPropertyChangeListener( new PropertyChangeListener(){

					@Override
					public void propertyChange(PropertyChangeEvent event) {
						
						if( event.getPropertyName().equals(PictureModel.TEXT_PROPERTY) ){
													
							
							//CHANGE PRESENTATION
							
								IEvaluationService service = (IEvaluationService)getSite().getService( IEvaluationService.class );
								service.requestEvaluation("com.serli.helium.picture.model.ExtractPropertyTester.Is_Empty");
							
							
								String txt = (String)event.getNewValue();
								
								if( txt == null ){
									
									extract_section.setExpanded(false);
									text.setText("");
									
								}else{
								
									extract_section.setExpanded(true);
									text.setText( txt );
								}
								
							
						 }
						
					}
					
				});
			
		//PACK PAGE
				
			this.picture_form.pack();	
				
		//CREATE CONTEXT MENU
			
			MenuManager manager = new MenuManager();
			manager.setRemoveAllWhenShown( true );
			
			manager.addMenuListener( new IMenuListener(){

				@Override
				public void menuAboutToShow(IMenuManager manager) {
					
					manager.add( new Separator( IWorkbenchActionConstants.MB_ADDITIONS ) ); //for menu contribution
				}
				
			});
			
			Menu context = manager.createContextMenu( this.image_widget );
			this.image_widget.setMenu(context);
			
		//SET EDITOR SITE
						
			this.getEditorSite().registerContextMenu( EDITOR_ID + ".picture", manager, null );
	
		//ADD PAGE
			
			int index = this.addPage( this.picture_form );
			this.setPageText(index, "Picture" );	
			
	}

/*---------------------------------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * Create the source page
	 */
	
	private void createSourcePage(){
		
		//HEAD
					
			this.source_form = this.toolkit.createForm( this.getContainer() );
			this.source_form.setText("Picture Text Test Creator" );
			this.toolkit.decorateFormHeading( this.source_form );
						
		//BODY
			
			FillLayout layout = new FillLayout( );
			layout.marginHeight = 10;
			
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

/*---------------------------------------------------------------------------------------------------------------------------------------------*/	

	@Override
	protected void pageChange(int newPageIndex) {
	 	 super.pageChange(newPageIndex);
	 	 
	 	//EVALUATE ACTIVEPAGE PROPERTY TESTER 
	 
	 	 	IEvaluationService service = (IEvaluationService)getSite().getService( IEvaluationService.class );
	 	 	service.requestEvaluation("com.serli.helium.picture.EditorPropertyTester.ActivePage");
	}	
	
/*---------------------------------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * Get the editor undo context
	 * 
	 * @return the undo context
	 */
	
	public IUndoContext getUndoContext(){
		
		return this.undo_context;
	}	
	
/*---------------------------------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * Get the editor operation history
	 * 
	 * @return the editor operation history
	 */
	
	public IOperationHistory getOperationHistory(){
		
		return OperationHistoryFactory.getOperationHistory();
	}	
	
/*---------------------------------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * the select All method
	 */
	
	public void selectAll(){
		
		if( this.getActivePage() == 0 ) this.image_widget.selectAll();
		else if( this.getActivePage() == 1 ){
			
			ISelectionProvider provider = this.getSite().getSelectionProvider();
			provider.setSelection( new TextSelection( new Document(this.editor.getText()),0, this.editor.getText().length()) );
		}
		
	}	
	
/*---------------------------------------------------------------------------------------------------------------------------------------------*/	

	/**
	 * Copy the source selected text
	 */
	
	public void copy(){
		
		this.editor.copy();
	}
	
/*---------------------------------------------------------------------------------------------------------------------------------------------*/	

	/**
	 * Set the picture zoom
	 */
	
	public void setZoom( int percent ){
		
		this.image_widget.setZoom(percent);
	}

/*---------------------------------------------------------------------------------------------------------------------------------------------*/	

	@Override
	public void showBusy(boolean busy) {
	
		if( busy ){
			
			this.source_form.setMessage("Stream loading...");
			this.picture_form.setMessage("Stream loading...");
		}else{
			
			this.source_form.setMessage(null);
			this.picture_form.setMessage(null);
		}
		
		this.source_form.setBusy(busy);
		this.picture_form.setBusy(busy);
		
		super.showBusy(busy);
	}
	
/*---------------------------------------------------------------------------------------------------------------------------------------------*/	
		
	@Override
	public void setFocus() {
	
		//SET UNDO/REDO GLOBAL ACTION HANDLER
		
			this.getEditorSite().getActionBars().setGlobalActionHandler( ActionFactory.UNDO.getId(), this.undoAction );
			this.getEditorSite().getActionBars().setGlobalActionHandler( ActionFactory.REDO.getId(), this.redoAction );
			this.getEditorSite().getActionBars().updateActionBars();
		
		
		//SET FOCUS FOR ACTIVE PAGE
		
			if ( this.getActivePage() == 0 ) this.image_widget.setFocus();
			else if ( this.getActivePage() == 1 ) this.editor.setFocus();			
	}
	
/*---------------------------------------------------------------------------------------------------------------------------------------------*/	

	@Override
	public void doSave(IProgressMonitor monitor) {}

/*---------------------------------------------------------------------------------------------------------------------------------------------*/	

	@Override
	public void doSaveAs() {}

/*---------------------------------------------------------------------------------------------------------------------------------------------*/	

	@Override
	public boolean isSaveAsAllowed() {
		
		return false;
	}

/*---------------------------------------------------------------------------------------------------------------------------------------------*/	

	@Override
	public AbstractTestModel getModel() {
		
		return this.model;
	}

/*---------------------------------------------------------------------------------------------------------------------------------------------*/	
	
	@Override
	public void dispose() {
			
		//RELEASE CONTRIBUTION
		
			/*IMenuService service = (IMenuService)this.getEditorSite().getService( IMenuService.class );
			service.releaseContributions( (ToolBarManager)this.picture_form.getToolBarManager() ); //TODO release*/
		
		//REMOVE LISTENER
		
			this.model.removeJobChangeListener( this.read_adapter );
		
		//CANCEL READ JOB	
			
			if( this.model.getReadDataJob().getState() != Job.NONE ){
				
				try{
				
					this.model.getReadDataJob().cancel();
					this.model.getReadDataJob().join();
				
				}catch( InterruptedException ex ){ 
					
					ILog log = PictureActivator.getDefault().getLog();
					log.log( new Status(IStatus.ERROR, IPictureConstants.PLUGIN_ID, ex.getMessage(), ex) );
				}	
				
			}
			
		//SEND RESPONSE	
		
			if( !this.model.isResponseSend() ) 
				this.model.sendEmptyTestSequence();
		
		//DISPOSE CONTEXT
			
			this.getOperationHistory().dispose(this.undo_context, true, true, true );
					
		//DISPOSE UI	
			
			this.toolkit.dispose();
			
			if( this.image != null )
				this.image.dispose();
		
	
			
		super.dispose();
	}
	
/*---------------------------------------------------------------------------------------------------------------------------------------------*/	
	
}
