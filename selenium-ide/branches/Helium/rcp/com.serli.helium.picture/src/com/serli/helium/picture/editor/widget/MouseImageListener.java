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



import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;

/**
 * The Mouse listener
 * 
 * @author Kevin Pollet
 */

public class MouseImageListener extends MouseAdapter implements MouseMoveListener {

/*-----------------------------------------------------------------------------------------------------------------------------------------------*/	

	private ImageWidget widget;
	
	private boolean selection;
	private int x,y,ex,ey;
	
/*-----------------------------------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * The mouse image listener
	 * 
	 * @param 
	 */
	
	public MouseImageListener( ImageWidget widget ){
		
		this.widget = widget;
		this.selection = false;
	}

/*-----------------------------------------------------------------------------------------------------------------------------------------------*/	
	
	@Override
	public void mouseUp(MouseEvent e) {

		if( selection && e.button == 1 ){
			
			if ( e.x == this.x && e.y == this.y ){
				
				this.selection = false;
				this.widget.clearSelection();
				
			}else{
				
				this.selection = false;
				
				//Adapt user selection

					float zoom_factor = this.widget.getZoom()/100.0f;
					
		            int x = (int)(this.x/zoom_factor); x  *= zoom_factor;
		            int y = (int)(this.y/zoom_factor); y  *= zoom_factor;
		            int ex = (int)(this.ex/zoom_factor);   ex *= zoom_factor;
		            int ey = (int)(this.ey/zoom_factor);   ey *= zoom_factor;
							            
				//Set rectangle selection
				
					int width = Math.abs( ex-x+1 );
					int t_x = x > ex ? ex : x;
					
					int height = Math.abs( ey-y+1 );
					int t_y = y > ey ? ey : y;
				
				//Set the selection and fire event
	
					widget.setSelection( new Rectangle(t_x,t_y,width,height) );
					widget.fireSelection();
					
			}
			
		}
		
	}
	
/*-----------------------------------------------------------------------------------------------------------------------------------------------*/	

	@Override
	public void mouseDown(MouseEvent e) {
		
		if( e.button == 1 ){
		
			this.selection = true;
			
			this.x = e.x;
			this.y = e.y;		
		}
		
	}
	
/*-----------------------------------------------------------------------------------------------------------------------------------------------*/	
	
	@Override
	public void mouseMove(MouseEvent e) {

		
		if( selection ){
		
			//Calc bounds
			
				float fact = this.widget.getZoom()/100.0f;
				Image image = this.widget.getImage();
						
				int maxx = (int)(image.getBounds().width*fact);
				int maxy = (int)(image.getBounds().height*fact);

            //Set rectangle selection
    			
    			int width = Math.abs( e.x-this.x+1 );
    			int t_x = this.x > e.x ? e.x : this.x;
    			
    			int height = Math.abs( e.y-this.y+1 );
    			int t_y = this.y > e.y ? e.y : this.y;	

    			
    		if( t_x >=0 && (t_x+width) < maxx && t_y >=0 && (t_y+height) < maxy  ){
    			
    			//Set last valid mouse coordinate
    			
    				this.ex = e.x;
    				this.ey = e.y;
    			
    			//Move scrollBar
    			
	            	int ox = this.widget.getOrigin().x;
	            	int oy = this.widget.getOrigin().y;
	
	            	if( (e.x - ox) > (this.widget.getClientArea().width-20) )  this.widget.setOrigin( ox+5 , oy );
	            	else if( e.x < (ox+20) ) this.widget.setOrigin( ox-5 , oy );
	            
	            	if( (e.y - oy) > (this.widget.getClientArea().height-20) ) this.widget.setOrigin( ox , oy+5 );
	            	else if( e.y < (oy+20) ) this.widget.setOrigin( ox , oy-5 );	
    			
    			//draw selection
    				this.widget.setSelection( new Rectangle(t_x,t_y,width,height) );
    			
    		}
    			
		}	
			
	}
	
/*-----------------------------------------------------------------------------------------------------------------------------------------------*/	
	

}
