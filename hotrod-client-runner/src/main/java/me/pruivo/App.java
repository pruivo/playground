package me.pruivo;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;

/**
 * Hello world!
 */
public class App {
   public static void main(String[] args) {
      try (RemoteCacheManagerHolder holder = new RemoteCacheManagerHolder()) {
         RemoteCache<Integer, String> cache = holder.rcm.getCache("test");
         Random random = ThreadLocalRandom.current();
         int i = 0;
         while (true) {
            cache.put(random.nextInt(10), "value" + (i++));
            Thread.sleep(500);
         }
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
      }
   }

   private static class RemoteCacheManagerHolder implements AutoCloseable {

      private final RemoteCacheManager rcm;

      private RemoteCacheManagerHolder() {
         rcm = new RemoteCacheManager();
      }

      @Override
      public void close() {
         rcm.stop();
      }
   }
}
