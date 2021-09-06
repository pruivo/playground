package me.pruivo;

import java.util.UUID;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;

import me.pruivo.data.Person;
import me.pruivo.listener.CustomEventListener;

/**
 * @author Pedro Ruivo
 */
public class Application {

   public int run() throws InterruptedException {
      try (RemoteCacheManager cacheManager = new RemoteCacheManager()) {


         // simple dist_sync cache
         ConfigurationBuilder builder = new ConfigurationBuilder();
         builder.clustering().cacheMode(CacheMode.DIST_SYNC);
         RemoteCache<String, Person> cache = cacheManager.administration().getOrCreateCache("test-cache", builder.build());

         cache.addClientListener(new CustomEventListener());

         cache.put(UUID.randomUUID().toString(), new Person("One", "Person", 33, "x"));
         cache.put(UUID.randomUUID().toString(), new Person("Another", "Person", 56, "y"));
         cache.put(UUID.randomUUID().toString(), new Person("John", "Doe", 20, "z"));

         // sleep for a while to get the events
         Thread.sleep(10000);
      }
      return 0;
   }


   public static void main(String[] args) throws InterruptedException {
      System.exit(new Application().run());
   }


}
