import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

 
/*

 * Math Workout Tracker

 *

 * A simple Java workout tracker for students who like math.

 *

 * Features:

 * 1. Record workouts

 * 2. Calculate workout volume = sets * reps * weight

 * 3. Display workout history

 * 4. Fit a LINEAR curve (y = mx + b) to workout volume

 * 5. Predict future workout volume

 *

 * The curve fitting uses least-squares linear regression.

 *

 * No external libraries are needed.

 */

 

public class MathWorkoutTracker {

 

    // Stores one workout

    static class Workout {

        int workoutNumber;

        String exercise;

        int sets;

        int reps;

        double weight;
 

        Workout(int workoutNumber, String exercise, int sets, int reps, double weight) {

            this.workoutNumber = workoutNumber;

            this.exercise = exercise;

            this.sets = sets;

            this.reps = reps;

            this.weight = weight;

        }

 

        double getVolume() {

            return sets * reps * weight;

        }

    }

 

    static ArrayList<Workout> workouts = new ArrayList<>();

    static Scanner scanner = new Scanner(System.in);

 

    public static void main(String[] args) {

 

        boolean running = true;

 

        System.out.println("================================");

        System.out.println("       MATH WORKOUT TRACKER");

        System.out.println("================================");

 

        while (running) {

            printMenu();

 

            int choice = getInt("Choose an option: ");

 

            switch (choice) {

                case 1:

                    addWorkout();

                    break;

 

                case 2:

                    showWorkouts();

                    break;

 

                case 3:

                    showStatistics();

                    break;

 

                case 4:

                    curveFit();

                    break;

 

                case 5:

                    exportWorkouts();

                    break;

                case 6:

                    running = false;

                    System.out.println("Goodbye! Keep training and keep calculating.");

                    break;

 

                default:

                    System.out.println("Please choose a number from 1-6.");

            }

        }

 

        scanner.close();

    }

 

    static void printMenu() {

        System.out.println();

        System.out.println("1. Add a workout");

        System.out.println("2. View workout history");

        System.out.println("3. View statistics");

        System.out.println("4. Fit a curve and make a prediction");

        System.out.println("5. Export workouts to CSV");

        System.out.println("6. Exit");

        System.out.println();

    }

 

    static void addWorkout() {

        System.out.println("\n--- ADD WORKOUT ---");

 

        String exercise = getString("Exercise name: ");

        int sets = getInt("Number of sets: ");

        int reps = getInt("Reps per set: ");

        double weight = getDouble("Weight used: ");

 

        int workoutNumber = workouts.size() + 1;

 

        Workout workout =

                new Workout(workoutNumber, exercise, sets, reps, weight);

 

        workouts.add(workout);

 

        System.out.println("\nWorkout saved!");

        System.out.println("Volume = sets x reps x weight");

        System.out.println(

                "Volume = " + sets + " x " + reps + " x " + weight

                        + " = " + workout.getVolume()

        );

    }

 

    static void showWorkouts() {

        System.out.println("\n--- WORKOUT HISTORY ---");

 

        if (workouts.size() == 0) {

            System.out.println("No workouts recorded yet.");

            return;

        }

 

        for (Workout w : workouts) {

            System.out.printf(

                    "#%d  %-15s Sets: %d  Reps: %d  Weight: %.1f  Volume: %.1f%n",

                    w.workoutNumber,

                    w.exercise,

                    w.sets,

                    w.reps,

                    w.weight,

                    w.getVolume()

            );

        }

    }

 

    static void showStatistics() {

        System.out.println("\n--- STATISTICS ---");

 

        if (workouts.size() == 0) {

            System.out.println("Add some workouts first.");

            return;

        }

 

        double totalVolume = 0;

        double highestVolume = workouts.get(workouts.size() - 1).getVolume();

        double lowestVolume = workouts.get(0).getVolume();

 

        for (Workout w : workouts) {

            double volume = w.getVolume();

 

            totalVolume += volume;

 

            if (volume > highestVolume) {

                highestVolume = volume;

            }

 

            if (volume < lowestVolume) {

                lowestVolume = volume;

            }

        }

 

        double average = totalVolume / workouts.size();

 

        System.out.println("Number of workouts: " + workouts.size());

        System.out.printf("Total volume: %.1f%n", totalVolume);

        System.out.printf("Average volume: %.1f%n", average);

        System.out.printf("Highest volume: %.1f%n", highestVolume);

        System.out.printf("Lowest volume: %.1f%n", lowestVolume);

    }

 

