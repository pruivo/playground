package me.pruivo;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import org.jgroups.util.DefaultSocketFactory;
import org.jgroups.util.Util;

/**
 * TODO!
 */
public class ServerRunnable implements Runnable {

   private final CyclicBarrier barrier;

   public ServerRunnable(CyclicBarrier barrier) {
      this.barrier = barrier;
   }

   @Override
   public void run() {
      try {
         barrier.await();
         try (ServerSocket socket = Util.createServerSocket(new DefaultSocketFactory(), "test", InetAddress.getLocalHost(), 7000, 7030, 0)) {
            System.out.println("[" + Thread.currentThread().getName() + "] Socket created at " + socket.getLocalSocketAddress());
         }
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
      } catch (Exception e) {
         System.out.println("[" + Thread.currentThread().getName() + "] failed to create socket!");
         e.printStackTrace();
      } finally {
         try {
            barrier.await();
         } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
         } catch (BrokenBarrierException e) {
            e.printStackTrace();
         }
      }
   }
}
