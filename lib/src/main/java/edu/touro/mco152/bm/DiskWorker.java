package edu.touro.mco152.bm;

import Observer.ObserveFinishedRun;
import edu.touro.mco152.bm.Commands.Command;
import edu.touro.mco152.bm.Commands.ReadCommand;
import edu.touro.mco152.bm.Commands.WriteCommand;
import edu.touro.mco152.bm.Invoker.Invoker;
import edu.touro.mco152.bm.Invoker.Parameters;
import edu.touro.mco152.bm.persist.DiskRun;
import edu.touro.mco152.bm.ui.Gui;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import static edu.touro.mco152.bm.App.*;

/**
 * Run the disk benchmarking exclusively as a Swing-compliant thread (only one of these threads can run at
 * once.) Must cooperate with Swing to provide and make use of interim and final progress and
 * information, which is also recorded as needed to the persistence store, and log.
 * <p>
 * Depends on static values that describe the benchmark to be done having been set in App and Gui classes.
 * The DiskRun class is used to keep track of and persist info about each benchmark at a higher level (a run),
 * while the DiskMark class described each iteration's result, which is displayed by the UI as the benchmark run
 * progresses.
 * <p>
 * This class only knows how to do 'read' or 'write' disk benchmarks, all of which is done in doInBackground(). It is instantiated by the
 * startBenchmark() method.
 * <p>
 * To be Swing compliant this class extends SwingWorker and is dependant on it. It declares that its final return (when
 * doInBackground() is finished) is of type Boolean, and declares that intermediate results are communicated to
 * Swing using an instance of the DiskMark class.
 */

public class DiskWorker implements Callable<Boolean>{

    // Record any success or failure status returned from SwingWorker (might be us or super)
    Boolean lastStatus = null;  // so far unknown

    UIHandler inputsForBenchmark = null;

    private boolean isCancelled = false;

    Command currentCommand;
    private final Queue<ObserveFinishedRun> observers = new LinkedList<>();

    public DiskWorker(UIHandler inputsForBenchmark){
        this.inputsForBenchmark = inputsForBenchmark;
    }



    public void cancel(){
        isCancelled = true;
        Invoker.cancel();
    }

    /**
     * this method adds a new observer to be notified when there is a new finished run.
     * @param observeFinishedRun
     */
    public void addObserver(ObserveFinishedRun observeFinishedRun){
        observers.add(observeFinishedRun);
    }


    @Override
    public Boolean call() throws Exception {
        this.isCancelled = false;

        /*
          We 'got here' because: 1: End-user clicked 'Start' on the benchmark UI,
          which triggered the start-benchmark event associated with the App::startBenchmark()
          method.  2: startBenchmark() then instantiated a DiskWorker, and called
          its (super class's) execute() method, causing Swing to eventually
          call this doInBackground() method.
         */
        Logger.getLogger(App.class.getName()).log(Level.INFO, "*** New worker thread started ***");
        msg("Running readTest " + App.readTest + "   writeTest " + App.writeTest);
        msg("num files: " + App.numOfMarks + ", num blks: " + App.numOfBlocks
                + ", blk size (kb): " + App.blockSizeKb + ", blockSequence: " + App.blockSequence);

        inputsForBenchmark.init();  // init chart legend info


        int startFileNum = App.nextMarkNumber;

        if (App.autoReset) {
            App.resetTestData();
            Gui.resetTestData();
        }
        int totalUnits = 0;
        if(writeTest){
            totalUnits += App.numOfMarks*App.numOfBlocks;
        }
        if(readTest){
            totalUnits += App.numOfMarks*App.numOfBlocks;
        }

        // set up the parameters for the command.
        Parameters params = new Parameters(multiFile, inputsForBenchmark, startFileNum,
                numOfMarks, numOfBlocks, blockSizeKb, totalUnits, App.blockSequence);

        /*
          The GUI allows a Write, Read, or both types of BMs to be started. They are done serially.
         */
        if (App.writeTest && !isCancelled) {
            // this sets the type of command to be executed.
            currentCommand = new WriteCommand();

            DiskRun run = Invoker.execute(currentCommand, params);
            // If the run is null that means that the command was cancelled, then it should return false.
            // otherwise it should notify all the observers that there is a finished run.
            if(run == null){
                lastStatus = false;
                return false;
            }else{
                for(ObserveFinishedRun observeFinishedRun: observers){
                    observeFinishedRun.observeRun(run);
                }
            }
        }

        if (App.readTest && App.writeTest && !isCancelled) {

            inputsForBenchmark.showMessage("""
                            For valid READ measurements please clear the disk cache by
                            using the included RAMMap.exe or flushmem.exe utilities.
                            Removable drives can be disconnected and reconnected.
                            For system drives use the WRITE and READ operations\s
                            independantly by doing a cold reboot after the WRITE""", "Clear Disk Cache now", -1);
        }

        // Same as above, just for Read operations instead of Writes.
        if (App.readTest && !isCancelled) {
            // this sets the type of command to be executed.
            currentCommand = new ReadCommand();
            DiskRun diskRun = Invoker.execute(currentCommand, params);

            // If the run is null that means that the command was cancelled, then it should return false.
            if(diskRun == null){
                lastStatus = false;
                return false;
            }else {
                for(ObserveFinishedRun observer: observers){
                    observer.observeRun(diskRun);
                }
            }
        }
        App.nextMarkNumber += App.numOfMarks;
        lastStatus = true;
        return true;
    }



    public Boolean getLastStatus() {
        return lastStatus;
    }
}
