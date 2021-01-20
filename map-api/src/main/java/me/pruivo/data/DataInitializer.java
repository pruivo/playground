package me.pruivo.data;

import org.infinispan.protostream.SerializationContextInitializer;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;

/**
 * @author Pedro Ruivo
 * @since 1.0
 */
@AutoProtoSchemaBuilder(
      includeClasses = {
            User.class,
            Order.class
      },
      schemaFileName = "data.proto",
      schemaFilePath = "proto/",
      schemaPackageName = "data_sample")
public interface DataInitializer extends SerializationContextInitializer {
}
