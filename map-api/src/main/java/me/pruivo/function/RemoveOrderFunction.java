package me.pruivo.function;

import java.util.function.BiFunction;

import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

import me.pruivo.data.User;

/**
 * @author Pedro Ruivo
 * @since 1.0
 */
public class RemoveOrderFunction implements BiFunction<Integer, User, User> {

   private final String orderId;

   @ProtoFactory
   public RemoveOrderFunction(String orderId) {
      this.orderId = orderId;
   }

   @Override
   public User apply(Integer integer, User user) {
      return user.removeOrder(orderId);
   }

   @ProtoField(1)
   String getOrderId() {
      return orderId;
   }
}
