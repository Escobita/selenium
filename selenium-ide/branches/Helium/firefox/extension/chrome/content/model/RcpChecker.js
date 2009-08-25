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

/**
 * The Rcp checker
 * 
 * @author Kevin Pollet
 */

function RcpChecker(){
	
	this.running = false;
	this.timer = null;
	this.listeners = [];
	
	if( typeof RcpChecker.initialized == "undefined" ){
	
		
		RcpChecker.prototype.isRunning = function(){ return this.running; };
		
		RcpChecker.prototype.setRunning = function( run_state ){ 
			
			this.old_state = this.running;
			this.running = run_state;
			
			if( run_state != this.old_state ){
				
				for( var i = 0 ; i < this.listeners.length ; i++ ){
					this.listeners[i].rcpStateChanged( run_state );
				}				
			}		
			 
		};
	
		RcpChecker.prototype.addRcpListener = function( listener ){ 
			
			var exist = false;
			
			for( var i = 0 ; !exist && i < this.listeners.length ; i++ ){ 

				if( this.listeners[i] == listener ){
					exist=true;
				}
			}
		
			if( !exist ) this.listeners.push( listener );
			
		};
		
		RcpChecker.prototype.removeRcpListener = function( listener ){
			
			var index = -1;
			
			for( var i = 0 ; index == -1 && i < this.listeners.length ; i++ ){ 
	
				if( this.listeners[i] == listener )
					index = i;
			}
	
			if( index != -1 ){
			
				for( var i = index ; i < (this.listeners.length-1) ; i++ )
					this.listeners[i] = this.listeners[i+1];						
				
			
				this.listeners.pop();
			}	
				
		};
		
		RcpChecker.prototype.startCheckRcpState = function( ){
			
			this.timer = setInterval( JSUtil.bind(this,this.check) , 2000);
		};
		
		RcpChecker.prototype.stopCheckRcpState = function( ){
			
			clearInterval( this.timer );
		};
		
		RcpChecker.prototype.check = function(){
		
			var port = HEPreferences.getServerPort();
			var name = HEPreferences.getServerName();
			var instance = this;
			
			var xmlhttp = new XMLHttpRequest();
			xmlhttp.open("GET", "http://"+name+":"+port+"/test", true );
			xmlhttp.onreadystatechange = function(){
	
				if( (this.readyState == 4 && this.status == 0) || (this.readyState == 4 && this.status == 404) ){
					
					if( instance.running != false)
						instance.setRunning(false);
					
				}else if( this.readyState == 4 ){
					
					if( instance.running != true)
						instance.setRunning(true);
				}
				
			};
			
			xmlhttp.send( null );
		};			
		
				
		RcpChecker.initialized = true;
	}
	
	
}





