package me.pruivo.runner;

import org.infinispan.Cache;

import me.pruivo.data.Order;

/**
 * TODO! document this
 *
 * @author Pedro Ruivo
 * @since 12
 */
public class Infinispan11Runner implements Runner {
   @Override
   public void execute(Cache<String, Order> cache) {
      cache.clear();
      for (int i = 0; i < 1500; ++i) {
         cache.put("order_" + i, Order.newOrder(i));
      }
   }

   @Override
   public void executeSingle(Cache<String, Order> cache) {
      cache.clear();
      cache.put("order_0", Order.newOrder(0));
   }
}
