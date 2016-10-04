package task6_7;

/**
 * This is a class to control a nuclear sub and rule the oceans
 */
public class NuclearSub {
    private final NuclearReactor engine = new NuclearReactor();
    private int depth;

    /**
     * This is the nuclear sub's main reactor
     */
    public class NuclearReactor {
        private boolean activated;

        private NuclearReactor() {
            activated = false;
        }

        private void start() {
            if (activated) return;
            activated = true;
            System.out.println("Engine starting...");
        }

        private void stop() {
            if (!activated) return;
            activated = false;
            System.out.println("Engine stopping...");
        }

        private boolean isRunning() {
            return activated;
        }
    }

    /**
     * Checks whether the engine is running
     *
     * @return True if running
     */
    public boolean isEngineRunning() {
        return engine.isRunning();
    }

    /**
     * Makes sub to go down 10 meters
     *
     * @throws SubCannotDoThat when cannot comply (engine off)
     */
    public void descend() throws SubCannotDoThat {
        if (!isEngineRunning()) throw new SubCannotDoThat("Engine not running!");
        System.out.println("Diving...");
        depth += 10;
    }

    /**
     * Makes sub to go up 10 meters
     *
     * @throws SubCannotDoThat when cannot comply (engine off or already on the surface)
     */
    public void ascend() throws SubCannotDoThat {
        if (!isEngineRunning()) throw new SubCannotDoThat("Engine not running!");
        if (depth == 0) throw new SubCannotDoThat("Already on surface!");
        System.out.println("Going up...");
        depth -= 10;
    }

    /**
     * Returns the depth underwater in meters
     *
     * @return current depth
     */
    public int readDepth() {
        return depth;
    }

    public void launch() throws SubCannotDoThat {
        engine.start();
    }
}
