package edu.touro.mco152.bm.Commands;

import edu.touro.mco152.bm.Invoker.Parameters;
import edu.touro.mco152.bm.persist.DiskRun;

/**
 * this interface is used to take parameters and execute the command.
 */
public interface Command {
    /**
     * execute the command
     * @param parameters
     * @return The result of the command, null if there was something wrong or if it was cancelled.
     */
    public DiskRun execute(Parameters parameters);

    /**
     * cancel the command.
     */
    public void cancel();
}
