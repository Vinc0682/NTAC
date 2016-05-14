package net.newtownia.NTAC.Commands;

import net.newtownia.NTAC.NTAC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Created by Vinc0682 on 14.05.2016.
 */
public class Version extends SubCommand
{
    public Version() {
        super("version");
    }

    @Override
    public void execute(NTAC pl, CommandSender cs, Command cmd, String label, String[] args)
    {
        pl.getMessageUtils().printMessage(cs, "Version");
    }
}
