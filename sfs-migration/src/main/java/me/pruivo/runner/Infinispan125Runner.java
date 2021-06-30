package me.pruivo.runner;

import java.util.function.IntConsumer;
import java.util.stream.IntStream;

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
public class Infinispan125Runner implements Runner {

   private static final Logger log = LogManager.getLogger(Infinispan125Runner.class);

   @Override
   public void execute(Cache<Object, Object> cache) {
      //make sure it is able to read
      IntConsumer get = i -> Util.assertEquals(Util.value(i), cache.get(Util.key(i)), log);
      IntConsumer corruptedGet = i -> Util.assertEquals(Util.value("_corrupted", i), cache.get(Util.key(i)), log);
      IntConsumer removed = i -> Util.assertEquals(null, cache.get(Util.key(i)), log);

      IntStream.of(0, 100, 200).forEach(value -> {
         // first 30, corrupted suffixed value
         IntStream.range(value, value + 30).forEach(corruptedGet);
         // 30-40 removed
         IntStream.range(value + 30, value + 40).forEach(removed);
         // 50-100 untouched
         IntStream.range(value + 50, value + 100).forEach(get);
      });

      IntConsumer wGet = i -> Util.assertEquals(Util.wrappedValue(i), cache.get(Util.key(i)), log);
      IntConsumer wCorruptedGet = i -> Util.assertEquals(Util.wrappedValue("_corrupted", i), cache.get(Util.key(i)), log);

      IntStream.of(300, 400, 500).forEach(value -> {
         // first 30, corrupted suffixed value
         IntStream.range(value, value + 30).forEach(wCorruptedGet);
         // 30-40 removed
         IntStream.range(value + 30, value + 40).forEach(removed);
         // 50-100 untouched
         IntStream.range(value + 50, value + 100).forEach(wGet);
      });
   }

   @Override
   public void executeSingle(Cache<Object, Object> cache) {
      Util.assertEquals(Util.value(-1), cache.get(Util.key(-1)), log);
   }
}
