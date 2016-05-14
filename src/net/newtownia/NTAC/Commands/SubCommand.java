package net.newtownia.NTAC.Commands;

import net.newtownia.NTAC.NTAC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Created by Vinc0682 on 14.05.2016.
 */
public abstract class SubCommand
{
    private String name;

    public SubCommand(String name)
    {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract void execute(NTAC pl, CommandSender cs, Command cmd, String label, String[] args);
}
