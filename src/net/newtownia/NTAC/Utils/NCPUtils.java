package net.newtownia.NTAC.Utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Created by Vinc0682 on 13.05.2016.
 */
public class NCPUtils
{
    public static boolean hasNoCheatPlus()
    {
        Plugin pl = Bukkit.getPluginManager().getPlugin("NoCheatPlus");
        if (pl != null && pl.isEnabled())
        {
            try
            {
                if (Class.forName("fr.neatmonster.nocheatplus.NCPAPIProvider") == null)
                    return false;
                if (Class.forName("fr.neatmonster.nocheatplus.checks.moving.model.MoveInfo") == null)
                    return false;
                if (Class.forName("fr.neatmonster.nocheatplus.checks.moving.util.AuxMoving") == null)
                    return false;
                return true;
            }
            catch (Exception e)
            {
                return false;
            }
        }
        else
            return false;
    }
}
