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

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

/*-----------------------------------------------------------------------------------------------------------------------*/	

	private IWorkbenchAction exit_action;
	
	private IWorkbenchAction show_view_action;

	private IContributionItem show_view_item;
	
	private IWorkbenchAction cut_action;
	private IWorkbenchAction copy_action;
	private IWorkbenchAction paste_action;
	private IWorkbenchAction delete_action;
	private IWorkbenchAction selectall_action;	
	
	private IWorkbenchAction preferences_action;
	private IWorkbenchAction about_action;
	
	
/*-----------------------------------------------------------------------------------------------------------------------*/	
	
    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
        super(configurer);
    }

/*-----------------------------------------------------------------------------------------------------------------------*/
    
    protected void makeActions(IWorkbenchWindow window) {
    	
    	this.show_view_action = ActionFactory.SHOW_VIEW_MENU.create(window);
    	this.register( this.show_view_action );
           	
    	this.show_view_item = ContributionItemFactory.VIEWS_SHORTLIST.create(window);
    	
    	this.cut_action = ActionFactory.CUT.create(window);
    	this.register( this.cut_action );
    	
    	this.copy_action = ActionFactory.COPY.create(window);
    	this.register( this.copy_action );
        	
    	this.paste_action = ActionFactory.PASTE.create(window);
    	this.register( this.paste_action );
    	
    	this.delete_action = ActionFactory.DELETE.create(window);
    	this.register( this.delete_action );
    	
    	this.selectall_action = ActionFactory.SELECT_ALL.create(window);
    	this.register( this.selectall_action );   	
    	
    	this.preferences_action = ActionFactory.PREFERENCES.create(window);
    	this.register( this.preferences_action );
    	
    	this.about_action = ActionFactory.ABOUT.create(window);
    	this.register( this.about_action );
    	
    	this.exit_action = ActionFactory.QUIT.create(window);
    	this.register( this.exit_action );
    }

/*-----------------------------------------------------------------------------------------------------------------------*/    
    
    protected void fillMenuBar(IMenuManager menuBar) {
  
    	//FILE MENU
    	
    		MenuManager file = new MenuManager( "&File", IWorkbenchActionConstants.M_FILE );
    		
    		ActionContributionItem exit_item = new ActionContributionItem( this.exit_action );
    		
    		file.add( new Separator( IWorkbenchActionConstants.MB_ADDITIONS ) );
    		file.add( exit_item );
    		
    		/*if( SWT.getPlatform() == "carbon" || SWT.getPlatform() =="cocoa"){
	    		
    			exit_item.setVisible( false );	
    		}*/
    		
    	
    	//EDIT MENU
	    	
	    	MenuManager edit = new MenuManager( "&Edit", IWorkbenchActionConstants.M_EDIT );
	    	edit.add( new Separator() );
	    	edit.add( new Separator( IWorkbenchActionConstants.MB_ADDITIONS ) );
    		
    	//WINDOW MENU
    	
	    	MenuManager show_view = new MenuManager("Show View","showview");
	    	show_view.add( this.show_view_item );
	    	
	    	MenuManager window = new MenuManager( "&Window",IWorkbenchActionConstants.M_WINDOW );
	    	window.add( show_view );
	    	window.add( new Separator( IWorkbenchActionConstants.MB_ADDITIONS ) );
    
    	//HELP MENU
    	
	    	MenuManager help = new MenuManager("&Help", IWorkbenchActionConstants.M_HELP );
	    
	    	ActionContributionItem pref_item = new ActionContributionItem( this.preferences_action );
	    	ActionContributionItem about_item = new ActionContributionItem( this.about_action );
	    	
	    
	    	help.add( pref_item );
	    	help.add( new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS) );
	    	help.add( new Separator() );
	    	
	    		/*if( SWT.getPlatform() == "carbon" || SWT.getPlatform() =="cocoa"){
	    		
	    			about_item.setVisible( false );
	    			pref_item.setVisible( false );	
	    		}*/	
    	
	    	help.add( about_item );
    		
    	//ADD TO MENU
    	
	    	menuBar.add( file );
	    	menuBar.add( edit );
	    	menuBar.add( window );
	    	
	    		Separator separator = new Separator(IWorkbenchActionConstants.MB_ADDITIONS);
	    		separator.setVisible( false );
	    	
	    	menuBar.add( separator );
	    	menuBar.add( help );
    	
    }

/*-----------------------------------------------------------------------------------------------------------------------*/    
    
}