    /*

     * Performs least-squares linear regression.

     *

     * We are fitting:

     *

     *       y = mx + b

     *

     * where:

     * x = workout number

     * y = workout volume

     *

     * The formulas are:

     *

     * m = [nΣxy - (Σx)(Σy)] / [nΣx² - (Σx)²]

     *

     * b = [Σy - mΣx] / n

     */

    static void curveFit() {

        System.out.println("\n--- CURVE FITTING ---");

 

        if (workouts.size() < 2) {

            System.out.println(

                    "You need at least 2 workouts to fit a curve."

            );

            return;

        }

 

        double sumX = 0;

        double sumY = 0;

        double sumXY = 0;

        double sumX2 = 0;

 

        int n = workouts.size();

 

        for (Workout w : workouts) {

            double x = w.workoutNumber;

            double y = w.getVolume();

 

            sumX += x;

            sumY += y;

            sumXY += x * y;

            sumX2 += x * x;

        }

 

        double denominator = n * sumX2 - sumX * sumX;

 

        if (denominator == 0) {

            System.out.println("There is not enough variation in the data.");

            return;

        }

 

        double m =

                (n * sumXY - sumX * sumY)

                        / denominator;

 

        double b =

                (sumY - m * sumX)

                        / n;

 

        System.out.println();

        System.out.println("Your best-fit line is:");

 

        System.out.printf("y = %.3fx + %.3f%n", m, b);

 

        System.out.println();

        System.out.println("Where:");

        System.out.println("x = workout number");

        System.out.println("y = workout volume");

        System.out.printf("m = %.3f (slope)%n", m);

        System.out.printf("b = %.3f (y-intercept)%n", b);

 

        if (m > 0) {

            System.out.println(

                    "Your trend is upward. Your workout volume is generally increasing."

            );

        } else if (m < 0) {

            System.out.println(

                    "Your trend is downward. Your workout volume is generally decreasing."

            );

        } else {

            System.out.println(

                    "Your trend is approximately flat."

            );

        }

 

        System.out.println();

        int futureWorkout =

                getInt("Predict the volume for which future workout number? ");

 

        double prediction =

                m * futureWorkout + b;

 

        System.out.printf(

                "Predicted volume for workout #%d: %.1f%n",

                futureWorkout,

                prediction

        );

 

        System.out.println();

        System.out.println("THE MATH");

        System.out.println("--------------------------------");

        System.out.println("m = [nΣxy - (Σx)(Σy)]");

        System.out.println("    -------------------------");

        System.out.println("    [nΣx² - (Σx)²]");

        System.out.println();

        System.out.println("b = [Σy - mΣx] / n");

        System.out.println();

        System.out.printf("n   = %d%n", n);

        System.out.printf("Σx  = %.3f%n", sumX);

        System.out.printf("Σy  = %.3f%n", sumY);

        System.out.printf("Σxy = %.3f%n", sumXY);

        System.out.printf("Σx² = %.3f%n", sumX2);

    }

 

    static String getString(String prompt) {

        System.out.print(prompt);

        return scanner.nextLine();

    }

 

    static int getInt(String prompt) {

        while (true) {

            try {

                System.out.print(prompt);

                return Integer.parseInt(scanner.nextLine());

            } catch (NumberFormatException e) {

                System.out.println("Please enter a whole number.");

            }

        }

    }

 

    static double getDouble(String prompt) {

        while (true) {

            try {

                System.out.print(prompt);

                return Double.parseDouble(scanner.nextLine());

            } catch (NumberFormatException e) {

                System.out.println("Please enter a number.");

            }

        }

    }

        static void exportWorkouts() {
        if (workouts.size() == 0) {
            System.out.println("No workouts to export.");
            return;
        }

        String fileName = "workouts.csv";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {

            // CSV header
            writer.write("Workout Number,Volume");
            writer.newLine();

            // Write each workout
            for (Workout w : workouts) {
                writer.write(
                        w.workoutNumber + "," +
                        w.getVolume()
                );

                writer.newLine();
            }

            System.out.println("Successfully exported workouts to " + fileName);

        } catch (IOException e) {
            System.out.println("Error writing workouts to CSV: " + e.getMessage());
        }
    }

}

