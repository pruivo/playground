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
public class RemoveOrderInfinispanFunction implements Function<EntryView.ReadWriteEntryView<Integer, User>, Client.Response> {

   private final String orderId;

   @ProtoFactory
   public RemoveOrderInfinispanFunction(String orderId) {
      this.orderId = orderId;
   }

   @Override
   public Client.Response apply(EntryView.ReadWriteEntryView<Integer, User> entry) {
      Optional<User> user = entry.find();
      if (user.isPresent()) {
         User newUser = user.get().removeOrder(orderId);
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
}
