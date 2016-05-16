package net.newtownia.NTAC.Utils;

import fr.neatmonster.nocheatplus.NCPAPIProvider;
import fr.neatmonster.nocheatplus.checks.moving.model.MoveInfo;
import fr.neatmonster.nocheatplus.checks.moving.util.AuxMoving;
import org.bukkit.entity.Player;

/**
 * Created by Vinc0682 on 16.05.2016.
 */
public class NCPLayer
{
    public static boolean isPlayerOnGround(Player p) throws Exception, ClassNotFoundException
    {
        AuxMoving aux = NCPAPIProvider.getNoCheatPlusAPI().getGenericInstance(AuxMoving.class);
        MoveInfo moveInfo = aux.usePlayerMoveInfo();
        moveInfo.set(p, p.getLocation(), null, 0);
        return moveInfo.from.isOnGround();
    }
}
