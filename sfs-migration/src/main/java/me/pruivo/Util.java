package me.pruivo;

import org.apache.logging.log4j.Logger;

import me.pruivo.data.Order;

/**
 * TODO! document this
 *
 * @author Pedro Ruivo
 * @since 12
 */
public final class Util {

   private Util() {
   }

   public static void assertEquals(Order expected, Order value, Logger logger) {
      if (expected.getOrderId().equals(value.getOrderId()) &&
            expected.getDescription().equals(value.getDescription()) &&
            expected.getStatus().equals(value.getStatus())) {
         return;
      }
      logger.error("Wrong order. Expected=" + expected + ", value=" + value);
   }

}
