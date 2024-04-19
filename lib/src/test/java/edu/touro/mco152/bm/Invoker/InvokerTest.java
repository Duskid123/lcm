package edu.touro.mco152.bm.Invoker;

import edu.touro.mco152.bm.App;
import edu.touro.mco152.bm.Commands.ReadCommand;
import edu.touro.mco152.bm.Commands.WriteCommand;
import edu.touro.mco152.bm.JUnitVersion;
import edu.touro.mco152.bm.UIHandler;
import edu.touro.mco152.bm.persist.DiskRun;
import edu.touro.mco152.bm.ui.Gui;
import edu.touro.mco152.bm.ui.MainFrame;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertTrue;

class InvokerTest {

    static UIHandler uiHandler;
    static Parameters parameters ;


    @BeforeAll
    public static void setup(){
        uiHandler = new JUnitVersion();

       setupDefaultAsPerProperties();

       parameters =  new Parameters(false, uiHandler, 1, 26, 128, 2048, DiskRun.BlockSequence.SEQUENTIAL);
    }

    @Test
    void execute() {
        Invoker invoker = new Invoker();
        invoker.addCommand(new WriteCommand(), new Parameters(true, uiHandler, 1, 26, 128, 2048, DiskRun.BlockSequence.SEQUENTIAL));
        invoker.addCommand(new ReadCommand(), new Parameters(true, uiHandler, 1, 26, 128, 2048, DiskRun.BlockSequence.SEQUENTIAL));
        assertTrue(invoker.execute());
    }

    @Test
    void writeTest(){
        assertTrue(Invoker.execute(new WriteCommand(), parameters));
    }

    @Test
    void readTest(){
        assertTrue(Invoker.execute(new ReadCommand(), parameters));
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