package me.pruivo;

import java.util.concurrent.CompletionStage;

import org.infinispan.distribution.ch.KeyPartitioner;
import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryModified;
import org.infinispan.notifications.cachelistener.event.CacheEntryCreatedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryModifiedEvent;

/**
 * Listens to keys created and modified
 *
 * @author Pedro Ruivo
 * @since 1.0
 */
@Listener(sync = false)
public class MyListener {

   private final KeyPartitioner partitioner;

   @CacheEntryCreated
   public CompletionStage<Void> keyCreated(CacheEntryCreatedEvent<MyKey, String> event) {
      if (!event.isPre()) {
         System.out.printf("Created: key=%s, value=%s, segment=%s\n", event.getKey(), event.getValue(), partitioner.getSegment(event.getKey()));
      }
      return null;
   }

   @CacheEntryModified
   public CompletionStage<Void> keyModified(CacheEntryModifiedEvent<MyKey, String> event) {
      if (!event.isPre()) {
         System.out.printf("Modified: key=%s, value=%s, segment=%s\n", event.getKey(), event.getValue(), partitioner.getSegment(event.getKey()));
      }
      return null;
   }

   public MyListener(KeyPartitioner partitioner) {
      this.partitioner = partitioner;
   }
}
