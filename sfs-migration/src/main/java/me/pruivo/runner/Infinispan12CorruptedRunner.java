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
public class Infinispan12CorruptedRunner implements Runner {

   private static final Logger log = LogManager.getLogger(Infinispan12CorruptedRunner.class);

   @Override
   public void execute(Cache<String, Order> cache) {
      //make sure it is able to read
      String name = cache.getName();
      if (name.contains("irac") || name.contains("tx")) {
         return;
      }
      for (int i = 0; i < 1500; ++i) {
         Order value = cache.get("order_" + i);
         Util.assertEquals(Order.newOrder(i), value, log);
      }
      // replace entries between 500 .. 1000
      for (int i = 500; i < 1000; ++i) {
         Order value = cache.get("order_" + i);
         cache.put("order_" + i, value.update("12-corrupted"));
      }
      // add new entries
      for (int i = 1500; i < 2000; ++i) {
         cache.put("order_" + i, Order.newOrder(i));
      }
   }

   @Override
   public void executeSingle(Cache<String, Order> cache) {
      String name = cache.getName();
      if (name.contains("irac") || name.contains("tx")) {
         return;
      }
      Order value = cache.get("order_0");
      Util.assertEquals(Order.newOrder(0), value, log);
   }
}
