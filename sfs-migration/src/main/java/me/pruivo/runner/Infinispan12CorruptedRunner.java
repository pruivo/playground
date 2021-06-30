package me.pruivo.runner;

import java.util.concurrent.TimeUnit;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.infinispan.Cache;

import me.pruivo.Util;

/**
 * TODO! document this
 *
 * @author Pedro Ruivo
 * @since 12
 */
public class Infinispan12CorruptedRunner implements Runner {

   private static final Logger log = LogManager.getLogger(Infinispan12CorruptedRunner.class);

   @Override
   public void execute(Cache<Object, Object> cache) {
      String name = cache.getName();
      if (name.contains("irac") || name.contains("tx")) {
         // irac & tx cache is broken and throw a RuntimeException when read or write
         return;
      }
      IntStream.range(0, 300).forEach(i -> Util.assertEquals(Util.value(i), cache.get(Util.key(i)), log));
      IntStream.range(300, 600).forEach(i -> Util.assertEquals(Util.wrappedValue(i), cache.get(Util.wrappedKey(i)), log));

      IntConsumer put = i -> cache.put(Util.key(i), Util.value("_corrupt", i));
      IntConsumer remove = i -> cache.remove(Util.key(i));
      IntConsumer putWithLifespan = i -> cache.put(Util.key(i), Util.value("_corrupt", i), Long.MAX_VALUE, TimeUnit.DAYS);
      IntConsumer putWithMaxIdle = i -> cache.put(Util.key(i), Util.value("_corrupt", i), -1, TimeUnit.DAYS, Long.MAX_VALUE, TimeUnit.DAYS);

      Stream.of(0, 100, 200).forEach(value -> {
         IntStream.range(value, value + 10).forEach(put);
         IntStream.range(value + 10, value + 20).forEach(putWithLifespan);
         IntStream.range(value + 20, value + 30).forEach(putWithMaxIdle);
         IntStream.range(value + 30, value + 40).forEach(remove);
      });

      IntConsumer wPut = i -> cache.put(Util.wrappedKey(i), Util.wrappedValue("_corrupt", i));
      IntConsumer wRemove = i -> cache.remove(Util.wrappedKey(i));
      IntConsumer wPutWithLifespan = i -> cache.put(Util.wrappedKey(i), Util.wrappedValue("_corrupt", i), Long.MAX_VALUE, TimeUnit.DAYS);
      IntConsumer wPutWithMaxIdle = i -> cache.put(Util.wrappedKey(i), Util.wrappedValue("_corrupt", i), -1, TimeUnit.DAYS, Long.MAX_VALUE, TimeUnit.DAYS);

      Stream.of(300, 400, 500).forEach(value -> {
         IntStream.range(value, value + 10).forEach(wPut);
         IntStream.range(value + 10, value + 20).forEach(wPutWithLifespan);
         IntStream.range(value + 20, value + 30).forEach(wPutWithMaxIdle);
         IntStream.range(value + 30, value + 40).forEach(wRemove);
      });
   }

   @Override
   public void executeSingle(Cache<Object, Object> cache) {
      String name = cache.getName();
      if (name.contains("irac") || name.contains("tx")) {
         // irac & tx cache is broken and throw a RuntimeException when read or write
         return;
      }
      Util.assertEquals(Util.value(-1), cache.get(Util.key(-1)), log);
   }
}
