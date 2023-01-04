package me.pruivo;

import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.Configuration;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.configuration.TransactionMode;
import org.infinispan.client.hotrod.impl.ConfigurationProperties;
import org.infinispan.client.hotrod.transaction.lookup.RemoteTransactionManagerLookup;

/**
 * Hello world!
 */
public class App {

   public static final String TX_CACHE_NAME = "tx";
   public static final String MM_CACHE_NAME = "mm";
   public static final String MM_DUP_CACHE_NAME = "mm_dup";
   public static final String COUNTER_NAME = "cnt";
   private static final String TX_CACHE_CONFIG =
         "<distributed-cache name=\"%s\">\n"
               + "    <encoding media-type=\"application/x-protostream\"/>\n"
               + "    <transaction mode=\"NON_XA\"/>\n"
               + "</distributed-cache>";
   private static final String MULTIMAP_CACHE_CONFIG =
         "<distributed-cache name=\"%s\">\n"
               + "    <encoding media-type=\"application/x-protostream\"/>\n"
               + "</distributed-cache>";

   public static void main(String[] args) {
      try (RemoteCacheManager rcm = new RemoteCacheManager(config())) {
         Action a = Action.findAction(args);
         System.out.println("Executing action: " + a);
         a.executeWith(rcm);
      }
   }

   private static Configuration config() {
      ConfigurationBuilder builder = new ConfigurationBuilder();
      builder.addServer().host("127.0.0.1").port(ConfigurationProperties.DEFAULT_HOTROD_PORT);
      builder.security().authentication()
            .username("admin")
            .password("admin");
      // TODO workaround!!
      //builder.version(ProtocolVersion.PROTOCOL_VERSION_31);
      builder.remoteCache(TX_CACHE_NAME)
            .configuration(String.format(TX_CACHE_CONFIG, TX_CACHE_NAME))
            .transactionMode(TransactionMode.NON_XA)
            .transactionManagerLookup(RemoteTransactionManagerLookup.getInstance());
      builder.remoteCache(MM_CACHE_NAME)
            .configuration(String.format(MULTIMAP_CACHE_CONFIG, MM_CACHE_NAME));
      builder.remoteCache(MM_DUP_CACHE_NAME)
            .configuration(String.format(MULTIMAP_CACHE_CONFIG, MM_DUP_CACHE_NAME));
      return builder.build();
   }
}
