package edu.touro.mco152.bm.Invoker;

import edu.touro.mco152.bm.UIHandler;
import edu.touro.mco152.bm.persist.DiskRun;

/**
 * I decided to use a custom class for the parameters.
 */

public class Parameters {
    public boolean isMultiple() {
        return isMultiple;
    }

    public UIHandler getUiHandler() {
        return uiHandler;
    }

    public int getStartNumFile() {
        return startNumFile;
    }

    public int getNumOfMarks() {
        return numOfMarks;
    }

    public int getNumOfBlocks() {
        return numOfBlocks;
    }

    public int getBlockSizeKb() {
        return blockSizeKb;
    }

    public int getTotalUnits() {
        return totalUnits;
    }
    public DiskRun.BlockSequence getBlockOrder() {
        return blockOrder;
    }

    boolean isMultiple;

    UIHandler uiHandler;
    int startNumFile;
    int numOfMarks;
    int numOfBlocks;
    int blockSizeKb;

    DiskRun.BlockSequence blockOrder;

    int totalUnits;



    public Parameters(boolean isMultiple, UIHandler uiHandler,
                      int startNumFile, int numOfMarks,
                      int numOfBlocks, int blockSizeKb,

                      DiskRun.BlockSequence blockOrder){
        this.isMultiple = isMultiple;
        this.uiHandler = uiHandler;
        this.startNumFile = startNumFile;
        this.numOfMarks = numOfMarks;
        this.numOfBlocks = numOfBlocks;
        this.blockSizeKb = blockSizeKb;
        this.blockOrder = blockOrder;
        this.totalUnits = numOfBlocks * numOfMarks;
    }

    public Parameters(boolean isMultiple, UIHandler uiHandler,
                      int startNumFile, int numOfMarks,
                      int numOfBlocks, int blockSizeKb,
                      int totalUnits, DiskRun.BlockSequence blockOrder){
        this.isMultiple = isMultiple;
        this.uiHandler = uiHandler;
        this.startNumFile = startNumFile;
        this.numOfMarks = numOfMarks;
        this.numOfBlocks = numOfBlocks;
        this.blockSizeKb = blockSizeKb;
        this.blockOrder = blockOrder;
        this.totalUnits = totalUnits;
    }
}
