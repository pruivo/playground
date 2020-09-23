package me.pruivo.config;

import static me.pruivo.config.MyCustomConfigurationParser.NAMESPACE;
import static me.pruivo.config.MyCustomConfigurationParser.ROOT_ELEMENT;

import java.util.Properties;

import javax.xml.stream.XMLStreamException;

import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.parsing.ConfigurationBuilderHolder;
import org.infinispan.configuration.parsing.ConfigurationParser;
import org.infinispan.configuration.parsing.Namespace;
import org.infinispan.configuration.parsing.ParseUtils;
import org.infinispan.configuration.parsing.Parser;
import org.infinispan.configuration.parsing.ParserScope;
import org.infinispan.configuration.parsing.XMLExtendedStreamReader;

/**
 * The XML parser.
 * <p>
 * It parses the configuration file to {@link MyCustomConfiguration}.
 *
 * @author Pedro Ruivo
 * @since 1.0
 */
@Namespace(root = ROOT_ELEMENT)
@Namespace(uri = NAMESPACE + "*", root = ROOT_ELEMENT, since = "11.0")
public class MyCustomConfigurationParser implements ConfigurationParser {

   static final String NAMESPACE = "urn:my:config:";
   static final String ROOT_ELEMENT = "custom-configuration";

   @Override
   public void readElement(XMLExtendedStreamReader reader, ConfigurationBuilderHolder holder)
         throws XMLStreamException {
      //here you can check the current scope being parser
      if (!holder.inScope(ParserScope.CACHE)) {
         //anything different from cache or template scope (<local-cache>, <replicated-cache>, etc.) is ignored
         throw new IllegalStateException();
      }

      //get the current cache element
      ConfigurationBuilder builder = holder.getCurrentConfigurationBuilder();
      MyCustomConfigurationBuilder myBuilder = builder.addModule(MyCustomConfigurationBuilder.class);

      switch (reader.getLocalName()) {
         case ROOT_ELEMENT: {
            Properties properties = Parser.parseProperties(reader);
            myBuilder.withProperties(properties);
            break;
         }
         // you can have other elements here
         default: {
            throw ParseUtils.unexpectedElement(reader);
         }
      }
   }

   @Override
   public Namespace[] getNamespaces() {
      return ParseUtils.getNamespaceAnnotations(getClass());
   }
}
