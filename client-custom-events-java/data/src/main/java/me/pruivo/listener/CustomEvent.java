package me.pruivo.listener;

import java.io.Serializable;

public class CustomEvent implements Serializable {

   private final String key;
   private final String personName;

   public CustomEvent(String key, String personName) {
      this.key = key;
      this.personName = personName;
   }

   public String getKey() {
      return key;
   }

   public String getPersonName() {
      return personName;
   }
}
