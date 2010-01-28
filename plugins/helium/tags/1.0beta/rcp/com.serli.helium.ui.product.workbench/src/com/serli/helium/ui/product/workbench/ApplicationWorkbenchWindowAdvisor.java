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

package com.serli.helium.ui.product.workbench;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

import com.serli.helium.ui.product.workbench.constants.IWorkbenchConstants;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

/*-------------------------------------------------------------------------------------------------------------------------------------------------*/	
	
	private IWorkbenchWindow window;
	private TrayItem tray_item;
	private Image tray_image;

/*-------------------------------------------------------------------------------------------------------------------------------------------------*/	
		
	public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		super(configurer);
		
    }

/*-------------------------------------------------------------------------------------------------------------------------------------------------*/	
	
    public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
    	
        return new ApplicationActionBarAdvisor(configurer);
    }

/*-------------------------------------------------------------------------------------------------------------------------------------------------*/	
       
    public void preWindowOpen() {
 	    	
        IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
        
        configurer.setTitle("Helium " + WorkbenchActivator.getDefault().getBundle().getVersion() );
        configurer.setInitialSize( new Point(1024, 768) );
        configurer.setShowStatusLine(true);
        configurer.setShowMenuBar( true );
        configurer.setShowCoolBar( true );
        configurer.setShowFastViewBars(true);
        configurer.setShowPerspectiveBar( false );
        configurer.setShowProgressIndicator(true);       
    }
   
/*-------------------------------------------------------------------------------------------------------------------------------------------------*/	
        
    @Override
    public void postWindowOpen() {
    
    	this.window = this.getWindowConfigurer().getWindow();
    	this.tray_item = initTrayItem();
    	
    	try {
    		
    		//Start the server core bundle (Java Web Start fix) 
        	Bundle server = Platform.getBundle("com.serli.helium.core.server");
			server.start();
			
		} catch (BundleException e) { 
			
			WorkbenchActivator.getDefault().getLog().log( new Status( IStatus.ERROR, IWorkbenchConstants.PLUGIN_ID, e.getMessage(), e) );
		}
    	
    }
    
/*-------------------------------------------------------------------------------------------------------------------------------------------------*/	
        
    /**
     * Initialise the system tray item
     * 
     * @return the tray item
     */
    
    private TrayItem initTrayItem(){
    
    	Tray tray = this.window.getShell().getDisplay().getSystemTray();
    	TrayItem trayitem = null;    	
    	    	
    	if( tray != null ){
    	
    		this.tray_image = WorkbenchActivator.getImageDescriptor("icons/tray.gif").createImage();
    		
    		trayitem = new TrayItem( tray, SWT.NONE );
    		trayitem.setImage( this.tray_image );
    		trayitem.setToolTipText("Helium test creator");   	
    		
    		trayitem.addSelectionListener( new SelectionAdapter(){
    			
    			@Override
    			public void widgetSelected(SelectionEvent e) {
    
    				window.getShell().setMinimized(false);
    				window.getShell().forceActive();
    			}
    			
    		}); 
    		
    		trayitem.addMenuDetectListener( new MenuDetectListener(){

				@Override
				public void menuDetected(MenuDetectEvent e) {
					
					Menu menu = new Menu( window.getShell(), SWT.POP_UP );
					
					MenuItem open = new MenuItem( menu,  SWT.PUSH );
					open.setText("Open JWTGen");
					open.addSelectionListener( new SelectionAdapter(){
						
						@Override
						public void widgetSelected(SelectionEvent e) {
							
							if( window.getShell().getMinimized() )							
								window.getShell().setMinimized(false);	
						}
						
					});
					open.setEnabled( window.getShell().getMinimized() );
					
					MenuItem exit = new MenuItem( menu,  SWT.PUSH );
					exit.setText("Close JWTGen");
					exit.addSelectionListener( new SelectionAdapter(){
						
						@Override
						public void widgetSelected(SelectionEvent e) {
							
							PlatformUI.getWorkbench().close();													
						}
						
					});
										
					
					menu.setVisible(true);
				}
    			
    		});
    		
    	}     	
    	
    	return trayitem;
    }
    
/*-------------------------------------------------------------------------------------------------------------------------------------------------*/	
    
    @Override
    public void dispose() {
    		
    	if( this.tray_image != null ){
    	
    		this.tray_image.dispose();
    		this.tray_item.dispose();
    	}    	
    	
    }
    
/*-------------------------------------------------------------------------------------------------------------------------------------------------*/	
    
}
