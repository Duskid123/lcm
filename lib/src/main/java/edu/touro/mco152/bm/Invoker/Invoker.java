package edu.touro.mco152.bm.Invoker;

import edu.touro.mco152.bm.Commands.Command;

import java.util.HashMap;
import java.util.Map;

public class Invoker {
    private Map<Command, Parameters> commands;
    private static Command currentCommand;

    public Invoker(){
        this.commands = new HashMap<>();
    }

    public void addCommand(Command command, Parameters parameters){
        this.commands.put(command, parameters);
    }

    static public boolean execute(Command command, Parameters parameters){
        currentCommand = command;
        return command.execute(parameters);
    }

    public boolean execute(){
        for(Command command : commands.keySet()){
            if(!command.execute(commands.get(command))){
                return false;
            }
        }
        return true;
    }

    public static void cancel(){
        currentCommand.cancel();
    }
}
