package me.pruivo.runner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.infinispan.Cache;

import me.pruivo.Util;
import me.pruivo.data.Order;

/**
 * TODO! document this
 *
 * @author Pedro Ruivo
 * @since 12
 */
public class Infinispan125Runner implements Runner {

   private static final Logger log = LogManager.getLogger(Infinispan125Runner.class);

   @Override
   public void execute(Cache<String, Order> cache) {
      //make sure it is able to read
      for (int i = 0; i < 500; ++i) {
         Order value = cache.get("order_" + i);
         Util.assertEquals(Order.newOrder(i), value, log);
      }
      for (int i = 500; i < 1000; ++i) {
         Order value = cache.get("order_" + i);
         Util.assertEquals(Order.newOrder(i).update("12-corrupted"), value, log);
      }
      for (int i = 1500; i < 21000; ++i) {
         Order value = cache.get("order_" + i);
         Util.assertEquals(Order.newOrder(i), value, log);
      }
      for (int i = 0; i < 500; ++i) {
         String key = "order_" + i;
         Order value = cache.get(key);
         cache.put(key, value.update("12.1.5"));
      }
      for (int i = 0; i < 500; ++i) {
         Order value = cache.get("order_" + i);
         Util.assertEquals(Order.newOrder(i).update("12.1.5"), value, log);
      }
   }

   @Override
   public void executeSingle(Cache<String, Order> cache) {
      Order value = cache.get("order_0");
      Util.assertEquals(Order.newOrder(0), value, log);
   }
}
