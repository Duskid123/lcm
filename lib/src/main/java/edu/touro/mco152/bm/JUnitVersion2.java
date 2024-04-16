package edu.touro.mco152.bm;

import edu.touro.mco152.bm.persist.DiskRun;
import edu.touro.mco152.bm.ui.Gui;
import edu.touro.mco152.bm.ui.MainFrame;

import java.io.File;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * not 100% sure about the current
 */

public class JUnitVersion2 implements UIHandler {

    boolean lastStatus = false;

    FutureTask<Boolean> futureTask;
    Callable<Boolean> callable;

    @Override
    public void setCallable(Callable callable) {
        this.callable = callable;
    }


    @Override
    public void handleMark(DiskMark Mark) {

    }

    @Override
    public void handleProgress(int progress) {
        if (progress < 0 || progress > 100) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void handleMessage(String message) {

    }

    @Override
    public void showMessage(String message, String title, int type) {

    }

    @Override
    public void updateUser(String update) {

    }

    @Override
    public void addRun(DiskRun run) {

    }

    @Override
    public void updateUserInterface(String message) {

    }

    @Override
    public void setProperties() {
        setupDefaultAsPerProperties();
    }

    @Override
    public void init() {

    }

    @Override
    public void start() {
        setProperties();
        futureTask = new FutureTask<>(callable);
        Thread thread = new Thread(futureTask);
        thread.start();
        try {
            // Wait for the task to complete
            futureTask.get();
            // Update lastStatus after the task completes
            lastStatus = futureTask.isDone();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void cancel() {
        System.out.println("cancel");
        lastStatus = true;
    }

    @Override
    public void showRun(DiskRun run) {

    }

    private void setupDefaultAsPerProperties()
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
