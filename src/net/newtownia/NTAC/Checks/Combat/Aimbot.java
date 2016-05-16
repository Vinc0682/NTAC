package net.newtownia.NTAC.Checks.Combat;

import net.newtownia.NTAC.Action.ViolationManager;
import net.newtownia.NTAC.NTAC;
import net.newtownia.NTAC.Utils.MathUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.*;

/**
 * Created by Vinc0682 on 15.05.2016.
 */
public class Aimbot extends AbstractCombatCheck
{
    private int dataCount = 5;
    private int threshold = 5;
    private int minYawChange = 5;

    private ViolationManager vlManager;
    private Map<UUID, List<Double>> playerAttackAngles;
    private Map<UUID, Float> playerLastAttackYaw;

    public Aimbot(NTAC pl, CombatBase combatBase)
    {
        super(pl, combatBase, "Aimbot");
        vlManager = new ViolationManager();
        playerAttackAngles = new HashMap<>();
        playerLastAttackYaw = new HashMap<>();

        loadConfig();
    }

    @Override
    protected void onAttack(EntityDamageByEntityEvent event)
    {
        Player p = (Player)event.getDamager();
        UUID pUUID = p.getUniqueId();

        if (playerLastAttackYaw.containsKey(pUUID))
        {
            if (MathUtils.isSame((double)p.getLocation().getYaw(),
                    playerLastAttackYaw.get(pUUID), minYawChange))
            {
                playerLastAttackYaw.put(pUUID, p.getLocation().getYaw());
                return;
            }
        }
        playerLastAttackYaw.put(pUUID, p.getLocation().getYaw());

        double angleDiff = MathUtils.getYawDiff(p.getLocation(), event.getEntity().getLocation());
        if (angleDiff < 0)
            angleDiff *= -1;

        if (!playerAttackAngles.containsKey(pUUID))
        {
            playerAttackAngles.put(pUUID, new ArrayList<>(Collections.singletonList(angleDiff)));
            return;
        }

        List<Double> angles = playerAttackAngles.get(pUUID);
        angles.add(0, angleDiff);
        while (angles.size() > dataCount)
            angles.remove(dataCount);

        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (double angle : angles)
        {
            sb.append(angle);
            sb.append(", ");
        }
        sb.append("]");
        //p.sendMessage(sb.toString());


        if (angles.size() == dataCount)
        {
            double average = MathUtils.getAverageDouble(angles);
            double min = average - threshold;
            double max = average + threshold;
            boolean suspicious = true;
            for (double angle : angles)
            {
                if (MathUtils.isSame(angle, average, threshold))
                {
                    suspicious = false;
                    p.sendMessage("Failed at: " + angle + " Average: " + average);
                }
            }
            if (suspicious)
            {
                p.sendMessage("You are suspicious!");
            }
        }
    }

    @Override
    public void loadConfig()
    {

    }
}
