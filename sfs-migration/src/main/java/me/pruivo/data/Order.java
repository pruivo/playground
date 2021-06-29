package me.pruivo.data;

import java.util.Objects;

import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

/**
 * @author Pedro Ruivo
 * @since 1.0
 */
public class Order {

   private final String orderId;
   private final String description;
   private final String status;

   public Order(String orderId, String description) {
      this(orderId, description, "NEW");
   }

   private Order(String orderId, String description, String status) {
      this.orderId = orderId;
      this.description = description;
      this.status = status;
   }

   @ProtoFactory
   static Order createOrder(String orderId, String description, String status) {
      return new Order(orderId, description, status);
   }

   @ProtoField(1)
   public String getOrderId() {
      return orderId;
   }

   @ProtoField(2)
   public String getDescription() {
      return description;
   }

   @ProtoField(3)
   public String getStatus() {
      return status;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Order order = (Order) o;
      return Objects.equals(orderId, order.orderId);
   }

   @Override
   public int hashCode() {
      return Objects.hash(orderId);
   }

   @Override
   public String toString() {
      return "Order{" +
            "orderId='" + orderId + '\'' +
            ", description='" + description + '\'' +
            ", status='" + status + '\'' +
            '}';
   }

   public static Order newOrder(int id) {
      return new Order("order_" + id, "Order description for order_" + id);
   }

   public Order update(String status) {
      return new Order(this.orderId, this.description, status);
   }
}
