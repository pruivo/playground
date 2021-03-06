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
import org.infinispan.protostream.annotations.ProtoField;

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
public class App implements Client {

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

   @Override
   public String getCurrentNode() {
      return nodes.get(currentNode).getNodeAddress();
   }

   @Override
   public void switchNode(int newNode) {
      if (newNode < 0 || newNode >= nodes.size()) {
         System.out.printf("Invalid node %d [0-%s]%n", newNode, nodes.size() - 1);
         return;
      }
      currentNode = newNode;
   }

   @Override
   public int getNumberOfNodes() {
      return nodes.size();
   }

   @Override
   public String getClientName() {
      return "JDK API";
   }

   public int createUser(String name) {
      int id = USER_ID_GEN.incrementAndGet();
      User u = new User(id, name);
      cache().put(id, u);
      return id;
   }

   @Override
   public User getUser(int userId) {
      return cache().get(userId);
   }

   @Override
   public Collection<User> listUsers() {
      return cache().values();
   }

   @Override
   public String createOrder(int userId, String description) {
      Order order = new Order(UUID.randomUUID().toString(), description);
      User user = cache().computeIfPresent(userId, new AddOrderFunction(order));
      return user == null ? null : order.getOrderId();
   }

   @Override
   public Response updateOrder(int userId, String orderId, String status) {
      User user = cache().computeIfPresent(userId, new UpdateOrderFunction(orderId, status));
      if (user == null) {
         return Response.USER_NOT_FOUND;
      }
      for (Order order : user.getOrders()) {
         if (orderId.equals(order.getOrderId())) {
            return Response.SUCCESS;
         }
      }
      return Response.ORDER_NOT_FOUND;
   }

   @ProtoField
   public Response removeOrder(int userId, String orderId) {
      User user =  cache().computeIfPresent(userId, new RemoveOrderFunction(orderId));
      return user == null ? Response.USER_NOT_FOUND : Response.SUCCESS;
   }

   private Cache<Integer, User> cache() {
      return nodes.get(currentNode).getCache(CACHE_NAME);
   }
}
