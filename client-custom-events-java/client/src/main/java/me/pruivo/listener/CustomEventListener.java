package me.pruivo.listener;

import org.infinispan.client.hotrod.annotation.ClientCacheEntryCreated;
import org.infinispan.client.hotrod.annotation.ClientCacheEntryModified;
import org.infinispan.client.hotrod.annotation.ClientListener;
import org.infinispan.client.hotrod.event.ClientCacheEntryCustomEvent;

@ClientListener(converterFactoryName = "person-converter")
public class CustomEventListener {

   @ClientCacheEntryCreated
   @ClientCacheEntryModified
   public void handleCustomEvent(ClientCacheEntryCustomEvent<CustomEvent> e) {
      CustomEvent data = e.getEventData();
      System.out.printf("Person %s has ID %s%n", data.getPersonName(), data.getKey());
   }

}
