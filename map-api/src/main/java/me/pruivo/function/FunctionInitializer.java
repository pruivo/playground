package me.pruivo.function;

import org.infinispan.protostream.SerializationContextInitializer;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;

import me.pruivo.data.DataInitializer;

/**
 * @author Pedro Ruivo
 * @since 1.0
 */
@AutoProtoSchemaBuilder(
      includeClasses = {
            AddOrderFunction.class,
            RemoveOrderFunction.class,
            UpdateOrderFunction.class,
            AddOrderInfinispanFunction.class,
            RemoveOrderInfinispanFunction.class,
            UpdateOrderInfinispanFunction.class
      },
      schemaFileName = "function.proto",
      schemaFilePath = "proto/",
      schemaPackageName = "function_sample",
      dependsOn = DataInitializer.class)
public interface FunctionInitializer extends SerializationContextInitializer {
}
