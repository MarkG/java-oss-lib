/**
 * 
 */
package com.trendrr.oss.networking.strest;

import java.util.concurrent.Semaphore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



/**
 * 
 * Allows us to do synchronous requests
 * 
 * @author Dustin Norlander
 * @created Mar 15, 2011
 * 
 */
class StrestSynchronousRequest implements StrestCallback{

	protected Log log = LogFactory.getLog(StrestSynchronousRequest.class);

	Semaphore lock = new Semaphore(1, true);
	StrestResponse response;
	Throwable error;
	
	public StrestSynchronousRequest() {
		try {
			//take the only semaphore
			lock.acquire(1);
		} catch (InterruptedException e) {
			log.error("Caught", e);
		}
	}
	
	public StrestResponse awaitResponse() throws Throwable {
		try {
			//try to aquire a semaphore, none is available so we wait.
			lock.acquire(1);
		} catch (InterruptedException e) {
			log.error("Caught", e);
			throw e;
		}
		if (this.error != null) {
			throw this.error;
		}
		
		return this.response;
	}
	
	/* (non-Javadoc)
	 * @see com.trendrr.oss.networking.strest.StrestCallback#messageRecieved(com.trendrr.oss.networking.strest.StrestResponse)
	 */
	@Override
	public void messageRecieved(StrestResponse response) {
		this.response = response;
		//release the single semaphore.
		lock.release(1);
	}

	/* (non-Javadoc)
	 * @see com.trendrr.oss.networking.strest.StrestCallback#txnComplete()
	 */
	@Override
	public void txnComplete() {
		//do nothing.  txn should always be complete!
	}

	/* (non-Javadoc)
	 * @see com.trendrr.oss.networking.strest.StrestCallback#error(java.lang.Throwable)
	 */
	@Override
	public void error(Throwable x) {
		this.error = x;
	}
}
