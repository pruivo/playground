package me.pruivo;

import org.infinispan.client.hotrod.configuration.ClientIntelligence;
import org.infinispan.client.hotrod.configuration.Configuration;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;

import java.net.URI;
import java.net.URISyntaxException;

public final class ConfigurationHelper {

    public static final String CACHE_NAME = "test-cache";

    public static Configuration configuration(String serverList) throws URISyntaxException {
        System.out.println("Connection to server(s) " + serverList);
        var builder = new ConfigurationBuilder();
        builder.addServers(serverList);
        // BASIC intelligence configures the client to only connect to the servers in the list.
        builder.clientIntelligence(ClientIntelligence.BASIC);
        builder.remoteCache(CACHE_NAME).configurationURI(cacheConfiguration());
        builder.security().authentication().username("admin").password("admin");
        return builder.build();
    }

    private static URI cacheConfiguration() throws URISyntaxException {
        return ConfigurationHelper.class.getClassLoader().getResource("cache.xml").toURI();
    }

}
