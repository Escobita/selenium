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

package com.serli.helium.ui.workbench.preferencepage;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import com.serli.helium.core.server.constants.IServerConstants;
import com.serli.helium.core.server.internal.Server;
import com.serli.helium.ui.workbench.JWTGenWorkbenchActivator;

/**
 * The Jetty preference page
 * 
 * @author Kevin Pollet
 */

public class JettyPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

/*-------------------------------------------------------------------------------------------------------------------------*/	

	private IWorkbenchWindow window;
	private IntegerFieldEditor port_editor;
	private StringFieldEditor  name_editor;
	
/*-------------------------------------------------------------------------------------------------------------------------*/	
		
	/**
	 * Preference page default constructor
	 */
	
	public JettyPreferencePage() {
		super( FieldEditorPreferencePage.GRID );
		
		this.setPreferenceStore( new ScopedPreferenceStore( new InstanceScope(), IServerConstants.PLUGIN_ID)  );
		this.setImageDescriptor( JWTGenWorkbenchActivator.getDefault().getImageRegistry().getDescriptor("jetty") );
		this.setDescription("Change the Jetty server property\n");
	}

/*-------------------------------------------------------------------------------------------------------------------------*/		
		
	@Override
	public void init(IWorkbench workbench) {
		
		this.window = workbench.getActiveWorkbenchWindow();
	}

/*-------------------------------------------------------------------------------------------------------------------------*/	
		
	@Override
	protected void createFieldEditors() {
		
		this.port_editor = new IntegerFieldEditor(IServerConstants.PREF_PORT, "Jetty port:", this.getFieldEditorParent() );
		this.port_editor.setPreferenceStore( this.getPreferenceStore() );
		this.port_editor.setValidRange(1024, 65536);
		
		this.name_editor = new StringFieldEditor(IServerConstants.PREF_NAME, "Jetty name:", this.getFieldEditorParent() );
		this.name_editor.setPreferenceStore( this.getPreferenceStore() );
		
		this.addField( port_editor );
		this.addField( name_editor );
		this.adjustGridLayout();
	}
	
		
/*-------------------------------------------------------------------------------------------------------------------------*/	
	@Override
	protected void performApply() {
		
		//Do nothing
	}
	
/*-------------------------------------------------------------------------------------------------------------------------*/	

	
	@Override
	public boolean performOk() {
			
		boolean retour = true;
		
		//store preferences
		this.port_editor.store();
		this.name_editor.store();
		
		//open dialog
		boolean res = MessageDialog.openConfirm( this.window.getShell(), "Server restart", "Would you like to restart Jetty now ?\n" +
																		 "Note : all editors will be closed" );
		
		if( res ){
			
			try{
		
				this.window.getActivePage().closeAllEditors(false);
				
				Server.getInstance().stop();
				Server.getInstance().start();
				
			}catch( Exception ex ){ 
				
				retour = false;
				ErrorDialog.openError( this.window.getShell(), "Server", "Jetty server restart error", 
									   new Status( IStatus.ERROR, IServerConstants.PLUGIN_ID, ex.getMessage(), ex) );
			}
			
		}
		
		return retour;
	}
	
	
/*-------------------------------------------------------------------------------------------------------------------------*/	

	
}
