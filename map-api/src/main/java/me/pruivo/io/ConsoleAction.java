package me.pruivo.io;

import java.io.Console;
import java.util.Collection;

import me.pruivo.App;
import me.pruivo.data.Order;
import me.pruivo.data.User;

/**
 * @author Pedro Ruivo
 * @since 1.0
 */
public enum ConsoleAction {

   EXIT("Exit") {
      @Override
      boolean execute(App client, Console console) {
         return true;
      }
   },
   SWITCH_NODE("Switch to other Infinispan node") {
      @Override
      boolean execute(App client, Console console) {
         do {
            String nodeS = console.readLine("Node id [0-%d]=", client.getNumberOfNodes() - 1).trim();
            try {
               client.switchNode(Integer.parseInt(nodeS));
               return false;
            } catch (NumberFormatException e) {
               System.out.printf("Uname to parse node id %s%n", nodeS);
            }
         } while (true);
      }
   },
   CREATE_USER("Create user") {
      @Override
      boolean execute(App client, Console console) {
         String name = console.readLine("Name=").trim();
         int id = client.createUser(name);
         console.printf("User created with id %s%n", id);
         return false;
      }
   },
   LIST_USERS("List users") {
      @Override
      boolean execute(App client, Console console) {
         Collection<User> users = client.listUsers();
         for (User user : users) {
            console.printf("- %s (id=%d)%n", user.getName(), user.getId());
         }
         return false;
      }
   },
   CREATE_ORDER("Create order") {
      @Override
      boolean execute(App client, Console console) {
         do {
            String userId = console.readLine("User id=").trim();
            String description = console.readLine("Order description=").trim();
            try {
               String orderId = client.createOrder(Integer.parseInt(userId), description);
               if (orderId == null) {
                  console.printf("User id %s does not exist%n", userId);
               } else {
                  console.printf("Order %s created%n", orderId);
               }
               return false;
            } catch (NumberFormatException e) {
               System.out.printf("Unable to parse user id %s%n", userId);
            }
         } while (true);
      }
   },
   LIST_ORDERS("List orders") {
      @Override
      boolean execute(App client, Console console) {
         do {
            String userId = console.readLine("User id=").trim();
            try {
               User user = client.getUser(Integer.parseInt(userId));
               if (user == null) {
                  console.printf("User id %s does not exist%n", userId);
               } else {
                  console.printf("User %s (%d orders)%n", user.getName(), user.getOrders().size());
                  for (Order order : user.getOrders()) {
                     console.printf("- %s (status=%s)%n  %s%n", order.getOrderId(), order.getStatus(), order.getDescription());
                  }
               }
               return false;
            } catch (NumberFormatException e) {
               System.out.printf("Unable to parse user id %s%n", userId);
            }
         } while (true);
      }
   },
   UPDATE_ORDER_1("Update order to IN_PROGRESS") {
      @Override
      boolean execute(App client, Console console) {
         String userId = console.readLine("User id=").trim();
         String orderId = console.readLine("Order id=").trim();
         do {
            try {
               User user = client.updateOrder(Integer.parseInt(userId), orderId, "IN_PROGRESS");
               if (user == null) {
                  console.printf("User id %s does not exist%n", userId);
                  return false;
               }
               for (Order order : user.getOrders()) {
                  if (orderId.equals(order.getOrderId())) {
                     console.printf("Order status %s%n", order.getStatus());
                     return false;
                  }
               }
               console.printf("Order %s not found%n", orderId);
               return false;
            } catch (NumberFormatException e) {
               System.out.printf("Unable to parse user id %s%n", userId);
            }
         } while (true);
      }
   },
   UPDATE_ORDER_2("Update order to COMPLETE") {
      @Override
      boolean execute(App client, Console console) {
         String userId = console.readLine("User id=").trim();
         String orderId = console.readLine("Order id=").trim();
         do {
            try {
               User user = client.updateOrder(Integer.parseInt(userId), orderId, "COMPLETE");
               if (user == null) {
                  console.printf("User id %s does not exist%n", userId);
                  return false;
               }
               for (Order order : user.getOrders()) {
                  if (orderId.equals(order.getOrderId())) {
                     console.printf("Order status %s%n", order.getStatus());
                     return false;
                  }
               }
               console.printf("Order %s not found%n", orderId);
               return false;
            } catch (NumberFormatException e) {
               System.out.printf("Unable to parse user id %s%n", userId);
            }
         } while (true);
      }
   },
   REMOVE_ORDER("Remove order") {
      @Override
      boolean execute(App client, Console console) {
         String userId = console.readLine("User id=").trim();
         String orderId = console.readLine("Order id=").trim();
         do {
            try {
               User user = client.removeOrder(Integer.parseInt(userId), orderId);
               if (user == null) {
                  console.printf("User id %s does not exist%n", userId);
               }
               return false;
            } catch (NumberFormatException e) {
               System.out.printf("Unable to parse user id %s%n", userId);
            }
         } while (true);
      }
   };

   final String description;

   ConsoleAction(String description) {
      this.description = description;
   }

   abstract boolean execute(App client, Console console);

}
