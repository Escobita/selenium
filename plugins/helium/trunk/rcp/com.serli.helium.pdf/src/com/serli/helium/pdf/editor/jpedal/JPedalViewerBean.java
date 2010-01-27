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

package com.serli.helium.pdf.editor.jpedal;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jpedal.examples.simpleviewer.Commands;
import org.jpedal.examples.simpleviewer.ISelectionListener;
import org.jpedal.examples.simpleviewer.SimpleViewer;
import org.jpedal.examples.simpleviewer.Values;

/**
 * The modified JPedal viewer bean for RCP integration.
 * 
 * @author Kevin Pollet
 */

public class JPedalViewerBean extends JPanel {
	
	private static final long serialVersionUID = 1L;

	private SimpleViewer viewer;
	
	private Integer pageNumber = null;
	private Integer rotation = null;
	private Integer zoom = null;

	private Boolean isMenuBarVisible = null;
	private Boolean isToolBarVisible = null;
	private Boolean isDisplayOptionsBarVisible = null;
	private Boolean isSideTabBarVisible = null;
	private Boolean isNavigationBarVisible = null;
	
	public JPedalViewerBean() {
		
        viewer = new SimpleViewer(this, SimpleViewer.PREFERENCES_JWTGEN ); //Load JWTGen preferences
	}
	
	public void addSelectionListener( ISelectionListener listener ){
		
		this.excuteCommand( Commands.ADD_SELECTION_LISTENER, new Object[]{listener} );
	}

	public void removeSelectionListener( ISelectionListener listener ){
		
		this.excuteCommand( Commands.REMOVE_SELECTION_LISTENER, new Object[]{listener} );
	}
	
    public SimpleViewer getViewer() {
    	return viewer;
    }
    
    public void setDocument( byte[] document, String filename ) {
		
		excuteCommand(Commands.OPENFILE, new Object[] { document, filename });
		
		if(pageNumber != null) {
			excuteCommand(Commands.GOTO, new String[] { 
				String.valueOf(pageNumber) });
		}
		
		if(rotation != null) {
			excuteCommand(Commands.ROTATION, new String[] { 
				String.valueOf(rotation) });
		}
		
		if(zoom != null) {
			excuteCommand(Commands.SCALING, new String[] { 
				String.valueOf(zoom) });
		} else {
			excuteCommand(Commands.SCALING, new String[] { 
					String.valueOf(100) });
		}
		
		if(isMenuBarVisible != null) {
			setMenuBar(isMenuBarVisible.booleanValue());
		}
		
		if(isToolBarVisible != null) {
			setToolBar(isToolBarVisible.booleanValue());
		}
		
		if(isDisplayOptionsBarVisible != null) {
			setDisplayOptionsBar(isDisplayOptionsBarVisible.booleanValue());
		}
		
		if(isSideTabBarVisible != null) {
			setSideTabBar(isSideTabBarVisible.booleanValue());
		}
		
		if(isNavigationBarVisible != null) {
			setNavigationBar(isNavigationBarVisible.booleanValue());
		}
	}
	
	// Page Number ////////
	public int getPageNumber() {
		if(pageNumber == null)
			return 1;
		else
			return pageNumber.intValue();
	}
	
	public void setPageNumber(final int pageNumber) {
		this.pageNumber = new Integer(pageNumber);
		
		excuteCommand(Commands.GOTO, new String[] { String.valueOf(pageNumber) });
		
	}

	// Rotation ////////
	public int getRotation() {
		if(rotation == null)
			return 0;
		else
			return rotation.intValue();
	}

	public void setRotation(final int rotation) {
		this.rotation = new Integer(rotation);

		
		excuteCommand(Commands.ROTATION, new String[] { String.valueOf(rotation) });
	}
	
	// Zoom ////////
	public int getZoom() {
		if(zoom == null)
			return 100;
		else
			return zoom.intValue();
	}

	public void setZoom(int zoom) {
		this.zoom = new Integer(zoom);
		
		excuteCommand(Commands.SCALING, new String[] { String.valueOf(zoom) });
	}

	//setToolBar, setDisplayOptionsBar, setSideTabBar, setNavigationBar, 
	public void setMenuBar(boolean visible) {
		this.isMenuBarVisible = new Boolean(visible);
		
		viewer.executeCommand(Commands.UPDATEGUILAYOUT, new Object[] {new String("ShowMenubar"), Boolean.valueOf(visible)});
	}
	
	public boolean getMenuBar() {
		if(isMenuBarVisible == null)
			return true;
		else
			return isMenuBarVisible.booleanValue();
	}
	
	public void setToolBar(boolean visible) {
		this.isToolBarVisible = new Boolean(visible);
		
		viewer.executeCommand(Commands.UPDATEGUILAYOUT, new Object[] {new String("ShowButtons"), Boolean.valueOf(visible)});
	}
	
	public boolean getToolBar() {
		if(isToolBarVisible == null)
			return true;
		else
			return isToolBarVisible.booleanValue();
	}
	
	public void setDisplayOptionsBar(boolean visible) {
		this.isDisplayOptionsBarVisible = new Boolean(visible);
		
		viewer.executeCommand(Commands.UPDATEGUILAYOUT, new Object[] {new String("ShowDisplayoptions"), Boolean.valueOf(visible)});
	}
	
	public boolean getDisplayOptionsBar() {
		if(isDisplayOptionsBarVisible == null)
			return true;
		else
			return isDisplayOptionsBarVisible.booleanValue();
	}
	
	public void setSideTabBar(boolean visible) {
		this.isSideTabBarVisible = new Boolean(visible);
		
		viewer.executeCommand(Commands.UPDATEGUILAYOUT, new Object[] {new String("ShowSidetabbar"), Boolean.valueOf(visible)});
	}
	
	public boolean getSideTabBar() {
		if(isSideTabBarVisible == null)
			return true;
		else
			return isSideTabBarVisible.booleanValue();
	}
	
	public void selectAll(){
	
		this.excuteCommand(Commands.SELECTALL, null );
	}	
	
	public void setNavigationBar(boolean visible) {
		this.isNavigationBarVisible = new Boolean(visible);
		
		viewer.executeCommand(Commands.UPDATEGUILAYOUT, new Object[] {new String("ShowNavigationbar"), Boolean.valueOf(visible)});
	}
	
	public boolean getNavigationBar() {
		if(isNavigationBarVisible == null)
			return true;
		else
			return isNavigationBarVisible.booleanValue();
	}
	
	private void excuteCommand(final int command, final Object[] input) {
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				viewer.executeCommand(command, input);
				
				while(Values.isProcessing()) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				repaint();
			}
		});
	}
	

}