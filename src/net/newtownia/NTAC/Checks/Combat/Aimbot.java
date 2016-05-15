package net.newtownia.NTAC.Checks.Combat;

import net.newtownia.NTAC.Action.ViolationManager;
import net.newtownia.NTAC.NTAC;
import net.newtownia.NTAC.Utils.MathUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Vinc0682 on 15.05.2016.
 */
public class Aimbot extends AbstractCombatCheck
{
    private ViolationManager vlManager;
    private Map<UUID, List<Double>> playerAngles;

    public Aimbot(NTAC pl, CombatBase combatBase)
    {
        super(pl, combatBase, "Aimbot");
        vlManager = new ViolationManager();
        playerAngles = new HashMap<>();

        loadConfig();
    }

    @Override
    protected void onAttack(EntityDamageByEntityEvent event)
    {
        Player p = (Player)event.getDamager();
        double angleDiff = MathUtils.getYawDiff(p.getLocation(), event.getEntity().getLocation());


    }

    @Override
    public void loadConfig()
    {

    }
}
