package me.pruivo;

import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import java.util.UUID;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.RemoteCounterManagerFactory;
import org.infinispan.client.hotrod.multimap.MultimapCacheManager;
import org.infinispan.client.hotrod.multimap.RemoteMultimapCache;
import org.infinispan.client.hotrod.multimap.RemoteMultimapCacheManagerFactory;
import org.infinispan.counter.api.CounterConfiguration;
import org.infinispan.counter.api.CounterManager;
import org.infinispan.counter.api.CounterType;
import org.infinispan.counter.api.StrongCounter;

import static me.pruivo.App.COUNTER_NAME;
import static me.pruivo.App.MM_CACHE_NAME;
import static me.pruivo.App.MM_DUP_CACHE_NAME;
import static me.pruivo.App.TX_CACHE_NAME;

public enum Action {
   COUNTER("cnt") {
      @Override
      void executeWith(RemoteCacheManager rcm) {
         CounterManager counterManager = RemoteCounterManagerFactory.asCounterManager(rcm);
         CounterConfiguration configuration = counterManager.getConfiguration(COUNTER_NAME);
         if (configuration == null) {
            configuration = CounterConfiguration.builder(CounterType.BOUNDED_STRONG).build();
            counterManager.defineCounter(COUNTER_NAME, configuration);
         }
         StrongCounter strongCounter = counterManager.getStrongCounter(COUNTER_NAME);
         System.out.println(strongCounter.getName() + "=" + strongCounter.getValue().join());
         strongCounter.addAndGet(10).join();
         System.out.println(strongCounter.getName() + "=" + strongCounter.getValue().join());
      }
   },
   TRANSACTION("tx") {
      @Override
      void executeWith(RemoteCacheManager rcm) {
         RemoteCache<String, String> cache = rcm.getCache(TX_CACHE_NAME);
         TransactionManager tm = cache.getTransactionManager();
         try {
            tm.begin();
            cache.put("k", UUID.randomUUID().toString());
            tm.commit();
         } catch (Exception e) {
            try {
               tm.rollback();
            } catch (SystemException ex) {
               e.addSuppressed(ex);
            }
            e.printStackTrace();
         }
         System.out.println("Key value's is " + cache.get("k"));
      }
   },
   MULTIMAP("mm") {
      @Override
      void executeWith(RemoteCacheManager rcm) {
         MultimapCacheManager<String, String> multimapCacheManager = RemoteMultimapCacheManagerFactory.from(rcm);

         RemoteMultimapCache<String, String> people = multimapCacheManager.get(MM_CACHE_NAME);

         people.put("coders", "Will");
         people.put("coders", "Auri");
         people.put("coders", "Pedro");

         System.out.println(people.get("coders").join());
      }
   },
   MULTIMAP_WITH_DUPS("mmdups") {
      @Override
      void executeWith(RemoteCacheManager rcm) {
         MultimapCacheManager<String, String> multimapCacheManager = RemoteMultimapCacheManagerFactory.from(rcm);

         RemoteMultimapCache<String, String> people = multimapCacheManager.get(MM_DUP_CACHE_NAME, true);

         people.put("coders", "Will");
         people.put("coders", "Auri");
         people.put("coders", "Pedro");
         people.put("coders", "Pedro");

         System.out.println(people.get("coders").join());
      }
   },
   LISTENERS("lst") {
      @Override
      void executeWith(RemoteCacheManager rcm) {
         RemoteCache<String, String> cache = rcm.getCache(TX_CACHE_NAME);
         cache.addClientListener(new EventPrintListener());
         cache.put("e1", "e1");
         cache.put("e2", "e2");
         cache.put("e1", "e3");
         cache.remove("e2");
      }
   };

   private final String argCheck;

   Action(String argCheck) {
      this.argCheck = argCheck;
   }

   abstract void executeWith(RemoteCacheManager rcm);

   public static Action findAction(String[] args) {
      if (args.length == 0) {
         return COUNTER;
      }
      String arg = args[0];
      for (Action a : values()) {
         if (a.argCheck.equalsIgnoreCase(arg)) {
            return a;
         }
      }
      throw new IllegalArgumentException("Unable to find action matching \"" + arg + "\".");
   }
}
