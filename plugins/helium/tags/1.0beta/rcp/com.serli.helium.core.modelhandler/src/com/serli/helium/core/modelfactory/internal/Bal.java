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

package com.serli.helium.core.modelfactory.internal;

/**
 * This Bal (like a mal box) permits thread synchronizing. When a thread want to get a message he waits
 * until an other thread post-it.
 * 
 * @author Kevin Pollet
 */

public class Bal {

/*----------------------------------------------------------------------------------------------------------------------------*/	
	
	private boolean is_message;
	private String message;

/*----------------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * The default Bal Constructor 
	 */
	
	public Bal(){
		
		this.is_message = false;
		this.message = null;
	}

/*----------------------------------------------------------------------------------------------------------------------------*/	

	/**
	 * Check if there is a message to read
	 * 
	 * @return true if there is a message false otherwise
	 */
	
	public boolean isMessage(){
		
		return this.is_message;
	}
	
/*---------------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * Try to get a message
	 * 
	 * @return the message if one else null
	 */
	
	public synchronized String tryGetMessage(){
		
		if( is_message ) return message;
		else return null;
	}
	
/*---------------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * Get the message (wait if none)
	 *  
	 * @return the response
	 */
	
	public synchronized String getMessage(){
	
		try {
			
			while( !is_message ){
					
				this.wait();			
			}			
		
			this.is_message = false;
			
		}catch(InterruptedException e){ e.printStackTrace(); }
		
		return message;		
	}
	
/*---------------------------------------------------------------------------------------------------------------------------*/	

	/**
	 * Post a message a wake up the thread who are waiting
	 * 
	 * @param message the message
	 */
	
	public synchronized void postMessage( String message ){
		
		this.message = message;
		this.is_message = true;
		
		this.notifyAll();
	}
	
/*---------------------------------------------------------------------------------------------------------------------------*/	
	
}
