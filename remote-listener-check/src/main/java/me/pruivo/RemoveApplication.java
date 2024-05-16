package me.pruivo;

import org.infinispan.client.hotrod.RemoteCacheManager;

import java.util.concurrent.TimeUnit;

public class RemoveApplication {

    public static void main(String[] args) throws Exception {
        var serverList = args.length > 0 ?
                String.join(",", args) :
                "127.0.0.1:11222";

        try (var manager = new RemoteCacheManager(ConfigurationHelper.configuration(serverList))) {
            var cache = manager.getCache(ConfigurationHelper.CACHE_NAME);
            for (int i = 0; i < 10; ++i) {
                cache.remove(i);
                Thread.sleep(TimeUnit.SECONDS.toMillis(1));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
