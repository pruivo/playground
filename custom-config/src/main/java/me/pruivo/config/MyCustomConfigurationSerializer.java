package me.pruivo.config;

import static me.pruivo.config.MyCustomConfigurationParser.ROOT_ELEMENT;

import javax.xml.stream.XMLStreamException;

import org.infinispan.commons.util.Version;
import org.infinispan.configuration.serializing.ConfigurationSerializer;
import org.infinispan.configuration.serializing.SerializeUtils;
import org.infinispan.configuration.serializing.XMLExtendedStreamWriter;

/**
 * The XML serializer.
 * <p>
 * Converts {@link MyCustomConfiguration} to XML.
 *
 * @author Pedro Ruivo
 * @since 1.0
 */
public class MyCustomConfigurationSerializer implements ConfigurationSerializer<MyCustomConfiguration> {
   @Override
   public void serialize(XMLExtendedStreamWriter writer, MyCustomConfiguration configuration)
         throws XMLStreamException {
      writer.writeStartElement(ROOT_ELEMENT);
      writer.writeDefaultNamespace(MyCustomConfigurationParser.NAMESPACE + Version.getMajorMinor());
      SerializeUtils.writeTypedProperties(writer, configuration.properties());
      writer.writeEndElement();
   }
}
