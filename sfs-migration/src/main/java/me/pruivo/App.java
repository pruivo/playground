package me.pruivo;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.infinispan.commons.api.CacheContainerAdmin;
import org.infinispan.commons.util.Version;
import org.infinispan.manager.DefaultCacheManager;

import me.pruivo.runner.Infinispan11Runner;
import me.pruivo.runner.Infinispan125Runner;
import me.pruivo.runner.Infinispan12CorruptedRunner;
import me.pruivo.runner.Runner;

/**
 * Hello world!
 */
public class App {

   private static final Logger log = LogManager.getLogger(App.class);
   private static final Map<String, Runner> RUNNERS;
   //cache name => templates
   private static final Map<String, String> M_CACHE_NAMES;
   private static final Map<String, String> S_CACHE_NAMES;

   static {
      RUNNERS = new HashMap<>();
      RUNNERS.put("11.0.11.Final", new Infinispan11Runner());
      RUNNERS.put("12.1.4.Final", new Infinispan12CorruptedRunner());
      RUNNERS.put("12.1.5-SNAPSHOT", new Infinispan125Runner());

      M_CACHE_NAMES = new HashMap<>();
      M_CACHE_NAMES.put("m_dist", "dist-sync");
      M_CACHE_NAMES.put("m_dist_tx", "dist-sync-opt-tx");
      M_CACHE_NAMES.put("m_dist_irac", "dist-sync-irac");
      M_CACHE_NAMES.put("m_dist_tx_irac", "dist-sync-opt-tx-irac");

      S_CACHE_NAMES = new HashMap<>();
      S_CACHE_NAMES.put("s_dist", "dist-sync");
      S_CACHE_NAMES.put("s_dist_tx", "dist-sync-opt-tx");
      S_CACHE_NAMES.put("s_dist_irac", "dist-sync-irac");
      S_CACHE_NAMES.put("s_dist_tx_irac", "dist-sync-opt-tx-irac");
   }


   public static void main(String[] args) throws Exception {
      try (DefaultCacheManager cm = new DefaultCacheManager("infinispan.xml")) {
         Runner runner = RUNNERS.get(Version.getVersion());
         if (runner == null) {
            log.error("Unable to find runner for version " + Version.getVersion());
            System.exit(1);
         }
         log.info("Define Caches");
         M_CACHE_NAMES.forEach((name, template) -> cm.administration().withFlags(CacheContainerAdmin.AdminFlag.VOLATILE).createCache(name, template));
         S_CACHE_NAMES.forEach((name, template) -> cm.administration().withFlags(CacheContainerAdmin.AdminFlag.VOLATILE).createCache(name, template));

         log.info("Running multiple entries caches");
         for (String name : M_CACHE_NAMES.keySet()) {
            log.info("Running on cache " + name);
            runner.execute(cm.getCache(name));
         }

         log.info("Running single entry caches");
         for (String name : S_CACHE_NAMES.keySet()) {
            log.info("Running on cache " + name);
            runner.executeSingle(cm.getCache(name));
         }
      }
      System.exit(0);
   }
}
