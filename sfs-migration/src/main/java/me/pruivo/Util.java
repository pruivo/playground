package me.pruivo;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import org.apache.logging.log4j.Logger;
import org.infinispan.commons.marshall.WrappedByteArray;

/**
 * TODO! document this
 *
 * @author Pedro Ruivo
 * @since 12
 */
public final class Util {

   private Util() {
   }


   public static void assertEquals(Object expected, Object value, Logger logger) {
      if (Objects.equals(expected, value)) {
         return;
      }
      logger.error("Wrong value. Expected=" + expected + ", value=" + value);
   }

   public static String key(int id) {
      return "key_" + id;
   }

   public static WrappedByteArray wrappedKey(int id) {
      return new WrappedByteArray(("key_" + id).getBytes(StandardCharsets.UTF_8));
   }

   public static String value(int id) {
      return "value_" + id;
   }

   public static String value(String suffix, int id) {
      return "value_" + id + suffix;
   }

   public static WrappedByteArray wrappedValue(int id) {
      return new WrappedByteArray(("value_" + id).getBytes(StandardCharsets.UTF_8));
   }

   public static WrappedByteArray wrappedValue(String prefix, int id) {
      return new WrappedByteArray(("value_" + id + prefix).getBytes(StandardCharsets.UTF_8));
   }

}
