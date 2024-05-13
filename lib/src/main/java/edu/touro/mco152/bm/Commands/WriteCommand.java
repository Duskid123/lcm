package edu.touro.mco152.bm.Commands;

import edu.touro.mco152.bm.App;
import edu.touro.mco152.bm.DiskMark;
import edu.touro.mco152.bm.Invoker.Parameters;
import edu.touro.mco152.bm.UIHandler;
import edu.touro.mco152.bm.Util;
import edu.touro.mco152.bm.persist.DiskRun;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import static edu.touro.mco152.bm.App.*;
import static edu.touro.mco152.bm.DiskMark.MarkType.WRITE;

public class WriteCommand implements Command{
    boolean isCancelled = false;

    /**
     * this enables the command to be cancelled.
     */
    @Override
    public void cancel() {
        this.isCancelled = true;
    }

    @Override
    public DiskRun execute(Parameters parameters){
        boolean isMultiple = parameters.isMultiple();
        int numOfBlocks = parameters.getNumOfBlocks(), blockSizeKb = parameters.getBlockSizeKb(), numOfMarks = parameters.getNumOfMarks(),
                startNumFile = parameters.getStartNumFile();
        UIHandler uiHandler = parameters.getUiHandler();
        DiskRun.BlockSequence blockSequence = parameters.getBlockOrder();

        DiskMark wMark;

        int wUnitsComplete = 0, unitsComplete;
        int wUnitsTotal = numOfBlocks * numOfMarks;
        float percentComplete;

        int blockSize = blockSizeKb * KILOBYTE;
        byte[] blockArr = new byte[blockSize];
        for (int b = 0; b < blockArr.length; b++) {
            if (b % 2 == 0) {
                blockArr[b] = (byte) 0xFF;
            }
        }

        DiskRun run = new DiskRun(DiskRun.IOMode.WRITE, blockSequence);
        run.setNumMarks(numOfMarks);
        run.setNumBlocks(numOfBlocks);
        run.setBlockSize(blockSizeKb);
        run.setTxSize(App.targetTxSizeKb());
        run.setDiskInfo(Util.getDiskInfo(dataDir));

        // Tell logger and GUI to display what we know so far about the Run
        uiHandler.updateUser(run.getDiskInfo());

        // Create a test data file using the default file system and config-specified location
            if (!isMultiple){
                testFile = new File(dataDir.getAbsolutePath() + File.separator + "testdata.jdm");
            }


            /*
              Begin an outer loop for specified duration (number of 'marks') of benchmark,
              that keeps writing data (in its own loop - for specified # of blocks). Each 'Mark' is timed
              and is reported to the GUI for display as each Mark completes.
             */
        for (int m = startNumFile; m <startNumFile+ numOfMarks && !isCancelled; m++) {

            if (isMultiple) {
                testFile = new File(dataDir.getAbsolutePath()
                        + File.separator + "testdata" + m + ".jdm");
            }
            wMark = new DiskMark(WRITE);    // starting to keep track of a new benchmark
            wMark.setMarkNum(m);
            long startTime = System.nanoTime();
            long totalBytesWrittenInMark = 0;

            String mode = "rw";
            if (App.writeSyncEnable) {
                mode = "rwd";
            }

            try {
                try (RandomAccessFile rAccFile = new RandomAccessFile(testFile, mode)) {
                    for (int b = 0; b < numOfBlocks; b++) {
                        if (parameters.getBlockOrder() == DiskRun.BlockSequence.RANDOM) {
                            int rLoc = Util.randInt(0, numOfBlocks - 1);
                            rAccFile.seek((long) rLoc * blockSize);
                        } else {
                            rAccFile.seek((long) b * blockSize);
                        }
                        rAccFile.write(blockArr, 0, blockSize);
                        totalBytesWrittenInMark += blockSize;
                        wUnitsComplete++;
                        unitsComplete = wUnitsComplete;
                        percentComplete = (float) unitsComplete / (float) parameters.getTotalUnits()  * 100f;

                            /*
                              Report to interface what percentage level of Entire BM (#Marks * #Blocks) is done.
                             */
                        uiHandler.handleProgress((int) percentComplete);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }

                /*
                  Compute duration, throughput of this Mark's step of BM
                 */
            long endTime = System.nanoTime();
            long elapsedTimeNs = endTime - startTime;
            double sec = (double) elapsedTimeNs / (double) 1000000000;
            double mbWritten = (double) totalBytesWrittenInMark / (double) MEGABYTE;
            wMark.setBwMbSec(mbWritten / sec);
            msg("m:" + m + " write IO is " + wMark.getBwMbSecAsString() + " MB/s     "
                    + "(" + Util.displayString(mbWritten) + "MB written in "
                    + Util.displayString(sec) + " sec)");

                /*
                  Let the interface know the interim result described by the current Mark
                 */
            uiHandler.handleMark(wMark);

            // Keep track of statistics to be displayed and persisted after all Marks are done.
            run.setRunMax(wMark.getCumMax());
            run.setRunMin(wMark.getCumMin());
            run.setRunAvg(wMark.getCumAvg());
            run.setEndTime(new Date());
        }
        // if the user canceled the run then it should return null.
        return isCancelled ? null : run;
    }
}
