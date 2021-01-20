package me.pruivo.io;

import static java.lang.System.console;

import java.io.Console;

import me.pruivo.App;

/**
 * @author Pedro Ruivo
 * @since 1.0
 */
public class ConsoleRunner implements Runnable {

   private final App app;

   public ConsoleRunner(App app) {
      this.app = app;
   }

   private static int selectOption(Console console, int nrOptions) {
      String s = console.readLine("Select option:");
      try {
         int option = Integer.parseInt(s);
         if (option < 0 || option >= nrOptions) {
            console.printf("Invalid option: %s%n", s);
            return -1;
         }
         return option;
      } catch (NumberFormatException e) {
         console.printf("Invalid option: %s%n", s);
      }
      return -1;
   }

   @Override
   public void run() {
      final Console console = console();
      final ConsoleAction[] options = ConsoleAction.values();

      boolean exit = false;
      while (!exit) {
         console.printf("===%n");
         console.printf("Using Infinispan node %s%n", app.getCurrentNode());
         for (int i = 0; i < options.length; ++i) {
            console.printf("%d: %s%n", i, options[i].description);
         }
         int option = selectOption(console, options.length);
         if (option >= 0) {
            exit = options[option].execute(app, console);
         }
      }
   }

}
