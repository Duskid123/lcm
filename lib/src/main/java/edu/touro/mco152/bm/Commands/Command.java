package edu.touro.mco152.bm.Commands;

import edu.touro.mco152.bm.Invoker.Parameters;

/**
 * this interface is used to take parameters and execute the command.
 */
public interface Command {
    /**
     * execute the command
     * @param parameters
     * @return true if the command executed successfully and false otherwise.
     */
    public boolean execute(Parameters parameters);

    /**
     * cancel the command.
     */
    public void cancel();


}
