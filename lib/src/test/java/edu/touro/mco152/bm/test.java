package edu.touro.mco152.bm;

import org.junit.jupiter.api.Test;

import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class test {

    @Test
    void start() {

        UIHandler junit = spy(JUnitVersion.class);

        Callable c = new DiskWorker(junit);

        junit.setCallable(c);

        junit.start();

        assertTrue(junit.lastStatus());
    }

    @Test
    void CheckHandleProgress() {
        UIHandler junit = spy(JUnitVersion.class);

        Callable c = new DiskWorker(junit);

        junit.setCallable(c);

        junit.start();

        verify(junit, atLeastOnce()).start();

        assertTrue(junit.lastStatus());

        // shows that the handle progress method will be called from 0-100 percent.
        for(int i=0; i<=100; i++){
            verify(junit, atLeastOnce()).handleProgress(i);
        }
        verify(junit, never()).handleProgress(-1);
        verify(junit, never()).handleProgress(101);

    }
}