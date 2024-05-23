package edu.Observer;

import Observer.TestObserver;
import edu.touro.mco152.bm.App;
import edu.touro.mco152.bm.Commands.ReadCommand;
import edu.touro.mco152.bm.Commands.WriteCommand;
import edu.touro.mco152.bm.Invoker.Invoker;
import edu.touro.mco152.bm.Invoker.Parameters;
import edu.touro.mco152.bm.JUnitVersion;
import edu.touro.mco152.bm.UIHandler;
import edu.touro.mco152.bm.persist.DiskRun;
import edu.touro.mco152.bm.ui.Gui;
import edu.touro.mco152.bm.ui.MainFrame;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestObserverTest {

    static UIHandler uiHandler;
    static Parameters parameters ;
    static TestObserver observer;


    /**
     * this sets up the necessary components for the tests.
     */
    @BeforeEach
    public void setup(){
        uiHandler = new JUnitVersion();
        setupDefaultAsPerProperties();
        parameters =  new Parameters(false, uiHandler, 1, 26, 128, 2048, DiskRun.BlockSequence.SEQUENTIAL);
        observer = new TestObserver();
    }

    /**
     * This test and the next test shows that the implementation could differentiate if it is a read run or write run.
     */
    @Test
    void isFinishedReadRun() {
        assertFalse(observer.isFinishedReadRun());
        DiskRun dr = Invoker.execute(new ReadCommand(), parameters);
        observer.observeRun(dr);
        assertTrue(observer.isFinishedReadRun());
        assertFalse(observer.isFinishedWriteRun());
    }

    @Test
    void isFinishedWriteRun() {
        assertFalse(observer.isFinishedWriteRun());
        DiskRun dr = Invoker.execute(new WriteCommand(), parameters);
        observer.observeRun(dr);
        assertTrue(observer.isFinishedWriteRun());
        assertFalse(observer.isFinishedReadRun());
    }

    // this test shows that the flag will be flagged after each diskrun has finished.
    @AfterAll
    static void afterAll(){
        assertTrue(observer.isAnyFinishedRun());
    }

    private static void setupDefaultAsPerProperties()
    {
        /// Do the minimum of what  App.init() would do to allow to run.
        Gui.mainFrame = new MainFrame();
        App.p = new Properties();
        App.loadConfig();

        Gui.progressBar = Gui.mainFrame.getProgressBar(); //must be set or get Nullptr

        // configure the embedded DB in .jDiskMark
        System.setProperty("derby.system.home", App.APP_CACHE_DIR);

        // code from startBenchmark
        //4. create data dir reference

        // may be null when tests not run in original proj dir, so use a default area
        if (App.locationDir == null) {
            App.locationDir = new File(System.getProperty("user.home"));
        }

        App.dataDir = new File(App.locationDir.getAbsolutePath()+File.separator+App.DATADIRNAME);

        //5. remove existing test data if exist
        if (App.dataDir.exists()) {
            if (App.dataDir.delete()) {
                App.msg("removed existing data dir");
            } else {
                App.msg("unable to remove existing data dir");
            }
        }
        else
        {
            App.dataDir.mkdirs(); // create data dir if not already present
        }
    }
}