package me.pruivo.listener;

import org.infinispan.filter.NamedFactory;
import org.infinispan.notifications.cachelistener.filter.CacheEventConverter;
import org.infinispan.notifications.cachelistener.filter.CacheEventConverterFactory;
import org.kohsuke.MetaInfServices;

import me.pruivo.data.Person;

@NamedFactory(name = "person-converter")
@MetaInfServices(CacheEventConverterFactory.class)
public class PersonConverterFactory implements CacheEventConverterFactory {

   private final CacheEventConverter<String, Person, CustomEvent> converter = new PersonConverter();

   @Override
   public CacheEventConverter<String, Person, CustomEvent> getConverter(Object[] params) {
      return converter;
   }



}
