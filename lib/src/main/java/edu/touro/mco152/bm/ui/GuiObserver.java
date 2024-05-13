package edu.touro.mco152.bm.ui;

import Observer.ObserveFinishedRun;
import edu.touro.mco152.bm.persist.DiskRun;

public class GuiObserver implements ObserveFinishedRun {
    @Override
    public void observeRun(DiskRun diskRun) {
        Gui.runPanel.addRun(diskRun);
    }
}
