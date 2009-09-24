/**
 * 
 */
package org.openmrs.module.chica.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author tmdugan
 * 
 */
public class ReadWriteManager
{
	private Log log = LogFactory.getLog(this.getClass());

	private boolean writingInProgress = false;
	private int writersWaiting = 0;
	private int readers = 0;

	public void logReadWriteInfo(){
		log.info("Writing in progress: "+writingInProgress);
		log.info("# of writers waiting: "+writersWaiting);
		log.info("# of readers: "+readers);
	}
	
	/**
	 * Read-Write locking code attributed to Nasir Khan
	 * http://www.developer.com/java/article.php/951051
	 */
	synchronized public void getReadLock()
	{
		// writing is more important so always wait on writer
		while (writingInProgress)
		{
			try
			{
				wait();
			} catch (InterruptedException ie)
			{
			}
		}
		readers++;
	}

	synchronized public void releaseReadLock()
	{
		readers--;
		if ((readers == 0) & (writersWaiting > 0))
		{
			notifyAll();
		}
	}

	synchronized public void getWriteLock()
	{
		writersWaiting++;
		while ((readers > 0) | writingInProgress)
		{
			try
			{
				wait();
			} catch (InterruptedException ie)
			{
			}
		}
		writersWaiting--;
		writingInProgress = true;
	}

	synchronized public void releaseWriteLock()
	{
		writingInProgress = false;
		notifyAll();
	}

}
