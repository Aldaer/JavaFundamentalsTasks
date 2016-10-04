package task4;

import task4.actors.Actor;
import task4.actors.Movie;

import java.io.*;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User interface for actor and movie databases
 */
public class MDB {
    private static String ask4String(String hint, String regexMatch) {
        Matcher m = Pattern.compile(regexMatch).matcher("");
        while (true) {
            try {
                System.out.print(hint);
                Scanner console = new Scanner(System.in);
                String s = console.nextLine();
                m.reset(s);
                if (m.matches()) return s;
            } catch (Exception e) {
            }
        }
    }

    private static int ask4Int(String hint, int max) {
        while (true) {
            try {
                System.out.print(hint);
                Scanner console = new Scanner(System.in);
                int i = console.nextInt();
                if ((i >= 0) && (i <= max)) return i;
            } catch (Exception e) {
            }
        }
    }

    private static void loadDatabases() {
        System.out.println("Loading databases...");

        ResourceBundle config = ResourceBundle.getBundle("task4/config");
        String actorDatabaseName = config.getString("actordatabase");
        String movieDatabaseName = config.getString("moviedatabase");

        try (ObjectInputStream actorStream = new ObjectInputStream(new FileInputStream(actorDatabaseName))) {
            Actor.loadDatabaseFromStream(actorStream);
        } catch (FileNotFoundException f) {
            System.out.println("Actor database " + actorDatabaseName + " not found");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (ObjectInputStream movieStream = new ObjectInputStream(new FileInputStream(movieDatabaseName))) {
            Movie.loadDatabaseFromStream(movieStream);
        } catch (FileNotFoundException f) {
            System.out.println("Movie database " + actorDatabaseName + " not found");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveDatabases() {
        System.out.println("Saving databases...");

        ResourceBundle config = ResourceBundle.getBundle("task4/config");
        String actorDatabaseName = config.getString("actordatabase");
        String movieDatabaseName = config.getString("moviedatabase");

        try (ObjectOutputStream actorStream = new ObjectOutputStream(new FileOutputStream(actorDatabaseName))) {
            Actor.saveDatabaseToStream(actorStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (ObjectOutputStream movieStream = new ObjectOutputStream(new FileOutputStream(movieDatabaseName))) {
            Movie.saveDatabaseToStream(movieStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static long createNewActor(boolean isMale) {
        String name = ask4String("-- Enter " + (isMale ? "actor's" : "actress'") + " name: ", ".+");
        int birthYear = ask4Int("-- Enter year of birth: ", 3000);
        return Actor.registerActor(name, isMale, birthYear).getId();
    }

    private static Long selectOrCreateActor(String hint, boolean isMale) {
        System.out.println(" 0 - skip\n 1 - create new");
        List<Actor> aList = Actor.getActorsByGender(isMale);
        for (int i = 0; i < aList.size(); i++) System.out.printf("%2d -- %s\n", i + 2, aList.get(i).toString());
        int a = ask4Int(hint, aList.size() + 1);

        if (a == 0) return null;
        return (a == 1) ? createNewActor(isMale) : aList.get(a - 2).getId();
    }

    private static void createNewMovie() {
        String name = ask4String("Enter movie name: ", ".+");
        int filmYear = ask4Int("Enter year of filming: ", 3000);
        Movie mv = new Movie(name, filmYear);

        System.out.println("----- Male lead -----");
        Long id = selectOrCreateActor("Select option: ", true);
        if (id != null) mv.setLeadActor(id);

        System.out.println("----- Female lead -----");
        id = selectOrCreateActor("Select option: ", false);
        if (id != null) mv.setLeadActor(id);
    }


    public static void main(String[] args) {
        loadDatabases();

        int n = 1;
        while (true) {
            System.out.println("\n 0 - exit\n 1 - list actors\n 2 - create new movie");
            List<Movie> mList = Movie.getAllMovies();
            for (int i = 0; i < mList.size(); i++) System.out.printf("%2d - %s\n", i + 3, mList.get(i).name);
            n = ask4Int("Select option: ", mList.size() + 2);
            System.out.println();
            if (n == 0) break;
            else if (n == 1) Actor.getAllActors().forEach(System.out::println);
            else if (n == 2) createNewMovie();
            else System.out.println(mList.get(n - 3).toString());
        }

        saveDatabases();
    }
}
