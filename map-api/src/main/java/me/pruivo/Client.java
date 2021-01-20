package me.pruivo;

import java.util.Collection;

import me.pruivo.data.Order;
import me.pruivo.data.User;

/**
 * @author Pedro Ruivo
 * @since 1.0
 */
public interface Client {

   /**
    * @return The client's name: JDK implementation or Infinispan functional API.
    */
   String getClientName();

   /**
    * Creates a user and returns its id.
    *
    * @param name The user's name.
    * @return The user's id.
    */
   int createUser(String name);

   /**
    * @return All users.
    */
   Collection<User> listUsers();

   /**
    * Creates an {@link Order} and assigns to a user.
    *
    * @param userId      The user's id.
    * @param description The order's description,
    * @return The order's id or {@code null} if the user does not exist.
    */
   String createOrder(int userId, String description);

   /**
    * @return The {@link User} instance associated with {@code userId}. It returns {@code null} if the user does not
    * exist.
    */
   User getUser(int userId);

   /**
    * Updates an {@link Order}'s status.
    *
    * @param userId  The user's id.
    * @param orderId The order's id.
    * @param status  The new status.
    * @return A {@link Response}.
    */
   Response updateOrder(int userId, String orderId, String status);

   /**
    * Removes an {@link Order}.
    *
    * @param userId  The user's id.
    * @param orderId The order's id.
    * @return A {@link Response}.
    */
   Response removeOrder(int userId, String orderId);

   /**
    * @return The number of Infinispan instances running in clustered mode.
    */
   int getNumberOfNodes();

   /**
    * Uses node {@code nodeId} to invoke the cache operations.
    *
    * @param nodeId The node's id.
    */
   void switchNode(int nodeId);

   /**
    * @return The current node's name,
    */
   String getCurrentNode();

   enum Response {
      USER_NOT_FOUND,
      ORDER_NOT_FOUND,
      SUCCESS
   }
}
