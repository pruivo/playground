package me.pruivo;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.infinispan.Cache;
import org.infinispan.commons.api.CacheContainerAdmin;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.distribution.ch.KeyPartitioner;
import org.infinispan.factories.ComponentRegistry;
import org.infinispan.jboss.marshalling.core.JBossUserMarshaller;
import org.infinispan.manager.DefaultCacheManager;

/**
 * Example how using {@link Enum} in keys are not possible in Infinispan.
 * <p>
 * {@link Enum#hashCode()} changes between runtimes; each Infinispan node will have it owns {@code hashcode} value and
 * each node will put the key in a different segment.
 *
 * @author Pedro Ruivo
 * @since 1.0
 */
public class App {

   public static void main(String[] args) throws IOException {
      try (DefaultCacheManager cacheManager = new DefaultCacheManager(defaultGlobalConfiguration())) {
         Cache<MyKey, String> cache = cache(cacheManager);
         KeyPartitioner keyPartitioner = keyPartitioner(cache);
         cache.addListener(new MyListener(keyPartitioner));

         //this is problematic
         MyKey enumKey = new MyKey(TimeUnit.SECONDS);
         //this works
         MyKey stringKey = new MyKey(TimeUnit.SECONDS.toString());

         System.out.printf("Enum key '%s' belongs to segment: %d\n", enumKey, keyPartitioner.getSegment(enumKey));
         System.out.printf("String key '%s' belongs to segment: %d\n", stringKey, keyPartitioner.getSegment(stringKey));

         System.out.println("\nPress ENTER to continue after having th second node running");
         System.in.read();

         String uuid = UUID.randomUUID().toString();
         cache.put(enumKey, uuid);
         cache.put(stringKey, uuid);

         System.out.println("\nPress ENTER to read the keys inserted");
         System.in.read();

         System.out.printf("Enum %s=%s\n", enumKey, cache.get(enumKey));
         System.out.printf("String %s=%s\n", stringKey, cache.get(stringKey));

         System.out.println("\nPress ENTER to exit!");
         System.in.read();
      }

      System.exit(0);
   }

   private static KeyPartitioner keyPartitioner(Cache<MyKey, String> cache) {
      ComponentRegistry cr = cache.getAdvancedCache().getComponentRegistry();
      return cr.getComponent(KeyPartitioner.class);
   }

   private static Cache<MyKey, String> cache(DefaultCacheManager cacheManager) {
      return cacheManager.administration()
            .withFlags(CacheContainerAdmin.AdminFlag.VOLATILE)
            .getOrCreateCache("test", cacheConfiguration());
   }

   private static GlobalConfiguration defaultGlobalConfiguration() {
      //by default, IP multicast is used to find the nodes.
      GlobalConfigurationBuilder builder = new GlobalConfigurationBuilder().clusteredDefault();
      //use jboss-marshaller to use java.io.Serializable interface
      builder.serialization().marshaller(new JBossUserMarshaller());
      builder.serialization().whiteList().addClass(MyKey.class.getName());
      return builder.build();
   }

   private static Configuration cacheConfiguration() {
      ConfigurationBuilder builder = new ConfigurationBuilder();
      builder.clustering().cacheMode(CacheMode.REPL_SYNC);
      return builder.build();
   }

}
