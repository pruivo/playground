package me.pruivo;

import org.infinispan.client.hotrod.annotation.ClientCacheEntryCreated;
import org.infinispan.client.hotrod.annotation.ClientCacheEntryModified;
import org.infinispan.client.hotrod.annotation.ClientCacheEntryRemoved;
import org.infinispan.client.hotrod.annotation.ClientListener;
import org.infinispan.client.hotrod.event.ClientCacheEntryCreatedEvent;
import org.infinispan.client.hotrod.event.ClientCacheEntryModifiedEvent;
import org.infinispan.client.hotrod.event.ClientCacheEntryRemovedEvent;

@SuppressWarnings("unused")
@ClientListener
public class LoggingListener {

    @ClientCacheEntryCreated
    public void handleCreatedEvent(ClientCacheEntryCreatedEvent<Integer> e) {
        System.out.printf("Thread %s has created an entry in the cache under key %s!\n",
                Thread.currentThread().getName(), e.getKey());
    }

    @ClientCacheEntryModified
    public void handleModifiedEvent(ClientCacheEntryModifiedEvent<Integer> e) {
        System.out.printf("Thread %s has modified an entry in the cache under key %s!\n",
                Thread.currentThread().getName(), e.getKey());
    }

    @ClientCacheEntryRemoved
    public void handleRemovedEvent(ClientCacheEntryRemovedEvent<Integer> e) {
        System.out.printf("Thread %s has removed an entry in the cache under key %s!\n",
                Thread.currentThread().getName(), e.getKey());
    }

}
