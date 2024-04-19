package edu.touro.mco152.bm.Commands;

import edu.touro.mco152.bm.App;
import edu.touro.mco152.bm.DiskMark;
import edu.touro.mco152.bm.Invoker.Parameters;
import edu.touro.mco152.bm.UIHandler;
import edu.touro.mco152.bm.Util;
import edu.touro.mco152.bm.persist.DiskRun;
import edu.touro.mco152.bm.persist.EM;
import jakarta.persistence.EntityManager;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import static edu.touro.mco152.bm.App.*;
import static edu.touro.mco152.bm.DiskMark.MarkType.READ;

/**
 * This command is used to run a single read benchmark.
 */
public class ReadCommand implements Command{
    boolean isCancelled = false;

    @Override
    public void cancel() {
        this.isCancelled = true;
    }

    /**
     * execute the command
     * @param parameters
     * @return
     */
    @Override
    public boolean execute(Parameters parameters){
        boolean isMultiple = parameters.isMultiple();
        int numOfBlocks = parameters.getNumOfBlocks(),
                blockSizeKb = parameters.getBlockSizeKb(),
                numOfMarks = parameters.getNumOfMarks(),
        startNumFile = parameters.getStartNumFile();
        UIHandler uiHandler = parameters.getUiHandler();
        DiskRun.BlockSequence blockSequence = parameters.getBlockOrder();

        isCancelled = false;

        /*
          init local vars that keep track of benchmarks, and a large read/write buffer
         */
        int rUnitsComplete = 0, unitsComplete;

        int rUnitsTotal = numOfBlocks * numOfMarks;

        float percentComplete;

        int blockSize = blockSizeKb * KILOBYTE;
        byte[] blockArr = new byte[blockSize];
        for (int b = 0; b < blockArr.length; b++) {
            if (b % 2 == 0) {
                blockArr[b] = (byte) 0xFF;
            }
        }

        DiskMark rMark;  // declare vars that will point to objects used to pass progress to UI


        DiskRun run = new DiskRun(DiskRun.IOMode.READ, parameters.getBlockOrder());
        run.setNumMarks(numOfMarks);
        run.setNumBlocks(numOfBlocks);
        run.setBlockSize(blockSizeKb);
        run.setTxSize(targetTxSizeKb());
        run.setDiskInfo(Util.getDiskInfo(dataDir));


        // Create a test data file using the default file system and config-specified location
        if(!isMultiple){
            testFile = new File(dataDir.getAbsolutePath() + File.separator + "testdata.jdm");
        }


        for (int m = startNumFile; m <startNumFile+ numOfMarks && !isCancelled; m++) {
            if (parameters.isMultiple()) {
                testFile = new File(dataDir.getAbsolutePath()
                        + File.separator + "testdata" + m + ".jdm");
            }
            rMark = new DiskMark(READ);  // starting to keep track of a new benchmark
            rMark.setMarkNum(m);
            long startTime = System.nanoTime();
            long totalBytesReadInMark = 0;


            try {
                try (RandomAccessFile rAccFile = new RandomAccessFile(testFile, "r")) {
                    for (int b = 0; b < numOfBlocks; b++) {
                        if (blockSequence == DiskRun.BlockSequence.RANDOM) {
                            int rLoc = Util.randInt(0, numOfBlocks - 1);
                            rAccFile.seek((long) rLoc * blockSize);
                        } else {
                            rAccFile.seek((long) b * blockSize);
                        }
                        rAccFile.readFully(blockArr, 0, blockSize);
                        totalBytesReadInMark += blockSize;
                        rUnitsComplete++;
                        unitsComplete = rUnitsComplete;
                        percentComplete = (float) unitsComplete / (float) parameters.getTotalUnits() * 100f;
                        uiHandler.handleProgress((int) percentComplete);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                String emsg = "May not have done Write Benchmarks, so no data available to read." +
                        ex.getMessage();
                uiHandler.showMessage(emsg, "Unable to READ", JOptionPane.ERROR_MESSAGE);
                uiHandler.handleMessage(emsg);
                return false;
            }
            long endTime = System.nanoTime();
            long elapsedTimeNs = endTime - startTime;
            double sec = (double) elapsedTimeNs / (double) 1000000000;
            double mbRead = (double) totalBytesReadInMark / (double) MEGABYTE;
            rMark.setBwMbSec(mbRead / sec);

            uiHandler.handleMessage("m:" + m + " READ IO is " + rMark.getBwMbSec() + " MB/s    "
                    + "(MBread " + mbRead + " in " + sec + " sec)");
            uiHandler.handleMark(rMark);

            run.setRunMax(rMark.getCumMax());
            run.setRunMin(rMark.getCumMin());
            run.setRunAvg(rMark.getCumAvg());
            run.setEndTime(new Date());
        }

            /*
              Persist info about the Read BM Run (e.g. into Derby Database) and add it to a GUI panel
             */
        EntityManager em = EM.getEntityManager();
        em.getTransaction().begin();
        em.persist(run);
        em.getTransaction().commit();

        uiHandler.addRun(run);
        return true;
    }

}
