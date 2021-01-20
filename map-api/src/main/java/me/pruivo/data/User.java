package me.pruivo.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

/**
 * @author Pedro Ruivo
 * @since 1.0
 */

public class User {

   private final int id;
   private final String name;
   private final List<Order> orders;

   public User(int id, String name) {
      this(id, name, Collections.emptyList());
   }

   private User(int id, String name, List<Order> orders) {
      this.id = id;
      this.name = name;
      this.orders = orders;
   }

   @ProtoFactory
   static User createUser(int id, String name, List<Order> orders) {
      return new User(id, name, orders);
   }

   @ProtoField(value = 1, required = true, defaultValue = "0")
   public int getId() {
      return id;
   }

   @ProtoField(2)
   public String getName() {
      return name;
   }

   @ProtoField(number = 3, collectionImplementation = ArrayList.class)
   public List<Order> getOrders() {
      return orders;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      User user = (User) o;
      return Objects.equals(name, user.name) && Objects.equals(orders, user.orders);
   }

   @Override
   public int hashCode() {
      return Objects.hash(name, orders);
   }

   @Override
   public String toString() {
      return "User{" +
            "name='" + name + '\'' +
            ", orders=" + orders +
            '}';
   }

   public User addOrder(Order order) {
      List<Order> newOrders = new ArrayList<>(orders.size() + 1);
      newOrders.addAll(orders);
      newOrders.add(order);
      return new User(id, name, newOrders);
   }

   public User updateOrder(String orderId, String status) {
      List<Order> newOrders = new ArrayList<>(orders.size());
      boolean found = false;
      for (Order order : orders) {
         if (orderId.equals(order.getOrderId())) {
            newOrders.add(order.update(status));
            found = true;
         } else  {
            newOrders.add(order);
         }
      }
      return found ? new User(id, name, newOrders) : this;
   }

   public User removeOrder(String orderId) {
      List<Order> newOrders = new ArrayList<>(orders.size());
      boolean found = false;
      for (Order order : orders) {
         if (orderId.equals(order.getOrderId())) {
            found = true;
         } else  {
            newOrders.add(order);
         }
      }
      return found ? new User(id, name, newOrders) : this;
   }
}
