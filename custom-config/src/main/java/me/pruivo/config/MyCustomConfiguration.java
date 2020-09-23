package me.pruivo.config;

import org.infinispan.commons.configuration.AbstractTypedPropertiesConfiguration;
import org.infinispan.commons.configuration.BuiltBy;
import org.infinispan.commons.configuration.attributes.AttributeSet;
import org.infinispan.configuration.serializing.SerializedWith;

/**
 * The custom configuration object
 *
 * @author Pedro Ruivo
 * @since 1.0
 */
@SerializedWith(MyCustomConfigurationSerializer.class)
@BuiltBy(MyCustomConfigurationBuilder.class)
public class MyCustomConfiguration extends AbstractTypedPropertiesConfiguration {

   MyCustomConfiguration(AttributeSet attributes) {
      super(attributes);
   }

   AttributeSet attributes() {
      return attributes;
   }

}
