package me.pruivo.function;

import java.util.Optional;
import java.util.function.Function;

import org.infinispan.functional.EntryView;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

import me.pruivo.data.Order;
import me.pruivo.data.User;

/**
 * @author Pedro Ruivo
 * @since 1.0
 */
public class AddOrderInfinispanFunction implements Function<EntryView.ReadWriteEntryView<Integer, User>, Boolean> {

   private final Order order;

   @ProtoFactory
   public AddOrderInfinispanFunction(Order order) {
      this.order = order;
   }

   @Override
   public Boolean apply(EntryView.ReadWriteEntryView<Integer, User> entry) {
      Optional<User> user = entry.find();
      if (user.isPresent()) {
         entry.set(user.get().addOrder(order));
         return true;
      } else {
         return false;
      }
   }


   @ProtoField(1)
   Order getOrder() {
      return order;
   }
}
