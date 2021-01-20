package me.pruivo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.infinispan.Cache;
import org.infinispan.commons.api.CacheContainerAdmin;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;

import me.pruivo.data.DataInitializerImpl;
import me.pruivo.data.Order;
import me.pruivo.data.User;
import me.pruivo.function.AddOrderFunction;
import me.pruivo.function.FunctionInitializerImpl;
import me.pruivo.function.RemoveOrderFunction;
import me.pruivo.function.UpdateOrderFunction;
import me.pruivo.io.ConsoleRunner;

/**
 * Hello world!
 */
public class App {

   private static final int NUMBER_NODES = 2;
   private static final String CACHE_NAME = "users";
   private static final AtomicInteger USER_ID_GEN = new AtomicInteger(0);

   private final List<DefaultCacheManager> nodes;
   private int currentNode;

   public App(List<DefaultCacheManager> nodes) {
      this.nodes = nodes;
      this.currentNode = 0;
   }

   public static void main(String[] args) {
      List<DefaultCacheManager> infinispanNodes = new ArrayList<>(NUMBER_NODES);

      for (int i = 0; i < NUMBER_NODES; ++i) {
         GlobalConfigurationBuilder builder = new GlobalConfigurationBuilder().clusteredDefault();
         builder.transport().nodeName("node-" + i);
         builder.serialization().addContextInitializers(new DataInitializerImpl(), new FunctionInitializerImpl());
         infinispanNodes.add(new DefaultCacheManager(builder.build()));
      }

      //define cache
      ConfigurationBuilder builder = new ConfigurationBuilder();
      builder.clustering().cacheMode(CacheMode.DIST_SYNC);
      infinispanNodes.get(0).administration()
            .withFlags(CacheContainerAdmin.AdminFlag.VOLATILE)
            .getOrCreateCache(CACHE_NAME, builder.build());

      App app = new App(infinispanNodes);
      ConsoleRunner runner = new ConsoleRunner(app);
      Thread t = new Thread(runner);
      t.start();
      try {
         t.join();
      } catch (InterruptedException e) {
         t.interrupt();
         Thread.currentThread().interrupt();
      }
      infinispanNodes.forEach(DefaultCacheManager::stop);
   }

   public String getCurrentNode() {
      return nodes.get(currentNode).getNodeAddress();
   }

   public void switchNode(int newNode) {
      if (newNode < 0 || newNode >= nodes.size()) {
         System.out.printf("Invalid node %d [0-%s]%n", newNode, nodes.size() - 1);
         return;
      }
      currentNode = newNode;
   }

   public int getNumberOfNodes() {
      return nodes.size();
   }

   public int createUser(String name) {
      int id = USER_ID_GEN.incrementAndGet();
      User u = new User(id, name);
      cache().put(id, u);
      return id;
   }

   public User getUser(int userId) {
      return cache().get(userId);
   }

   public Collection<User> listUsers() {
      return cache().values();
   }

   public String createOrder(int userId, String description) {
      Order order = new Order(UUID.randomUUID().toString(), description);
      User user = cache().computeIfPresent(userId, new AddOrderFunction(order));
      return user == null ? null : order.getOrderId();
   }

   public User updateOrder(int userId, String orderId, String status) {
      return cache().computeIfPresent(userId, new UpdateOrderFunction(orderId, status));
   }

   public User removeOrder(int userId, String orderId) {
      return cache().computeIfPresent(userId, new RemoveOrderFunction(orderId));
   }

   private Cache<Integer, User> cache() {
      return nodes.get(currentNode).getCache(CACHE_NAME);
   }
}
