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

package com.serli.helium.picture.editor.widget;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;

/**
 * 
 * The image widget
 * 
 * @author Kevin Pollet
 */

public class ImageWidget extends ScrolledComposite {

/*-----------------------------------------------------------------------------------------------------------------------*/	

	private float fact_zoom;
	
	private Rectangle selection;
	private Color fill_color;
	private Color border_color;
	
	private Image image;
	private Image scaled_image;
	
	private Canvas picture_canvas;

	private ArrayList< SelectionListener > listener_list;
	
/*-----------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * The imageWidget constructor
	 * 
	 * @param parent the parent composite
	 * @param style the ImageWidget style
	 */
	
	public ImageWidget(Composite parent, int style) {
		super(parent, style);
					
		
		this.fact_zoom = 1.0f;
		this.image = null;
		this.scaled_image = null;
		this.picture_canvas = new Canvas( this, SWT.NO_REDRAW_RESIZE | SWT.BORDER );
		this.picture_canvas.setCursor( new Cursor(parent.getDisplay(), SWT.CURSOR_CROSS ) );
		
		this.selection = null;
		this.fill_color = new Color( parent.getDisplay(), 136, 165, 189 );
		this.border_color = new Color( parent.getDisplay(), 139, 165, 188 );
		
		this.listener_list = new ArrayList< SelectionListener >();
		
		this.setContent( this.picture_canvas );
		

		this.makeListener();		
	}

/*-----------------------------------------------------------------------------------------------------------------------*/	

	/**
	 * Method call to register widget listener
	 */
	
	private void makeListener(){
		
		//Picture paint listener
		this.picture_canvas.addPaintListener( new PaintListener(){

			@Override
			public void paintControl(PaintEvent e) {
															
				GC graphics = e.gc;
								
				
				if( scaled_image != null  ){
				
					//draw image
					
						graphics.drawImage( scaled_image, e.x, e.y, e.width, e.height, e.x, e.y, e.width, e.height);
					
					//draw selection
					 
						Rectangle selection = ImageWidget.this.selection;
						
						if( selection != null && selection.intersects(e.x, e.y, e.width, e.height) ){
											
							Rectangle select = new Rectangle( selection.x, selection.y, selection.width-1, selection.height-1 );
							
							graphics.setAlpha(90);
							
							graphics.setBackground( ImageWidget.this.border_color );
							graphics.drawRectangle( select );
													
							graphics.setBackground(  ImageWidget.this.fill_color );
							graphics.fillRectangle(  select );
							
						}
						
				}
					
			}
			
		});	
		
		
		//Picture canvas mouse listener
		MouseImageListener listener = new MouseImageListener(this); 
		this.picture_canvas.addMouseListener( listener );
		this.picture_canvas.addMouseMoveListener( listener );
			
	}
	
/*-----------------------------------------------------------------------------------------------------------------------*/	

	/**
	 * Add a selection listener (if the listener is already
	 * added, this method has no effect)
	 * 
	 * @param the listener
	 */
	
	public void addSelectionListener( SelectionListener listener ){
		
		if( !this.listener_list.contains( listener ) )
			this.listener_list.add( listener );
	}
	
/*-----------------------------------------------------------------------------------------------------------------------*/	

	/**
	 * Remove a selection listener
	 * 
	 * @param the selection listener
	 */
	
	public void removeSelectionListener( SelectionListener listener ){
	
		this.listener_list.remove( listener );
	}	
	
/*-----------------------------------------------------------------------------------------------------------------------*/	


	/**
	 * Fire a selection widget event 
	 */
	
 	protected void fireSelection(){
	
		Event event = new Event();
		event.type = SWT.Selection;
		event.widget = this;
		
		if( this.selection != null )
			event.setBounds( this.selection );
		
		SelectionEvent selection_event = new SelectionEvent( event );

		for( SelectionListener listener: this.listener_list )
			listener.widgetSelected( selection_event );
			
	}
	
/*-----------------------------------------------------------------------------------------------------------------------*/	

	/**
	 * Set the widget selection
	 * 
	 * @param selection the selection
	 */
	
