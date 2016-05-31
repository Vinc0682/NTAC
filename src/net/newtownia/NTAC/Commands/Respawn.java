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
                /*for (Entity e : victim.getWorld().getNearbyEntities(victim.getLocation(), 40, 40, 40))
                {
                    if (e.getType() == EntityType.PLAYER)
                        continue;

                    PacketGenerator.getDestroyPacket(e.getEntityId()).sendPacket(victim);

                    final Entity tmp = e;
                    Bukkit.getScheduler().runTaskLater(pl, new Runnable() {
                        @Override
                        public void run()
                        {
                            LogUtils.debug("Spawning: " + tmp.getName());
                            Identity entityID = new Identity();
                            entityID.name = tmp.getName();
                            entityID.type = tmp.getType();
                            entityID.uuid = tmp.getUniqueId();
                            entityID.isAlreadyOnline = true;
                            PacketGenerator.getIdentityNotPlayerSpawnPacket(entityID, tmp.getEntityId(), tmp.getLocation());
                        }
                    }, 2);
                }*/
            }
            else
                pl.getMessageUtils().printMessage(cs, "PlayerNotFound", args[1]);
        }
    }
}
