package Observer;

import edu.touro.mco152.bm.persist.DiskRun;

/**
 * This interface is used to be able to observe a finished Diskrun operation.
 * This is useful so that it could be added to a list to be notified when a disk run is completed.
 */
public interface ObserveFinishedRun {
    /**
     * this method is used to be able to add a new functionality that reacts to a finished diskRun operation.
     * @param diskRun
     */
    public void observeRun(DiskRun diskRun);
}
