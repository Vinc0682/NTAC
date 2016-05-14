package net.newtownia.NTAC.Commands;

import net.newtownia.NTAC.NTAC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Created by Vinc0682 on 14.05.2016.
 */
public class Reload extends SubCommand
{
    public Reload() {
        super("reload");
    }

    @Override
    public void execute(NTAC pl, CommandSender cs, Command cmd, String label, String[] args)
    {
        if (!cs.hasPermission("ntac.command.reload"))
        {
            pl.getMessageUtils().printMessage(cs, "NoPermission");
            return;
        }

        pl.reload();
        pl.getMessageUtils().printMessage(cs, "Reload");
    }
}
