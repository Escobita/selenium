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

package com.serli.helium.core.modelfactory.extension;

import java.beans.PropertyChangeListener;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;

import com.serli.helium.core.modelfactory.constants.IModelHandlerConstants;
import com.serli.helium.core.modelfactory.extension.event.ResponseEvent;
import com.serli.helium.core.modelfactory.extension.event.listener.IResponseListener;
import com.serli.helium.core.modelfactory.internal.Bal;
import com.serli.helium.core.modelfactory.internal.xmltest.SeqTest;


/**
 * The abstract test model class. All plug-in who wants to add new stream test
 * to Helium have to create a model because the GUI use the M-VC architectural
 * pattern. This model is responsible to create and send test in the Bal to the
 * model servlet who send this message to the corresponding client.  
 *   
 * @author Kevin Pollet
 */

public abstract class AbstractTestModel {

/*----------------------------------------------------------------------------------------------------------------------------*/	
	
	private Bal bal;
	private Job read_job;
	private SeqTest test_sequence;
	private byte[] stream_data;
	private boolean response_send;
	
	private HttpServletRequest request;
	private ArrayList< IResponseListener > listener_response_list;
	
/*----------------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * The AbstractTestModel default constructor
	 */
	
	public AbstractTestModel(){
						
		this.stream_data = null;
		this.test_sequence = new SeqTest();
		this.response_send = false;
		
		this.listener_response_list = new ArrayList< IResponseListener >();
	}
	
/*----------------------------------------------------------------------------------------------------------------------------*/	

	/**
	 * The model init method (stream data are not accessible )
	 * 
	 * @param request HTTP request
	 * @param bal the BAL to send a message to the request thread
	 * 
	 * @noreference This method is not intended to be referenced by clients.
	 */
	
	final public void init( HttpServletRequest request, Bal bal ){ //Dependency injection
		
		this.request = request;
		this.bal = bal;	
		this.read_job = new Job("Read " + request.getParameter("url") + " content" ){
			
			@Override
			protected IStatus run(IProgressMonitor monitor) {
					
				IStatus status = Status.OK_STATUS;
				
				try {
				
					ByteArrayOutputStream buffer = new ByteArrayOutputStream();
					InputStream input = AbstractTestModel.this.request.getInputStream();
					
					monitor.beginTask( "Read" , AbstractTestModel.this.request.getContentLength() );
					
						int read = -1;
						
						do{
												
							read = input.read();
					
							if( read != -1 ){
								
								buffer.write( read );						
								monitor.internalWorked(1.0f);
							}
							
							
						}while( read!= -1 && !monitor.isCanceled() );						
					
					
					if( monitor.isCanceled() ) status = Status.CANCEL_STATUS;
					else stream_data = buffer.toByteArray();
					
					
				} catch ( Exception e ){ status = new Status( IStatus.ERROR, IModelHandlerConstants.PLUGIN_ID , "Error in client read post data", e); }
				  finally{
					
					monitor.done();
				 }
				  

				return status;
			}
			
			
		};
				
	}
	
/*----------------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * This method is called after model initialisation
	 * 
	 * Client can override this method to initialise their model
	 */
	
	public void postInit(){}
	
/*----------------------------------------------------------------------------------------------------------------------------*/	

	/**
	 * Add a response listener (this method has no effect)
	 * if the listener already exist	
	 *
	 * @param the response listener
	 */
	
	public void addResponseListener( IResponseListener listener ){
		
		if( !this.listener_response_list.contains( listener ) )		
			this.listener_response_list.add( listener );
	}

/*----------------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * Remove a response listener 
	 * 
	 * @param the response listener
	 * @return true if success false otherwise
	 */
	
	public boolean removeResponseListener( IResponseListener listener ){
		
		return this.listener_response_list.remove( listener );	
	}
	
/*----------------------------------------------------------------------------------------------------------------------------*/	

	/**
	 * Add a property change listener (this method has no effect)
	 * if the listener already exist	
	 *
	 * @param the property change listener
	 */
	
	public void addPropertyChangeListener( PropertyChangeListener listener ){
		
		this.test_sequence.addPropertyChangeListener( listener );
	}

/*----------------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * Remove a property change listener 
	 * 
	 * @param the property change listener
	 */
	
	public void removePropertyChangeListener( PropertyChangeListener listener ){
		
		this.test_sequence.removePropertyChangeListener( listener );	
	}
	
/*----------------------------------------------------------------------------------------------------------------------------*/

	/**
	 * Add a job change listener (this method has no effect)
	 * if the listener already exist	
	 *
	 * @param the job listener
	 */
	
	public void addJobChangeListener( IJobChangeListener listener ){
		
		this.read_job.addJobChangeListener( listener );
	}

/*----------------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * Remove a job change listener 
	 * 
	 * @param the job listener
	 */
	
	public void removeJobChangeListener( IJobChangeListener listener ){
		
		this.read_job.removeJobChangeListener( listener );	
	}
	
/*----------------------------------------------------------------------------------------------------------------------------*/

	/**
	 * Read the model data (call only one times)
	 * 
	 * you can get event by add a Job change listener
	 */
	
	public void readModelData(){
		
		if( this.read_job.getState() == Job.NONE && this.stream_data == null) 
			this.read_job.schedule(); //start job
	}

/*----------------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * Send empty model sequence
	 */
	
	public void sendEmptyTestSequence() {
				
		String xmlseq = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<seqtest>\n</seqtest>\n";
					
		this.bal.postMessage( xmlseq );			
		this.response_send = true;
	}	
	
/*----------------------------------------------------------------------------------------------------------------------------*/	
		
	/**
	 * Send the test model sequence
	 */
	
	public void sendTestSequence() {
				
		String xmlseq = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + this.test_sequence.toXml();
					
		this.bal.postMessage( xmlseq );
		this.response_send = true;
			
		//Call listener (response send)
		ResponseEvent event = new ResponseEvent(this,xmlseq);
		for( IResponseListener l: this.listener_response_list )
			l.responseSend(event);		
				
		
	}
	
/*----------------------------------------------------------------------------------------------------------------------------*/		

	/**
	 * Get if the model response have been send
	 * 
	 * @return true if a response was send
	 */
	
	public boolean isResponseSend(){
		
		return this.response_send;
	}	

/*----------------------------------------------------------------------------------------------------------------------------*/		
	
	/**
	 * Get if the model response have been send
	 * 
	 * @return true if a response was send
	 */
	
	public void setResponseSend( boolean response_send ){
		
		this.response_send = response_send;
	}	
	
/*----------------------------------------------------------------------------------------------------------------------------*/		
	
	
	/**
	 * Get the test sequence
	 * 
	 * @return the sequence of test
	 */
	
	public SeqTest getTestSequence(){
		
		return this.test_sequence;
	}
	
/*----------------------------------------------------------------------------------------------------------------------------*/	

	/**
	 * Get the test HTTP request
	 * 
	 * @return the HTTP request
	 */
	
	public HttpServletRequest getRequest(){
		
		return this.request;
	}
	
/*----------------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * Get the request data
	 * 
	 * @return the data
	 */
	
	public byte[] getData(){
		
		return this.stream_data;
	}
	
/*----------------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * Get the read data job
	 * 
	 * @return the job
	 */

	public Job getReadDataJob(){
		
		return this.read_job;
	}	
	
/*----------------------------------------------------------------------------------------------------------------------------*/	
	
	
}
