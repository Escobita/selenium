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

package com.serli.helium.core.modelfactory.internal.xmltest;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;


/**
 * This class represents the XML param element. See the resp.dtd file for more details.
 * A param element have a unque name and a value and an "optional" boolean for GUI purpose. 
 * 
 * @author Kevin Pollet
 */

public class Param {

/*----------------------------------------------------------------------------------------------------------------------*/	

	public final static String PROPERTY_NAME = "Name";
	public final static String PROPERTY_VALUE = "Value";	
	
/*----------------------------------------------------------------------------------------------------------------------*/	

	private PropertyChangeSupport change_support;
	
	private Test parent;
	
	private String p_name;
	private String p_value;
	private boolean p_option;
	
/*----------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * Construct a test parameter
	 * 
	 * @param test parameter test parent
	 * @param name the parameter name
	 * @param optional true if this parameter is optional
	 * 
	 * @throws IllegalArgumentException if parameter name is null or empty
	 */
	
	public Param( String name, boolean optional ) throws IllegalArgumentException{
		this(name,null, optional );	
		
	}
	
/*----------------------------------------------------------------------------------------------------------------------*/
	
	
	/**
	 * Construct a test parameter
	 * 
	 * @param name the parameter name
	 * @param value the parameter value
	 * @param optional true if this parameter is optional
	 * 
	 * @throws IllegalArgumentException if parameter name is null or empty or parent is null
	 */
	
	public Param( String name, String value, boolean optional ) throws IllegalArgumentException{
		
		if( name == null || name.isEmpty() ) throw new IllegalArgumentException("Param name cannot be null or empty");
					
		this.p_name  = name;
		this.p_value = value;
		this.p_option = optional;
		this.parent = null;		
		this.change_support = new PropertyChangeSupport( this );
	}
	
/*----------------------------------------------------------------------------------------------------------------------*/	

	/**
	 * @see PropertyChangeSupport#addPropertyChangeListener(PropertyChangeListener)
	 */
	
	public void addPropertyChangeListener( PropertyChangeListener listener ){
	
		this.change_support.addPropertyChangeListener(listener);
	}	
	
/*----------------------------------------------------------------------------------------------------------------------*/	

	/**
	 * @see PropertyChangeSupport#removePropertyChangeListener(PropertyChangeListener)
	 */
	
	public void removePropertyChangeListener( PropertyChangeListener listener ){
	
		this.change_support.removePropertyChangeListener(listener);
	}	
	
/*----------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * Get the parameter name
	 * 
	 * @return the parameter name
	 */
	
	public String getName(){
		
		return this.p_name;
	}

/*----------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * Get the parameter value
	 * 
	 * @return the parameter value
	 */
	
	public String getValue(){
		
		return this.p_value;
	}	

/*----------------------------------------------------------------------------------------------------------------------*/	

	/**
	 * Get the boolean optional value
	 * 
	 * @return the value
	 */
	
	public boolean isOptional(){
			
		return this.p_option;
	}	
	
/*----------------------------------------------------------------------------------------------------------------------*/	

	/**
	 * Get the parent test
	 * 
	 * @return the parent parameter test
	 */
	
	public Test getParent(){
	
		return this.parent;
	}

/*----------------------------------------------------------------------------------------------------------------------*/		
	
	/**
	 * Set the parameter name
	 * 
	 * @param name the new parameter name
	 * @throws IllegalArgumentException if parameter name is null or empty
	 * @throws UniqueException if parameter name already exist
	 */
	
	public void setName( String name ) throws IllegalArgumentException, UniqueException{
		
		if( !name.equals(this.p_name) ){
		
			if( name == null || name.isEmpty() ) throw new IllegalArgumentException("Param name cannot be null or empty");
			if( this.parent.getParam( name ) != null ) throw new UniqueException("Param name already exist");
		
			String oldname = this.p_name;		
			this.p_name = name;	
		
			this.change_support.firePropertyChange( PROPERTY_NAME , oldname, this.p_name );
		}
	}	
	
/*----------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * Set the parameter value
	 * 
	 * @param value the new parameter value
	 */
	
	public void setValue( String value ){
		
		String oldvalue = this.p_value;
		this.p_value = value;
		
		this.change_support.firePropertyChange( PROPERTY_VALUE , oldvalue, this.p_value );
	}

/*----------------------------------------------------------------------------------------------------------------------*/	

	/**
	 * Set the parameter parent
	 * 
	 * @param parent the new parent
	 */
	
	protected void setParent( Test parent ){
		
		if( this.parent != null ) 
			this.parent.removeTestParameter(this);
		
		this.parent = parent;		
	}
	
/*----------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * Get the XML value of this
	 * parameter 
	 * 
	 * @return the XML string
	 */
	
	public String toXml(){ 
		
		String value = this.p_value.replaceAll("\r", "");		
		
		return "<param name=\""+ this.p_name +"\">" +
			   "<![CDATA["+value+"]]>" +
			   "</param>";
	}
	
/*----------------------------------------------------------------------------------------------------------------------*/	
	
}
