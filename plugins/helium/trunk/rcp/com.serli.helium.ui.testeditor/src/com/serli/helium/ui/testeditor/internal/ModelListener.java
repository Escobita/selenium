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

package com.serli.helium.ui.testeditor.internal;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.serli.helium.core.modelfactory.extension.AbstractTestModel;
import com.serli.helium.core.modelfactory.extension.IModelListener;
import com.serli.helium.ui.testeditor.TestEditorActivator;
import com.serli.helium.ui.testeditor.constants.ITestEditorConstants;
import com.serli.helium.ui.testeditor.extension.IStreamEditorPart;
import com.serli.helium.ui.testeditor.extension.StreamEditorInput;


public class ModelListener implements IModelListener {
	
/*---------------------------------------------------------------------------------------------------------------------------*/	

	/**
	 * Default constructor
	 */
	
	public ModelListener() {
		
	}

/*---------------------------------------------------------------------------------------------------------------------------*/	
	
	@Override
	public void modelCreated(String model_id, Object model) {

	
		String editor_id = EditorExtensionManager.getInstance().getEditorId( model_id );
		
		if( editor_id != null ){
			
			StreamEditorInput input = new StreamEditorInput( (AbstractTestModel)model );
			this.OpenEditor( editor_id, input );
			
			//RCP Application get the focus (data have been received)
			Display.getDefault().asyncExec( new Runnable(){
	
					@Override
					public void run() {
					
						Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(); 
						shell.setMinimized(false);
						shell.setActive();
					}
			});
		
		}else ((AbstractTestModel)model).sendEmptyTestSequence(); //model cannot be hook		
		
	}

	
/*---------------------------------------------------------------------------------------------------------------------------*/	
	
	@Override
	public void modelClose() {
		
		Display.getDefault().syncExec( new Runnable(){

			@Override
			public void run() {
				
				
				boolean show_dialog = false;
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				IEditorReference[] ref = page.getEditorReferences();
			
				
				
				for( int i = 0 ; i < ref.length ; i++){
				
					IEditorPart part = ref[i].getEditor(false);
					
					if( part instanceof IStreamEditorPart ){
						
						if( !show_dialog ) show_dialog = true;
						page.closeEditor(part, false );	
					}
				}
				
				
				if ( show_dialog )
					 MessageDialog.openInformation( page.getWorkbenchWindow().getShell(), "Information", "All stream editor have been closed" +
																					 "\nbecause a new page is loading in Selenium IDE");
				
			}
			
		});
		
	}	
	
/*---------------------------------------------------------------------------------------------------------------------------*/	

	/**
	 * A useful method to open editor
	 */
	
	private void OpenEditor( String editor_id, IEditorInput input ){
		
		
			final String id = editor_id;
			final IEditorInput in = input; 
			
			Display.getDefault().syncExec( new Runnable(){

				@Override
				public void run() {
					
					try {
						
						IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
						IEditorPart part = page.openEditor( in , id );
						part.setFocus();
						
					} catch (PartInitException e) { 
						
						ILog log = TestEditorActivator.getDefault().getLog();
						log.log( new Status( IStatus.ERROR, ITestEditorConstants.PLUGIN_ID , e.getMessage() , e ) );
					}
					
				}
				
			});	
					
		
	}

/*---------------------------------------------------------------------------------------------------------------------------*/	

}
