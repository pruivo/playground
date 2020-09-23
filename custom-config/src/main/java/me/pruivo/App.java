package me.pruivo;

import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

import org.infinispan.Cache;
import org.infinispan.commons.util.TypedProperties;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;

import me.pruivo.config.MyCustomConfiguration;
import me.pruivo.config.MyCustomConfigurationBuilder;

/**
 * Example how to setup custom configuration for Infinispan 11
 *
 * @author Pedro Ruivo
 * @since 1.0
 */
public class App {

   public static void main(String[] args) throws IOException {
      //first example, with programmatically configuration
      try (DefaultCacheManager cacheManager = new DefaultCacheManager(defaultGlobalConfiguration())) {
         Properties properties = new Properties();
         properties.put("a", "b");
         properties.put("c", "d");
         ConfigurationBuilder builder = new ConfigurationBuilder();
         builder.clustering().cacheMode(CacheMode.LOCAL);
         builder.addModule(MyCustomConfigurationBuilder.class).withProperties(properties);

         cacheManager.defineConfiguration("my-cache", builder.build());
         Cache<?, ?> cache = cacheManager.getCache("my-cache");

         Configuration configuration = cache.getCacheConfiguration();
         TypedProperties tProperties = configuration.module(MyCustomConfiguration.class).properties();

         assertProperties(tProperties, "a", "b");
         assertProperties(tProperties, "c", "d");
      }

      //second example with configuration file
      try (DefaultCacheManager cacheManager = new DefaultCacheManager("infinispan.xml")) {
         Cache<?, ?> cache = cacheManager.getCache("my-cache-2");

         Configuration configuration = cache.getCacheConfiguration();
         TypedProperties tProperties = configuration.module(MyCustomConfiguration.class).properties();

         assertProperties(tProperties, "custom-1", "a");
         assertProperties(tProperties, "custom-2", "b");
      }

      System.exit(0);
   }

   private static GlobalConfiguration defaultGlobalConfiguration() {
      //we don't need transport for this example
      return new GlobalConfigurationBuilder().nonClusteredDefault().build();
   }

   private static void assertProperties(Properties properties, String key, String value) {
      if (!Objects.equals(value, properties.get(key))) {
         throw new AssertionError("get(" + key + "), " + properties.getProperty(key) + "!=" + value);
      }
   }
}
