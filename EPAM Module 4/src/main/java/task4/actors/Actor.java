package task4.actors;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;
import java.util.function.Predicate;

/**
 * Actor info to be stored in movie database
 */
public class Actor implements Serializable {
    private final static double DATABASE_VERSION = 1.0;             // Written/read as the first object in the database
    static final long serialVersionUID = 123456789L;

    private static List<Actor> actorDatabase = new LinkedList<>();

    private Actor(String name, boolean isMale, int birth, long id) {
        this.name = name.intern();
        this.male = isMale;
        this.birth = birth;
        this.id = id;
    }

    private final String name;
    private final boolean male;
    private final int birth;
    private long id;                      // id is NOT used in equals() & hashCode()

    public long getId() {
        return id;
    }

    public boolean isMale() {
        return male;
    }

    @Override
    public String toString() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        return name + "(" + (male ? "M" : "F") + "), age " + (year - birth);
    }

    @Override
    public boolean equals(Object a) {
        if (this == a) return true;
        if (a.getClass() != this.getClass()) return false;

        Actor actor = (Actor) a;

        return male == actor.male && birth == actor.birth && name.equals(actor.name);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (male ? 1 : 0);
        result = 53 * result + birth;
        return result;
    }

    /**
     * Registers new actor in the database. If such actor exists, returns reference to existing instance.
     * Otherwise, returns reference to newly added object. Each object is guaranteed unique id.
     *
     * @param name   Actor's name.
     * @param isMale Actor's gender.
     * @param birth  Actor's year of birth
     * @return Reference to new Actor object
     */
    public static Actor registerActor(String name, boolean isMale, int birth) {
        Actor newA = new Actor(name, isMale, birth, 0);
        return actorDatabase.parallelStream().filter(Predicate.isEqual(newA)).findFirst().orElse(addAndGet(newA));
    }

    private static Actor addAndGet(Actor newA) {
        actorDatabase.forEach(actor -> {
            if (actor.id >= newA.id) newA.id = actor.id + 1;
        });   // Generate unique id, but return the same object
        actorDatabase.add(newA);
        return newA;
    }

    /**
     * Ensures Actor is in the database. Returns reference to the equivalent object from the database.
     * @return Reference to interned Actor object
     */
/*
    Actor intern() {
        Actor internA = actorDatabase.parallelStream().filter(Predicate.isEqual(this)).findFirst().orElse(this);
        if (internA == this) return addAndGet(this);                    // Actor not found, adding
        return internA;
    }
*/

    /**
     * Stores database into provided stream. Database is stored as a single object, so do not use this to for append operations!
     *
     * @param oos Stream to store database to
     * @throws IOException
     */
    public static void saveDatabaseToStream(ObjectOutputStream oos) throws IOException {
        oos.writeDouble(DATABASE_VERSION);
        oos.writeInt(actorDatabase.size());
        for (Actor a : actorDatabase) oos.writeObject(a);            // Cannot use lambda because of exceptions
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

        actorDatabase.clear();
        try {
            for (int i = 0; i < numObjects; i++) {
                Actor a = (Actor) ois.readObject();
                actorDatabase.add(a);
            }
        } catch (ClassNotFoundException e) {
            throw new IOException("Database corrupt");
        }
    }

    /**
     * Makes a list of all actors of given gender sorting them alphabetically.
     *
     * @param male Gender selection
     * @return List of actors
     */
    public static List<Actor> getActorsByGender(boolean male) {
        List<Actor> aList = new ArrayList<>();

        actorDatabase.parallelStream().filter(actor -> actor.male == male).forEach(aList::add);
        Collections.sort(aList, (a1, a2) -> a1.name.compareToIgnoreCase(a2.name));
        return Collections.unmodifiableList(aList);
    }

    /**
     * Makes a list of all actors sorting them alphabetically.
     *
     * @return List of actors
     */
    public static List<Actor> getAllActors() {
        List<Actor> aList = new ArrayList<>();

        actorDatabase.parallelStream().forEach(aList::add);
        Collections.sort(aList, (a1, a2) -> a1.name.compareToIgnoreCase(a2.name));
        return Collections.unmodifiableList(aList);
    }


    static @Nullable Actor findById(long id) {
        return actorDatabase.parallelStream().filter(actor -> actor.id == id).findFirst().orElse(null);
    }
}
