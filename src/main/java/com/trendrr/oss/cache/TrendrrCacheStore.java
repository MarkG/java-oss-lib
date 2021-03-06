/**
 * 
 */
package com.trendrr.oss.cache;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trendrr.oss.concurrent.Initializer;
import com.trendrr.oss.concurrent.LazyInit;


/**
 * 
 * Can store multiple cache implementations
 * 
 * @author Dustin Norlander
 * @created Jan 3, 2012
 * 
 */
public class TrendrrCacheStore {

	protected static Log log = LogFactory.getLog(TrendrrCacheStore.class);
	
	protected ConcurrentHashMap<String, TrendrrCache> caches = new ConcurrentHashMap<String, TrendrrCache>();
	protected ConcurrentHashMap<String, LazyInit> cacheLocks = new ConcurrentHashMap<String, LazyInit>();
	
	protected static TrendrrCacheStore instance = new TrendrrCacheStore();
	/**
	 * Singleton instance.  Yeah, thats right, an f'in singleton. 
	 * @return
	 */
	public static TrendrrCacheStore instance() {
		return instance;
	}
	
	/**
	 * adds a cache to the store.
	 * @param key
	 * @param cache
	 */
	public void addCache(String key, TrendrrCache cache) {
		this.caches.put(key, cache);
	}
	
	/**
	 * gets the cache if it already exists.
	 * @param key
	 * @return
	 */
	public TrendrrCache getCache(String key) {
		return this.caches.get(key);
	}
	
	/**
	 * gets the cache if it already exists, if not it will call the initializer function
	 * @param key
	 * @param initializer
	 * @return
	 */
	public TrendrrCache getCache(String key, Initializer<TrendrrCache> initializer) {
		TrendrrCache c = this.getCache(key);
		if (c != null)
			return c;
		
		cacheLocks.putIfAbsent(key, new LazyInit());
		LazyInit lock = cacheLocks.get(key);
		
		if (lock.start()) {
			try {
				c = initializer.init();
				if (c != null)
					caches.put(key, c);
			} finally {
				lock.end();
			}
		}
		return this.getCache(key);
	}
}
