package me.pruivo.function;

import java.util.function.BiFunction;

import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

import me.pruivo.data.User;

/**
 * @author Pedro Ruivo
 * @since 1.0
 */
public class UpdateOrderFunction implements BiFunction<Integer, User, User> {

   private final String orderId;
   private final String status;

   @ProtoFactory
   public UpdateOrderFunction(String orderId, String status) {
      this.orderId = orderId;
      this.status = status;
   }

   @Override
   public User apply(Integer integer, User user) {
      return user.updateOrder(orderId, status);
   }

   @ProtoField(1)
   String getOrderId() {
      return orderId;
   }

   @ProtoField(2)
   String getStatus() {
      return status;
   }
}
