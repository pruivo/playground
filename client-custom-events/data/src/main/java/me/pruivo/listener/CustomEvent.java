package me.pruivo.listener;


import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

public class CustomEvent {

   private final String key;
   private final String personName;


   @ProtoFactory
   public CustomEvent(String key, String personName) {
      this.key = key;
      this.personName = personName;
   }

   @ProtoField(1)
   public String getKey() {
      return key;
   }

   @ProtoField(2)
   public String getPersonName() {
      return personName;
   }
}
