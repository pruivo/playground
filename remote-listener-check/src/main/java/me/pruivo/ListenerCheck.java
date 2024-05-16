package me.pruivo;

import org.infinispan.client.hotrod.RemoteCacheManager;

public class ListenerCheck {

    public static void main(String[] args) throws Exception {
        var serverList = args.length > 0 ?
                String.join(",", args) :
                "127.0.0.1:11222";

        try (var manager = new RemoteCacheManager(ConfigurationHelper.configuration(serverList))) {
            var cache = manager.getCache(ConfigurationHelper.CACHE_NAME);
            cache.addClientListener(new LoggingListener());
            System.out.println("Send SIGTERM to terminate");
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
