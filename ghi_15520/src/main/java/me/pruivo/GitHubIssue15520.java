package me.pruivo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

public record GitHubIssue15520(EmbeddedCacheManager cacheManager) {

    private static final Logger LOG = LogManager.getLogger(MethodHandles.lookup().lookupClass());
    private static final String INFINISPAN_CONFIGURATION = "infinispan-ghi.xml";

    public void doGets(AtomicBoolean running) {
        var cache = cacheManager.getCache("entity");
        int maxKeys = 1024;
        while (running.get()) {
            var key = ThreadLocalRandom.current().nextInt(maxKeys);
            var value = cache.get(key);
            maxKeys = value == null ? 1024 : 2048;
        }
    }

    public static void main(String[] args) throws Exception {
        LOG.info("PID: {}", ProcessHandle.current().pid());
        var running = new AtomicBoolean(true);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> running.set(false)));
        try (EmbeddedCacheManager cacheManager = new DefaultCacheManager(INFINISPAN_CONFIGURATION)) {
            var a = new GitHubIssue15520(cacheManager);
            LOG.info("Starting.");
            a.doGets(running);
            LOG.info("Stopping.");
        }
    }
}
