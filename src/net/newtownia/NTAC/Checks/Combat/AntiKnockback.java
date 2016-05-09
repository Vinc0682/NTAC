package net.newtownia.NTAC.Checks.Combat;

import net.newtownia.NTAC.Checks.AbstractCheck;
import net.newtownia.NTAC.NTAC;
import net.newtownia.NTAC.Utils.PlayerUtils;
import net.newtownia.NTAC.Utils.PunishUtils;
import net.newtownia.NTAC.Utils.Strings;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.ArrayList;

public class AntiKnockback extends AbstractCheck implements Listener
{
    private String kickMsg = Strings.getString(1, 0xFF);
    private int adjust = 2;
    private ArrayList<Material> unsolidMaterials;

    public AntiKnockback(NTAC p)
    {
        super(p, Strings.getString(2, 0xFF));
        loadConfig();

        unsolidMaterials = new ArrayList<>();
        unsolidMaterials.add(Material.AIR);
        unsolidMaterials.add(Material.SIGN);
        unsolidMaterials.add(Material.SIGN_POST);
        unsolidMaterials.add(Material.TRIPWIRE);
        unsolidMaterials.add(Material.TRIPWIRE_HOOK);
        unsolidMaterials.add(Material.SUGAR_CANE_BLOCK);
        unsolidMaterials.add(Material.LONG_GRASS);
        unsolidMaterials.add(Material.YELLOW_FLOWER);
    }

    public void loadConfig()
    {
        YamlConfiguration config = pl.getConfiguration();

        adjust = Integer.valueOf(config.getString("Anti-Knockback.Adjustment"));

        kickMsg = config.getString("Anti-Knockback.Kick-Message");
        kickMsg = pl.getMessageUtils().formatMessage(kickMsg);
    }

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent e)
    {
        if(!isEnabled())
            return;

        if(!isKnockbackEvent(e))
            return;

        final Player p = (Player)e.getEntity();
        final Location oldLoc = p.getLocation();

        if(p.hasPermission("ntac.bypass.antiknockback"))
            return;

        Bukkit.getScheduler().runTaskLater(pl, new Runnable() {
            @Override
            public void run()
            {
                if(!isStillKnockbackable(p))
                    return;

                Location currentLoc = p.getLocation();

                if(currentLoc.getY() == oldLoc.getY())
                    PunishUtils.kickPlayer(p, kickMsg);
            }
        }, getTicksToWait(p) + adjust);
    }

    private boolean isKnockbackEvent(EntityDamageByEntityEvent e)
    {
        if(e.isCancelled())
            return false;

        if(e.getEntityType() != EntityType.PLAYER)
             return false;

        if(e.getDamager().getType() == EntityType.ENDER_DRAGON)
            return false;

        Player p = (Player)e.getEntity();
        Block b = p.getEyeLocation().getBlock().getRelative(BlockFace.UP);

        if(isBlockSolid(b.getType()))
        {
            return false;
        }

        return true;
    }

    private boolean isStillKnockbackable(Player p)
    {
        if(!p.isOnline())
            return false;

        if(p.isDead())
            return false;

        return true;
    }

    private boolean isBlockSolid(Material m)
    {
        return !unsolidMaterials.contains(m);
    }

    public int getTicksToWait(Player p)
    {
        return  PlayerUtils.getPing(p) / 50;
    }
}
