package net.newtownia.NTAC.Commands;

import net.newtownia.NTAC.NTAC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Vinc0682 on 29.05.2016.
 */
public class Gui extends SubCommand
{
    public Gui()
    {
        super("gui");
    }

    @Override
    public void execute(NTAC pl, CommandSender cs, Command cmd, String label, String[] args)
    {
        if (!cs.hasPermission("ntac.command.gui"))
        {
            pl.getMessageUtils().printMessage(cs, "NoPermission");
            return;
        }
        if (!(cs instanceof Player))
        {
            pl.getMessageUtils().printMessage(cs, "NotPlayer");
            return;
        }
        pl.getGui().openGUI((Player)cs);
    }
}
