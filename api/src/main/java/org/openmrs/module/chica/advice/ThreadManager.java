/**
 * 
 */
package org.openmrs.module.chica.advice;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.chirdlutil.ReadWriteManager;

/**
 * @author tmdugan
 * 
 */
public class ThreadManager
{
	private static Log log = LogFactory.getLog(ThreadManager.class);

	private static Collection<Thread> activeThreads = Collections
			.synchronizedCollection(new ArrayList<Thread>());

	private static Integer totalThreadsCreated = 0;
	private static Integer purgedThreads = 0;
	private static final ReadWriteManager readWriteManager = new ReadWriteManager();

	public static void startThread(Thread thread)
	{
		try
		{
			readWriteManager.getWriteLock();
			try {
	            activeThreads.add(thread);
            }
            catch (Exception e) {
	            log.error("",e);
            }finally{
			readWriteManager.releaseWriteLock();
            }

			thread.start();
			totalThreadsCreated++;
			threadMonitor(); // clean out inactive threads each time a new
			// thread is added
		} catch (Exception e)
		{
			log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
		}
	}

	private static void threadMonitor()
	{
		try
		{
			// log the number of active threads so we can be sure that they are
			// not building up
			// and filling up the JVM memory
			log.info("Total number of threads created: " + totalThreadsCreated);
			log.info("Total number of threads purged: " + purgedThreads);

			readWriteManager.getReadLock();
			int activeThreadsLength = 0;
            Thread[] activeThreadsArray = null;
            try {
	            activeThreadsLength = activeThreads.size();
	            log.info("Number of active threads: " + activeThreadsLength);
	            activeThreadsArray = new Thread[activeThreadsLength];
	            activeThreads.toArray(activeThreadsArray);
            }
            catch (Exception e) {
	            log.error("",e);
            }finally{
            	readWriteManager.releaseReadLock();
            }

			// remove inactive threads
            if(activeThreadsArray != null){
                for (int i = 0; i < activeThreadsLength; i++)
                {
                    Thread currThread = activeThreadsArray[i];
                    if (!currThread.isAlive())
                    {
                        readWriteManager.getWriteLock();
                        try {
                            activeThreads.remove(currThread);
                        }
                        catch (Exception e) {
                            log.error("",e);
                        }finally{
                            readWriteManager.releaseWriteLock();
                        }
                        purgedThreads++;
                    }
                }
            }
		} catch (Exception e)
		{
			log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
		}
	}
}
