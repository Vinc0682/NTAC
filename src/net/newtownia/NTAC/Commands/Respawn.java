package net.newtownia.NTAC.Commands;

import net.newtownia.NTAC.NTAC;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Vinc0682 on 30.05.2016.
 */
public class Respawn extends SubCommand
{
    public Respawn()
    {
        super("respawn");
    }

    @Override
    public void execute(NTAC pl, CommandSender cs, Command cmd, String label, String[] args)
    {
        if (!cs.hasPermission("ntac.command.punish.respawn"))
        {
            pl.getMessageUtils().printMessage(cs, "NoPermission");
            return;
        }

        if(args.length > 1) {
            Player victim = Bukkit.getPlayer(args[1]);
            if (victim != null)
            {
                for (Player p : Bukkit.getOnlinePlayers())
                {
                    victim.hidePlayer(p);
                    victim.showPlayer(p);
                }
            }
            else
                pl.getMessageUtils().printMessage(cs, "PlayerNotFound", args[1]);
        }
    }
}
