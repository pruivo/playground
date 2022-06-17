package me.pruivo;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

/**
 * Hello world!
 */
public class App {

   private static final int N_THREADS = 8;

   public static void main(String[] args) {
      ExecutorService executorService = Executors.newFixedThreadPool(N_THREADS);
      try {
         CyclicBarrier barrier = new CyclicBarrier(N_THREADS + 1);
         IntStream.range(0, N_THREADS).forEach(value -> executorService.submit(new ServerRunnable(barrier)));
         // start all threads
         barrier.await();
         // wait until all finished
         barrier.await();
      } catch (BrokenBarrierException e) {
         e.printStackTrace();
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
      } finally {
         executorService.shutdown();
      }

   }

}
