/*
 * Copyright (c) 2005, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package me.pruivo;

import io.reactivex.rxjava3.core.Flowable;
import org.infinispan.Cache;
import org.infinispan.client.hotrod.DefaultTemplate;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.commons.util.CloseableIterator;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.factories.ComponentRegistry;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.persistence.manager.PersistenceManager;
import org.infinispan.persistence.remote.configuration.ExhaustedAction;
import org.infinispan.persistence.remote.configuration.RemoteStoreConfigurationBuilder;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5, time = 2)
@Measurement(iterations = 10, time = 2)
@Fork(1)
public class MyBenchmark {

    private static final String CACHE_NAME = "test-iteration";


    @Benchmark
    public void testEmbeddedCacheForEach(Shared shared, Blackhole blackhole) {
        shared.getCache().forEach((s, v) -> {
            blackhole.consume(s);
            blackhole.consume(v);
        });
    }

    @Benchmark
    public void testReactive(Shared shared, Blackhole blackhole) {
        Flowable.fromPublisher(shared.getPersistenceManager().publishEntries(true, false))
                .blockingSubscribe(blackhole::consume);
    }

    @Benchmark
    public void testRemoteIterator(Shared shared, Blackhole blackhole) {
        try (CloseableIterator<Map.Entry<Object, Object>> it = shared.getRemoteCache().retrieveEntries(null, 100)) {
            while (it.hasNext()) {
                blackhole.consume(it.next());
            }
        }
    }

    @State(Scope.Benchmark)
    public static class Shared {

        @Param({"1000"})
        public int valueSize;

        @Param({"100000"})
        public int nrKeys;

        private DefaultCacheManager cacheManager;
        private RemoteCacheManager remoteCacheManager;
        private Cache<String, byte[]> cache;
        private RemoteCache<String, byte[]> remoteCache;
        private PersistenceManager persistenceManager;

        @Setup
        public synchronized void setup() {
            createRemoteCacheManager();
            populateRemoteCache();
            createEmbeddedCacheManager();
        }

        private void createRemoteCacheManager() {
            var builder = new org.infinispan.client.hotrod.configuration.ConfigurationBuilder();
            builder.addServers("127.0.0.1:11222")
                    .security().authentication()
                    .username("admin")
                    .password("admin");
            builder.remoteCache(CACHE_NAME).templateName(DefaultTemplate.DIST_SYNC);
            remoteCacheManager = new RemoteCacheManager(builder.build());
            remoteCache = remoteCacheManager.getCache(CACHE_NAME);
        }

        private void populateRemoteCache() {
            remoteCache.clear();
            var value = new byte[valueSize];
            ThreadLocalRandom.current().nextBytes(value);
            for (int i = 0; i < nrKeys; ++i) {
                remoteCache.put(UUID.randomUUID().toString(), value);
            }
        }

        private void createEmbeddedCacheManager() {
            cacheManager = new DefaultCacheManager(new GlobalConfigurationBuilder().clusteredDefault().build());
            var builder = new ConfigurationBuilder();
            builder.clustering().cacheMode(CacheMode.DIST_SYNC).hash().numSegments(2);
            builder.persistence().addStore(RemoteStoreConfigurationBuilder.class)
                    .rawValues(true)
                    .shared(true)
                    .segmented(false)
                    .remoteCacheName(CACHE_NAME)
                    .connectionPool()
                    .maxActive(16)
                    .exhaustedAction(ExhaustedAction.CREATE_NEW)
                    .addServer()
                    .host("127.0.0.1")
                    .port(11222)
                    .remoteSecurity().authentication()
                    .enable()
                    .username("admin")
                    .password("admin");
            cache = cacheManager.createCache(CACHE_NAME, builder.build());
            persistenceManager = ComponentRegistry.componentOf(cache, PersistenceManager.class);
        }

        @TearDown
        public synchronized void tearDown() {
            cacheManager.stop();
            remoteCacheManager.stop();
        }

        public Cache<String, byte[]> getCache() {
            return cache;
        }

        public PersistenceManager getPersistenceManager() {
            return persistenceManager;
        }

        public RemoteCache<String, byte[]> getRemoteCache() {
            return remoteCache;
        }
    }

}
