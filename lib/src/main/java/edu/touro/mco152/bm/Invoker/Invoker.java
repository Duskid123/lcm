package edu.touro.mco152.bm.Invoker;

import edu.touro.mco152.bm.Commands.Command;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to be an Invoker so that the commands could be run.
 */
public class Invoker {
    // This is a map so that we could keep track of commands and the parameters of the commands.
    private Map<Command, Parameters> commands;


    //Made this static so that it could be accessed statically.
    private static Command currentCommand;

    /**
     * Construct
     */
    public Invoker(){
        this.commands = new HashMap<>();
    }

    /**
     *
     * @param command
     * @param parameters
     */
    public void addCommand(Command command, Parameters parameters){
        this.commands.put(command, parameters);
    }

    /**
     * This method runs a command. It sets the current command to be executed and references it so that if the user wants
     * to cancel the command it would work.
     * @param command
     * @param parameters
     * @return
     */
    static public boolean execute(Command command, Parameters parameters){
        currentCommand = command;
        return command.execute(parameters);
    }

    /**
     * This method runs all the commands that the invoker has already.
     * @return
     */
    public boolean execute(){
        for(Command command : commands.keySet()){
            if(!command.execute(commands.get(command))){
                return false;
            }
        }
        return true;
    }

    /**
     * This method would cancel the current command.
     */
    public static void cancel(){
        currentCommand.cancel();
    }
}
