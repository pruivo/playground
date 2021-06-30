package me.pruivo.runner;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.infinispan.Cache;

import me.pruivo.Util;

/**
 * TODO! document this
 *
 * @author Pedro Ruivo
 * @since 12
 */
public class Infinispan11Runner implements Runner {
   @Override
   public void execute(Cache<Object, Object> cache) {
      cache.clear();
      IntStream.range(0, 100).forEach(i -> cache.put(Util.key(i), Util.value(i)));
      IntStream.range(100, 200).forEach(i -> cache.put(Util.key(i), Util.value(i), Long.MAX_VALUE, TimeUnit.DAYS));
      IntStream.range(200, 300).forEach(i -> cache.put(Util.key(i), Util.value(i), -1, TimeUnit.DAYS, Long.MAX_VALUE, TimeUnit.DAYS));

      IntStream.range(300, 400).forEach(i -> cache.put(Util.wrappedKey(i), Util.wrappedValue(i)));
      IntStream.range(400, 500).forEach(i -> cache.put(Util.wrappedKey(i), Util.wrappedValue(i), Long.MAX_VALUE, TimeUnit.DAYS));
      IntStream.range(500, 600).forEach(i -> cache.put(Util.wrappedKey(i), Util.wrappedValue(i), -1, TimeUnit.DAYS, Long.MAX_VALUE, TimeUnit.DAYS));
   }

   @Override
   public void executeSingle(Cache<Object, Object> cache) {
      cache.clear();
      cache.put(Util.key(-1), Util.value(-1));
   }
}
