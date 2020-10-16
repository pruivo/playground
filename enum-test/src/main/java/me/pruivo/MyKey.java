package me.pruivo;

import java.io.Serializable;
import java.util.Objects;

/**
 * Key wrapper
 *
 * @author Pedro Ruivo
 * @since 1.0
 */
public class MyKey implements Serializable {

   private Serializable key;

   public MyKey(Serializable key) {
      this.key = Objects.requireNonNull(key);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      MyKey myKey = (MyKey) o;
      return Objects.equals(key, myKey.key);
   }

   @Override
   public int hashCode() {
      return Objects.hash(key);
   }

   @Override
   public String toString() {
      return "MyKey{" +
            "class=" + key.getClass() +
            ", hashCode=" + key.hashCode() +
            ", value=" + key +
            '}';
   }
}