	protected void setSelection( Rectangle selection ){
		
		if( this.selection != null ){
		
			//Clear old selection
				Rectangle old_selection = this.selection;
				this.selection = null;
				
				this.picture_canvas.redraw( old_selection.x, old_selection.y, old_selection.width, old_selection.height, false );				
		}
		
		//Draw new selection
			this.selection = selection;
			
			if( selection != null )
				this.picture_canvas.redraw( selection.x, selection.y, selection.width, selection.height, false );				
			
			
	}	
	
/*-----------------------------------------------------------------------------------------------------------------------*/	
		
	/**
	 * Get the widget current selection (in the zoomed image)
	 * 
	 * @return the current selection or null if none
	 */
	
	public Rectangle getCurrentSelection(){
		
		return this.selection;
	}	
	
/*-----------------------------------------------------------------------------------------------------------------------*/	

	/**
	 * Select all the picture
	 */
	
	public void selectAll(){
	
		if( this.image != null ){
		
			Rectangle bounds = this.picture_canvas.getBounds();
		
			this.setSelection( bounds );
			this.fireSelection();	
		}
		
	}
	
/*-----------------------------------------------------------------------------------------------------------------------*/	

	/**
	 * Clear the widget selection
	 */
	
	public void clearSelection(){

		this.setSelection( null );
		this.fireSelection();
	}
	
/*-----------------------------------------------------------------------------------------------------------------------*/	
		
	/**
	 * Get the actual zoom factor
	 * 
	 * @return the actual zoom percentage
	 */
	
 	public int getZoom(){
 		
		return (int)(this.fact_zoom*100);
	}
	
/*-----------------------------------------------------------------------------------------------------------------------*/	

	/**
	 * Set the image widget zoom
	 * 
	 * @param zoom the zoom percentage (ex: 100 for 100%)
	 */
	
	public void setZoom( int zoom ){
				
		if( this.image != null ){
		
			this.fact_zoom = zoom/100.0f;	
			
			//clear selection
				this.selection = null; 
			
			//calculate new width and height
				
				Rectangle bounds = this.image.getBounds();
				
				int new_width = (int)(bounds.width*fact_zoom);
				int new_height = (int)(bounds.height*fact_zoom);
			
			//create a scaled image		
				if( this.scaled_image !=null )
					this.scaled_image.dispose();
				
				this.scaled_image = new Image( this.getDisplay(), new_width, new_height );
				GC gc = new GC( this.scaled_image );
			
					gc.drawImage( this.image, 0, 0, bounds.width, bounds.height,
											  0, 0, new_width, new_height );
				
				gc.dispose();
			
			//Change canvas size	
				this.picture_canvas.setSize( new_width, new_height );
				this.picture_canvas.redraw();			
		}	
		
	}	
	
/*-----------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * Get the widget image or null if none
	 * 
	 * @return the widget image
	 */
	
	public Image getImage(){
		
		return this.image;
	}
		
/*-----------------------------------------------------------------------------------------------------------------------*/	

	/**
	 * Set the widget image or null to clear
	 *
	 * @param image the widget display image
	 */
	
	public void setImage( Image image ){
					
		if( this.scaled_image != null ){ //dispose cached image
			
			this.scaled_image.dispose();
			this.scaled_image = null;
		}
		
		
		this.image = image;
		
		if( image == null ) this.picture_canvas.redraw();
		else this.setZoom(100); //set default zoom
	}	
	
/*-----------------------------------------------------------------------------------------------------------------------*/	

	@Override
	public void setMenu(Menu menu) {
		super.setMenu(menu);
		
		this.picture_canvas.setMenu( menu );	
	}
	
/*-----------------------------------------------------------------------------------------------------------------------*/	
		
	/**
	 * The widget dispose method
	 */
	
	@Override
	public void dispose() {
			
		if( this.scaled_image != null )
			this.scaled_image.dispose();
		
		this.fill_color.dispose();
		this.border_color.dispose();
		
		super.dispose();
	}
	
/*-----------------------------------------------------------------------------------------------------------------------*/
	
}
