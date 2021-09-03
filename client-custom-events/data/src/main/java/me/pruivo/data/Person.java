package me.pruivo.data;

import java.util.Objects;

import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

public final class Person {

   private final String firstName;
   private final String lastName;
   private final int age;
   private final String phoneNumber;

   @ProtoFactory
   public Person(String firstName, String lastName, int age, String phoneNumber) {
      this.firstName = firstName;
      this.lastName = lastName;
      this.age = age;
      this.phoneNumber = phoneNumber;
   }

   @ProtoField(1)
   public String getFirstName() {
      return firstName;
   }

   @ProtoField(2)
   public String getLastName() {
      return lastName;
   }

   @ProtoField(value = 3, defaultValue = "-1")
   public int getAge() {
      return age;
   }

   @ProtoField(4)
   public String getPhoneNumber() {
      return phoneNumber;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Person person = (Person) o;
      return age == person.age && firstName.equals(person.firstName) && lastName.equals(person.lastName) && phoneNumber.equals(person.phoneNumber);
   }

   @Override
   public int hashCode() {
      return Objects.hash(firstName, lastName, age, phoneNumber);
   }

   @Override
   public String toString() {
      return "Person{" +
            "firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", age=" + age +
            ", phoneNumber='" + phoneNumber + '\'' +
            '}';
   }
}
