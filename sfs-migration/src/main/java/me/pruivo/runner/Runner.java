package me.pruivo.runner;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;

import me.pruivo.data.Order;

/**
 * TODO! document this
 *
 * @author Pedro Ruivo
 * @since 12
 */
public interface Runner {

   void execute(Cache<String, Order> cache) throws Exception;

   void executeSingle(Cache<String, Order> cache) throws Exception;

}
