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


//Helium tab object as Selenium (LogView...)
//Look at Selenium "editor.js" file
 
function HeliumView(){
	
  this.name = "helium";
}

HeliumView.prototype.show = function() {
	document.getElementById(this.name + "View").hidden = false;
	document.getElementById(this.name + "Tab").setAttribute("selected", "true");
};

HeliumView.prototype.hide = function() {
	document.getElementById(this.name + "Tab").removeAttribute("selected");
	document.getElementById(this.name + "View").hidden = true;
};

/*
 * Create object 
 */

var helium = new HeliumView();
