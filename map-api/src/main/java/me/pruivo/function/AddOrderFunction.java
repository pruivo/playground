package me.pruivo.function;

import java.util.function.BiFunction;

import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

import me.pruivo.data.Order;
import me.pruivo.data.User;

/**
 * @author Pedro Ruivo
 * @since 1.0
 */
public class AddOrderFunction implements BiFunction<Integer, User, User> {

   private final Order order;

   @ProtoFactory
   public AddOrderFunction(Order order) {
      this.order = order;
   }

   @Override
   public User apply(Integer userId, User user) {
      return user.addOrder(order);
   }

   @ProtoField(1)
   Order getOrder() {
      return order;
   }
}
