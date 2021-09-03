package me.pruivo.serialization;

import org.infinispan.protostream.SerializationContextInitializer;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;

import me.pruivo.data.Person;
import me.pruivo.listener.CustomEvent;

@AutoProtoSchemaBuilder(
      includeClasses = {
            Person.class,
            CustomEvent.class
      },
      schemaFileName = "custom-events",
      schemaFilePath = "proto/generated",
      schemaPackageName = "me.pruivo.data"
)
public interface DataContextInitializer extends SerializationContextInitializer {
}
