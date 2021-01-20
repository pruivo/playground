package me.pruivo.function;

import java.util.Optional;
import java.util.function.Function;

import org.infinispan.functional.EntryView;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

import me.pruivo.Client;
import me.pruivo.data.User;

/**
 * @author Pedro Ruivo
 * @since 1.0
 */
public class UpdateOrderInfinispanFunction implements Function<EntryView.ReadWriteEntryView<Integer, User>, Client.Response> {

   private final String orderId;
   private final String status;

   @ProtoFactory
   public UpdateOrderInfinispanFunction(String orderId, String status) {
      this.orderId = orderId;
      this.status = status;
   }

   @Override
   public Client.Response apply(EntryView.ReadWriteEntryView<Integer, User> entry) {
      Optional<User> user = entry.find();
      if (user.isPresent()) {
         User newUser = user.get().updateOrder(orderId, status);
         if (newUser == user.get()) {
            return Client.Response.ORDER_NOT_FOUND;
         }
         entry.set(newUser);
         return Client.Response.SUCCESS;
      } else {
         return Client.Response.USER_NOT_FOUND;
      }
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
