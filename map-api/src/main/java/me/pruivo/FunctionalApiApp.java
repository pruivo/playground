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
import org.infinispan.functional.FunctionalMap;
import org.infinispan.functional.impl.FunctionalMapImpl;
import org.infinispan.functional.impl.ReadWriteMapImpl;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.protostream.annotations.ProtoField;

import me.pruivo.data.DataInitializerImpl;
import me.pruivo.data.Order;
import me.pruivo.data.User;
import me.pruivo.function.AddOrderFunction;
import me.pruivo.function.AddOrderInfinispanFunction;
import me.pruivo.function.FunctionInitializerImpl;
import me.pruivo.function.RemoveOrderFunction;
import me.pruivo.function.UpdateOrderFunction;
import me.pruivo.function.UpdateOrderInfinispanFunction;
import me.pruivo.io.ConsoleRunner;

/**
 * Hello world!
 */
public class FunctionalApiApp implements Client {

   private static final int NUMBER_NODES = 2;
   private static final String CACHE_NAME = "users";
   private static final AtomicInteger USER_ID_GEN = new AtomicInteger(0);

   private final List<DefaultCacheManager> nodes;
   private int currentNode;

   public FunctionalApiApp(List<DefaultCacheManager> nodes) {
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

      FunctionalApiApp app = new FunctionalApiApp(infinispanNodes);
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
      return "Infinispan Functional API Client";
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
      return readWriteMap().eval(userId, new AddOrderInfinispanFunction(order))
            .thenApply(added -> added ? order.getOrderId() : null)
            .join();
   }

   @Override
   public Response updateOrder(int userId, String orderId, String status) {
      return readWriteMap().eval(userId, new UpdateOrderInfinispanFunction(orderId, status))
            .join();
   }

   @ProtoField
   public Response removeOrder(int userId, String orderId) {
      User user =  cache().computeIfPresent(userId, new RemoveOrderFunction(orderId));
      return user == null ? Response.USER_NOT_FOUND : Response.SUCCESS;
   }

   private Cache<Integer, User> cache() {
      return nodes.get(currentNode).getCache(CACHE_NAME);
   }

   private FunctionalMapImpl<Integer, User> functionalMap() {
      return FunctionalMapImpl.create(cache().getAdvancedCache());
   }

   private FunctionalMap.ReadWriteMap<Integer, User> readWriteMap() {
      return ReadWriteMapImpl.create(functionalMap());
   }
}
