package me.pruivo.listener;

import org.infinispan.metadata.Metadata;
import org.infinispan.notifications.cachelistener.filter.CacheEventConverter;
import org.infinispan.notifications.cachelistener.filter.EventType;

import me.pruivo.data.Person;

public class PersonConverter implements CacheEventConverter<String, Person, CustomEvent> {
   @Override
   public CustomEvent convert(String key, Person oldValue, Metadata oldMetadata, Person newValue, Metadata newMetadata, EventType eventType) {
      if (newValue == null) {
         // remove
         return new CustomEvent(key, null);
      }
      return new CustomEvent(key, String.format("%s %s", newValue.getFirstName(), newValue.getLastName()));
   }
}
