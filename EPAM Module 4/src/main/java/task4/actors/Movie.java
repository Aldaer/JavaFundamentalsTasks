package task4.actors;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Movie info to be stored in the database. Movies do not support equals() and are NOT checked for duplicate entries
 */
public class Movie implements Serializable {
    private final static Double DATABASE_VERSION = 1.0;             // Written/read as the first object in the database
    static final long serialVersionUID = 987654321L;

    private static List<Movie> movieDatabase = new LinkedList<>();

    public final String name;
    public final int year;
    private @Nullable Long maleLead;                                // bind to Actors by id
    private @Nullable Long femLead;

    /**
     * Creates a new movie and adds it to movie database automatically
     *
     * @param name Name of the movie
     * @param year Year of filming
     */
    public Movie(String name, int year) {
        this.name = name;
        this.year = year;
        movieDatabase.add(this);
    }

    private String getLeadActor(boolean isMale) {
        Long actorId = isMale ? maleLead : femLead;
        if (actorId == null) return "NONE";

        Actor lead = Actor.findById(actorId);
        return lead == null ? "INVALID ENTRY" : lead.toString();
    }

    /**
     * Assigns actor found by this id as male or female lead (depending on gender).
     *
     * @param id Actor's id
     */
    public void setLeadActor(long id) {
        Actor a = Actor.findById(id);
        if (a == null) return;
        if (a.isMale())
            maleLead = id;
        else
            femLead = id;
    }

    @Override
    public String toString() {
        return "== \"" + name + "\"\n" +
                "== " + "Filmed in: " + year + "\n" +
                "== Leading actors: " + getLeadActor(true) + ", " + getLeadActor(false);
    }

    /**
     * Returns an unmodifiable view to the movie database
     *
     * @return List of all movies
     */
    public static List<Movie> getAllMovies() {
        Collections.sort(movieDatabase, (m1, m2) -> m1.name.compareToIgnoreCase(m2.name));
        return Collections.unmodifiableList(movieDatabase);
    }

    /**
     * Stores database into provided stream. Database is stored as a single object, so do not use this to for append operations!
     *
     * @param oos Stream to store database to
     * @throws IOException
     */
    public static void saveDatabaseToStream(ObjectOutputStream oos) throws IOException {
        oos.writeDouble(DATABASE_VERSION);
        oos.writeInt(movieDatabase.size());
        for (Movie m : movieDatabase) oos.writeObject(m);            // Cannot use lambda because of exceptions
    }

    /**
     * Loads database from provided stream. Database is stored as a single object, so do not use this to for append operations!
     *
     * @param ois Stream to load database from
     * @throws IOException
     */
    public static void loadDatabaseFromStream(ObjectInputStream ois) throws IOException {
        double providedVer = ois.readDouble();
        if (providedVer != DATABASE_VERSION)
            throw new IOException(String.format("Expected database version %.3f, found %.3f", DATABASE_VERSION, providedVer));
        int numObjects = ois.readInt();

        movieDatabase.clear();
        try {
            for (int i = 0; i < numObjects; i++) {
                Movie m = (Movie) ois.readObject();
                movieDatabase.add(m);
            }
        } catch (ClassNotFoundException e) {
            throw new IOException("Database corrupt");
        }
    }
}
