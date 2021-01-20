package me.pruivo.io;

import java.io.Console;
import java.util.Collection;

import me.pruivo.Client;
import me.pruivo.data.Order;
import me.pruivo.data.User;

/**
 * @author Pedro Ruivo
 * @since 1.0
 */
public enum ConsoleAction {

   EXIT("Exit") {
      @Override
      boolean execute(Client client, Console console) {
         return true;
      }
   },
   SWITCH_NODE("Switch to other Infinispan node") {
      @Override
      boolean execute(Client client, Console console) {
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
      boolean execute(Client client, Console console) {
         String name = console.readLine("Name=").trim();
         int id = client.createUser(name);
         console.printf("User created with id %s%n", id);
         return false;
      }
   },
   LIST_USERS("List users") {
      @Override
      boolean execute(Client client, Console console) {
         Collection<User> users = client.listUsers();
         for (User user : users) {
            console.printf("- %s (id=%d)%n", user.getName(), user.getId());
         }
         return false;
      }
   },
   CREATE_ORDER("Create order") {
      @Override
      boolean execute(Client client, Console console) {
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
      boolean execute(Client client, Console console) {
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
      boolean execute(Client client, Console console) {
         String userId = console.readLine("User id=").trim();
         String orderId = console.readLine("Order id=").trim();
         do {
            try {
               Client.Response rsp = client.updateOrder(Integer.parseInt(userId), orderId, "IN_PROGRESS");
               switch (rsp) {
                  case SUCCESS:
                     console.printf("Order updated!.%n");
                     break;
                  case USER_NOT_FOUND:
                     ConsoleAction.userNotFound(console, userId);
                     break;
                  case ORDER_NOT_FOUND:
                     ConsoleAction.orderNotFound(console, orderId);
                     break;
               }
               return false;
            } catch (NumberFormatException e) {
               System.out.printf("Unable to parse user id %s%n", userId);
            }
         } while (true);
      }
   },
   UPDATE_ORDER_2("Update order to COMPLETE") {
      @Override
      boolean execute(Client client, Console console) {
         String userId = console.readLine("User id=").trim();
         String orderId = console.readLine("Order id=").trim();
         do {
            try {
               Client.Response rsp = client.updateOrder(Integer.parseInt(userId), orderId, "COMPLETE");
               switch (rsp) {
                  case SUCCESS:
                     console.printf("Order updated!.%n");
                     break;
                  case USER_NOT_FOUND:
                     ConsoleAction.userNotFound(console, userId);
                     break;
                  case ORDER_NOT_FOUND:
                     ConsoleAction.orderNotFound(console, orderId);
                     break;
               }
               return false;
            } catch (NumberFormatException e) {
               System.out.printf("Unable to parse user id %s%n", userId);
            }
         } while (true);
      }
   },
   REMOVE_ORDER("Remove order") {
      @Override
      boolean execute(Client client, Console console) {
         String userId = console.readLine("User id=").trim();
         String orderId = console.readLine("Order id=").trim();
         do {
            try {
               Client.Response rsp = client.removeOrder(Integer.parseInt(userId), orderId);
               switch (rsp) {
                  case SUCCESS:
                     console.printf("Order removed.%n");
                     break;
                  case USER_NOT_FOUND:
                     ConsoleAction.userNotFound(console, userId);
                     break;
                  case ORDER_NOT_FOUND:
                     ConsoleAction.orderNotFound(console, orderId);
                     break;
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

   abstract boolean execute(Client client, Console console);

   private static void userNotFound(Console console, String userId) {
      console.printf("User id %s does not exist%n", userId);
   }

   private static void orderNotFound(Console console, String orderId) {
      console.printf("Order id %s does not exist%n", orderId);
   }

}
