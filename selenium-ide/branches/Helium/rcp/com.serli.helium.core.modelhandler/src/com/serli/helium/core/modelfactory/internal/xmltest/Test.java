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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;


/**
 * This class represents the test element in the XML servlet response (see resp.dtd for more details).
 * A test element contains a parameter list.
 * 
 * @author Kevin Pollet
 */

public class Test implements PropertyChangeListener{

/*------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/	
	
	public final static String PROPERTY_TYPE = "Type";
	public final static String PROPERTY_PARAM_ADDED = "ParameterAdded";
	public final static String PROPERTY_PARAM_REMOVED = "ParameterRemoved";
	
/*------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/	
	
	private String type;
	private SeqTest parent;
		
	private ArrayList< Param > parameter_list;
		
	private PropertyChangeSupport change_support;

/*------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * The Test constructor
	 * 
	 * @param type the test type
	 * @param other_type null or test type if type equals OTHER
	 * @throws IllegalArgumentException if type is null or empty
	 */
	
	public Test( String type ) throws IllegalArgumentException{
	
		if( type == null || type.isEmpty() ) throw new IllegalArgumentException("Test type cannot be null");

		this.type = type;
		this.parent = null;
		this.parameter_list = new ArrayList< Param >();		
		
		this.change_support = new PropertyChangeSupport( this );	
	}

/*------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/	

	/**
	 * @see PropertyChangeSupport#addPropertyChangeListener(PropertyChangeListener)
	 */
	
	public void addPropertyChangeListener( PropertyChangeListener listener ){
	
		this.change_support.addPropertyChangeListener(listener);
	}	
	
/*------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/	

	/**
	 * @see PropertyChangeSupport#removePropertyChangeListener(PropertyChangeListener)
	 */
	
	public void removePropertyChangeListener( PropertyChangeListener listener ){
	
		this.change_support.removePropertyChangeListener(listener);
	}	
	
/*------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * Add a parameter to this test
	 * 
	 * @param p the new parameter
	 * @throws UniqueException if a parameter with the same name exist
	 */
	
	public void addParameter( Param p ) throws UniqueException{
		
		if( this.getParam( p.getName() ) != null ) throw new UniqueException("Parameter with name : " + p.getName() + " already exist");
		
		
		this.parameter_list.add( p );
		p.setParent( this );
		p.addPropertyChangeListener( this );
		
		this.change_support.firePropertyChange( PROPERTY_PARAM_ADDED , null, p );
	}
	
/*------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/	

	/**
	 * Insert a parameter in this test
	 * 
	 * @param p the new parameter
	 * @param index the parameter index
	 * @throws UniqueException if a parameter with the same name exist
	 */
	
	public void insertParameter( Param p, int index ) throws UniqueException{
		
		if( this.getParam( p.getName() ) != null ) throw new UniqueException("Parameter with name : " + p.getName() + " already exist");
		
		
		this.parameter_list.add( index, p );
		p.setParent( this );
		p.addPropertyChangeListener( this );
		
		this.change_support.firePropertyChange( PROPERTY_PARAM_ADDED , null, p );
	}
	
/*------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/		
	
	/**
	 * Remove a test parameter
	 * 
	 * @param p the test parameter
	 * @return true if success false otherwise
	 */
	
	public boolean removeTestParameter( Param p ) {
	
		return this.removeTestParameter( p.getName() ) != null;
	}
	
/*------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/	
		
	/**
	 * Remove a test parameter
	 * 
	 * @param name the test parameter name
	 * @return the removed parameter or null if none
	 */
	
	public Param removeTestParameter( String name ) {
	
		Param res = null;
		
			for( int i = 0 ; res == null && i < this.parameter_list.size() ; i++ ){
			
				Param temp =  this.parameter_list.get(i);
				
				if( temp.getName().equals(name) )
					res = temp;
				
			}	
			
			
			if ( res != null ){
				
				this.parameter_list.remove(res);
				
				res.setParent( null );
				res.removePropertyChangeListener( this );
				
				this.change_support.firePropertyChange( PROPERTY_PARAM_REMOVED, res, null );
			}
			
			
		
		return res;
	}

/*------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * Get a test parameter Object
	 * 
	 * @param name the parameter name
	 * @return the parameter object or null if none
	 */
	
	public Param getParam( String name ){
		
		Param res = null;
		
			for( int i = 0 ; res == null && i < this.parameter_list.size() ; i++ ){
			
				Param temp =  this.parameter_list.get(i);
				
				if( temp.getName().equals(name) )
					res = temp;				
			}
		
		return res;	
	}
	
/*------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * Get the test type 
	 * 
	 * @return the test type
	 */
	
	public String getType(){
		
		return this.type;
	}
	
/*------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * Set this test type
	 * 
	 * @param type the test type
	 * @param other_type the other test type if test equals Type.OTHER 
	 * @throws IllegalArgumentException if type is null
	 */
	
	public void setType( String type ) throws IllegalArgumentException { 
		
		if( type == null || type.isEmpty() ) throw new IllegalArgumentException("Test type cannot be null");
		
		String oldtype = this.type;
		this.type = type;
		
		this.change_support.firePropertyChange( PROPERTY_TYPE , oldtype, type );
	}

/*------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/	

	/**
	 * Get All parameter list
	 * 
	 * @return the parameter list
	 */
	
	public ArrayList< Param > getAllParameter(){
		
		return this.parameter_list;
	}
	
/*------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/	

	/**
	 * Remove all parameter from this
	 * test
	 */
	
	public void clear(){
		
		for( int i = 0 ; i < this.parameter_list.size() ; i++ )
			this.removeTestParameter( this.parameter_list.get(i) );
	}
	
/*------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * Get the number of parameter
	 * 
	 * @return the number of parameter 
	 */
	
	public int getNbParameter(){
		
		return this.parameter_list.size();
	}
		
/*------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/	

	/**
	 * Get the test parent
	 * 
	 *@return the test sequence parent
	 */
	
	public SeqTest getParent(){
	
		return this.parent;
	}	
	
/*------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/	

	/**
	 * Set the test parent
	 * 
	 * @param parent the test element parent
	 */
	
	protected void setParent( SeqTest parent ){

		if( this.parent != null )		
			this.parent.removeTest( this );			
				
		this.parent = parent;
	}	
	
/*------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/	

	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		
		this.change_support.firePropertyChange(evt);		
	}	

/*------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * Get the XML value of this
	 * parameter 
	 * 
	 * @return the XML string
	 */
	
	public String toXml(){
		
		String res = "<test type=\""+ this.type + "\">\n";
				
			for( int i=0 ; i < this.parameter_list.size() ; i++ ){
		
				res += this.parameter_list.get(i).toXml() + "\n";
			}
			
		res += "</test>\n";	
			
		return res;
	}

/*------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/		
	
	
}
