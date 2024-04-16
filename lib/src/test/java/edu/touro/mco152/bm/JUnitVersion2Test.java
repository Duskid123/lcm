package edu.touro.mco152.bm;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class JUnitVersion2Test {
    static UIHandler junit;
    static DiskWorker c;

    @BeforeAll
    public static void setup(){
        junit = spy(JUnitVersion2.class);

        c = new DiskWorker(junit);

        junit.setCallable(c);

    }

    /**
     * I believe that this shows Existence that the code has ran which makes the laststatus as true.
     * I believe that this also shows that things have to happen in order.
     */
    @Test
    void start() {
        junit.start();
        assertTrue(c.lastStatus);
    }

    /**
     * This is a unit test that shows the boundaries of the handle progress should only be called between 0-100.
     * I believe that it tests <b>range</b> from Correct.
     * This is testing the handleProgress method.
     */
    @ParameterizedTest
    @ValueSource(ints = {0, 99, 27 ,100})
    void CheckHandleProgress(int ints) {
//        assertThrows()
        junit.start();
        verify(junit, atLeastOnce()).handleProgress(ints);
    }



    /**
     * This tests the efficiency of the benchmarking method So it is <b>Performance</b> for the bicep.
     * I will be breaking this test by adding a sleep for 5 seconds to break the performance test.
     * I also Believe that this tests the Time for Correct.
     */
    @Test
    void Time(){
        long timeTaken = System.currentTimeMillis();
        junit.start();
        timeTaken = System.currentTimeMillis() - timeTaken;
        assertTrue(timeTaken > 0);
        assertTrue(timeTaken < 2000);
    }

    /**
     * This is to test that it is in the correct borders of the handle progress method as it shows that there is an exception
     * thrown if the value is less than 0 or greater than 100. I believe that this shows throwing an error from Bicep.
     *
     *
     */
    @Test
    void handleProgress() {
        assertThrows(IllegalArgumentException.class,() -> junit.handleProgress(101));
        assertThrows(IllegalArgumentException.class,() -> junit.handleProgress(-11));
    }
}