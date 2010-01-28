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
 * This JavaScript file provides useful method for JavaScript
 * 
 * @author Kevin Pollet
 */

function JSUtil(){}
 
JSUtil.observe = function( object, method, func  ){
	
	var old_func = object[method];

	object["_"+method] = old_func;
	object[method] = function(){
	
		func( arguments );
		old_func.apply(this,arguments);
	};	
	
};

JSUtil.bind = function( object, func ){
	
	return function(){ func.apply( object, arguments ); }				
};

JSUtil.inherit = function( destination, source ){
	
    for (var element in source) {
        destination[element] = source[element];
    } 
    
};
