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

package com.serli.helium.picture.editor.provider;

import java.util.ArrayList;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * The selection provider class
 * 
 * @author Kevin Pollet
 */

public class TextSelectionProvider implements ISelectionProvider {

/*------------------------------------------------------------------------------------------------------------------*/	
	
	private StyledText text;
	private ArrayList< ISelectionChangedListener > listener_list;
	
/*------------------------------------------------------------------------------------------------------------------*/	

	/**
	 * The constructor
	 * 
	 * @param text the widget who contains selection
	 */
	
	public TextSelectionProvider( StyledText text ){
		
		this.text = text;
		this.listener_list = new ArrayList< ISelectionChangedListener >();
		
		this.text.addListener( SWT.MouseUp, new Listener(){ //to handle text selection

			@Override
			public void handleEvent(Event event) {
				
				setSelection( getSelection() );
			}
			
		});
		
	}
	
/*------------------------------------------------------------------------------------------------------------------*/		
	
	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		
		if( !this.listener_list.contains( listener ) )
			this.listener_list.add( listener );
	}

/*------------------------------------------------------------------------------------------------------------------*/	
		
	@Override
	public ISelection getSelection() {
		
		ITextSelection res = null;
		
		if( text.getSelectionText().isEmpty() ) res = TextSelection.emptySelection();
		else{
			
			Point selection = text.getSelectionRange();
			res =  new TextSelection( new Document( text.getText() ), selection.x, selection.y );
		}
		
		
		return res;
	}

/*------------------------------------------------------------------------------------------------------------------*/		
	
	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		
		this.listener_list.remove( listener );
	}
	
/*------------------------------------------------------------------------------------------------------------------*/	

	@Override
	public void setSelection(ISelection selection) {
		
		if( !selection.isEmpty() && selection instanceof ITextSelection  ){
			
			ITextSelection text_selection = (ITextSelection)selection;
			this.text.setSelectionRange( text_selection.getOffset(), text_selection.getLength() );		
			this.text.showSelection();
		}		
		
		
		for( ISelectionChangedListener l: this.listener_list )
			l.selectionChanged( new SelectionChangedEvent(this,selection) );		
		
	}
	
/*------------------------------------------------------------------------------------------------------------------*/	
	
}
