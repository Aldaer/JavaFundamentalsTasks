package task6_7;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class NuclearSubTest {

    @Test
    public void launchNuclearSub() throws Exception {
        NuclearSub k19 = new NuclearSub();

        k19.launch();
        k19.descend();
        k19.descend();
        assertTrue(k19.readDepth() == 20);

    }

    @Test(expected = SubCannotDoThat.class)
    public void cannotDiveWithEngineNotRunning() throws Exception {
        NuclearSub U178 = new NuclearSub();
        U178.descend();
    }
}
