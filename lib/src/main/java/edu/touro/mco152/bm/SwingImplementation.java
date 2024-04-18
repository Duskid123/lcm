package edu.touro.mco152.bm;

import edu.touro.mco152.bm.persist.DiskRun;
import edu.touro.mco152.bm.ui.Gui;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

import static edu.touro.mco152.bm.App.dataDir;
import static edu.touro.mco152.bm.App.msg;

public class SwingImplementation extends SwingWorker<Boolean, DiskMark> implements UIHandler {

    Boolean lastStatus = null;
    Callable callable;

    public void setCallable(Callable callable){
        this.callable = callable;
    }



    @Override
    public void handleMark(DiskMark Mark) {
        publish(Mark);
        App.updateMetrics(Mark);
    }

    @Override
    public void handleProgress(int progress) {
        setProgress(progress);
    }

    @Override
    public void handleMessage(String message) {
        msg(message);
    }

    @Override
    public void showMessage(String message, String title, int type) {
        JOptionPane.showMessageDialog(Gui.mainFrame, message, "Unable to READ", type);
    }

    @Override
    public void updateUser(String update) {
        msg("disk info: (" + update + ")");

        Gui.chartPanel.getChart().getTitle().setVisible(true);
        Gui.chartPanel.getChart().getTitle().setText(update);
    }

    @Override
    public void addRun(DiskRun run) {
        Gui.runPanel.addRun(run);
    }

    @Override
    protected Boolean doInBackground() throws Exception {
        callable.call();
        return lastStatus;
    }

    /**
     * Called when doInBackGround method of SwingWorker successfully or unsuccessfully finishes or is aborted.
     * This method is called by Swing and has access to the get method within it's scope, which returns the computed
     * result of the doInBackground method.
     */
    @Override
    public void done() {
        try {
            lastStatus = super.get();   // record for future access
        } catch (Exception e) {
            Logger.getLogger(App.class.getName()).warning("Problem obtaining final status: " + e.getMessage());
        }

        if (App.autoRemoveData) {
            Util.deleteDirectory(dataDir);
        }
        App.state = App.State.IDLE_STATE;
        Gui.mainFrame.adjustSensitivity();
    }



    @Override
    public void updateUserInterface(String message) {

    }

    /**
     * Process a list of 'chunks' that have been processed, ie that our thread has previously
     * published to Swing. For my info, watch Professor Cohen's video -
     * Module_6_RefactorBadBM Swing_DiskWorker_Tutorial.mp4
     * @param markList a list of DiskMark objects reflecting some completed benchmarks
     */
    @Override
    protected void process(List<DiskMark> markList) {
        markList.stream().forEach((dm) -> {
            if (dm.type == DiskMark.MarkType.WRITE) {
                Gui.addWriteMark(dm);
            } else {
                Gui.addReadMark(dm);
            }
        });
    }

    @Override
    public void setProperties() {
        this.addPropertyChangeListener((final PropertyChangeEvent event) -> {
            switch (event.getPropertyName()) {
                case "progress":
                    int value = (Integer) event.getNewValue();
                    Gui.progressBar.setValue(value);
                    long kbProcessed = (value) * App.targetTxSizeKb() / 100;
                    Gui.progressBar.setString(kbProcessed + " / " + App.targetTxSizeKb());
                    break;
                case "state":
                    switch ((StateValue) event.getNewValue()) {
                        case STARTED:
                            Gui.progressBar.setString("0 / " + App.targetTxSizeKb());
                            break;
                        case DONE:
                            break;
                    } // end inner switch
                    break;
            }
    });
    }

    @Override
    public void init() {
        Gui.updateLegend();
    }

    @Override
    public void start() {
        setProperties();
        this.execute();
    }

    @Override
    public void cancel() {
        this.cancel(true);

    }

    @Override
    public void showRun(DiskRun run) {
        Gui.runPanel.addRun(run);
    }
}
