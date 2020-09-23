package me.pruivo.config;

import static org.infinispan.commons.configuration.AbstractTypedPropertiesConfiguration.PROPERTIES;

import java.util.Properties;

import org.infinispan.commons.configuration.Builder;
import org.infinispan.commons.configuration.ConfigurationBuilderInfo;
import org.infinispan.commons.configuration.attributes.AttributeSet;
import org.infinispan.commons.util.TypedProperties;
import org.infinispan.configuration.cache.ConfigurationBuilder;

/**
 * The {@link MyCustomConfiguration} builder.
 * <p>
 * Used when configure caches programmatically and when parsing the configuration file.
 *
 * @author Pedro Ruivo
 * @since 1.0
 */
public class MyCustomConfigurationBuilder implements Builder<MyCustomConfiguration>, ConfigurationBuilderInfo {

   private final AttributeSet attributes;

   public MyCustomConfigurationBuilder(ConfigurationBuilder ignored) {
      //ignoring the ConfigurationBuilder but we could keep it and implement org.infinispan.configuration.cache.ConfigurationChildBuilder
      attributes = MyCustomConfiguration.attributeSet();
   }

   public MyCustomConfigurationBuilder withProperties(Properties properties) {
      attributes.attribute(PROPERTIES).set(TypedProperties.toTypedProperties(properties));
      return this;
   }

   @Override
   public void validate() {
      // here you can validate your properties
   }

   @Override
   public MyCustomConfiguration create() {
      //protect attributes against write
      return new MyCustomConfiguration(attributes.protect());
   }

   @Override
   public MyCustomConfigurationBuilder read(MyCustomConfiguration template) {
      // used for copying configuration, read the attributes from the template instance
      this.attributes.read(template.attributes());
      return this;
   }
}
