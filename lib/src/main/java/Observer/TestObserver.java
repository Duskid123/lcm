package Observer;

import edu.touro.mco152.bm.persist.DiskRun;


/**
 * Made this implementation to show that the observer function works as intended.
 */
public class TestObserver implements ObserveFinishedRun {
    private boolean finishedReadRun = false, finishedWriteRun = false, anyFinishedRun = false;

    @Override
    public void observeRun(DiskRun diskRun) {
        anyFinishedRun = true;
        if(diskRun.getIoMode() == DiskRun.IOMode.READ){
            finishedReadRun = true;
        }else if (diskRun.getIoMode() == DiskRun.IOMode.WRITE){
            finishedWriteRun = true;
        }
    }

    public boolean isFinishedReadRun(){
        return finishedReadRun;
    }
    public boolean isFinishedWriteRun(){return finishedWriteRun;}
    public boolean isAnyFinishedRun(){return anyFinishedRun;}
}
