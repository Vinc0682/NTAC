package net.newtownia.NTAC.Checks.Movement;

import net.newtownia.NTAC.Action.ActionData;
import net.newtownia.NTAC.Action.ViolationManager;
import net.newtownia.NTAC.NTAC;
import net.newtownia.NTAC.Utils.PlayerUtils;
import net.newtownia.NTAC.Utils.PunishUtils;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

/**
 * Created by HorizonCode on 17.05.2016. Recoded by VincBreaker on 18.05.2016.
 */
public class Speed extends AbstractMovementCheck
{
    private double sprinting = 0.83;
    private double sneaking = 0.215;
    private double cobweb = 0.2;
    private double ice = 1.7;
    private double jump = 1.8;
    private double velocity = 2;
    private double speedPotion = 1.45;
    private double slowPotion = 0.8;
    private double stairs = 1.5;

    private double vlDecrease = 0.5;
    private double maxVL = 10;
    private ActionData actionData;

    private ViolationManager vlManager;

    public Speed(NTAC pl, MovementBase movementBase)
    {
        super(pl, movementBase, "Speed");
        vlManager = new ViolationManager();

        loadConfig();
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent event)
    {
        if (!isEnabled())
            return;
        Player p = event.getPlayer();
        UUID pUUID = p.getUniqueId();
        if (p.hasPermission("ntac.bypass.speed") || p.getAllowFlight() || p.isInsideVehicle())
            return;

        if (PlayerUtils.isUnderBlock(p))
            return;
        if (PlayerUtils.isGlidingWithElytra(p))
            return;

        Location from = event.getFrom();
        Location to = event.getTo();
        double dX = to.getX() - from.getX();
        double dY = to.getY() - from.getY();
        double dZ = to.getZ() - from.getZ();
        double distSq = dX * dX + dZ * dZ;

        if (movementBase.isTeleporting(pUUID))
            return;

        double speed = sprinting;
        if (p.isSneaking())
            speed = sneaking;
        if (PlayerUtils.isOnIce(p, false))
            speed = ice;
        if (PlayerUtils.isInWeb(p.getLocation()))
            speed = cobweb;
        if (PlayerUtils.isOnStair(p))
            speed *= stairs;
        if (isJumping(p, from, to))
            speed *= jump;
        if (!movementBase.hasVelocityTimePassed(pUUID, 1000))
            speed *= velocity;
        if (p.hasPotionEffect(PotionEffectType.SPEED))
            speed *= (PlayerUtils.getPotionEffect(p, PotionEffectType.SPEED).getAmplifier() + 1) * speedPotion;
        if (p.hasPotionEffect(PotionEffectType.SLOW))
            speed *= (PlayerUtils.getPotionEffect(p, PotionEffectType.SLOW).getAmplifier() + 1) * slowPotion;
        speed *= 0.1;

        //p.sendMessage("Expected: " + speed + " Real: " + distSq);

        if (distSq > speed)
        {
            if (vlManager.getViolation(p) < maxVL)
                vlManager.addViolation(p, 1);
            int vl = vlManager.getViolationInt(p);
            if(actionData.doesLastViolationCommandsContains(vl, "cancel"))
            {
                Location resetLoc = vlManager.getFirstViolationLocation(p);
                if(resetLoc != null)
                    p.teleport(resetLoc);
            }
            PunishUtils.runViolationAction(p, vl, vl, actionData);
        }
        else
            vlManager.subtractViolation(p, vlDecrease);
    }

    private boolean isJumping(Player p, Location from, Location to)
    {
        boolean stepping = false;
        if (to.getY() > from.getY())
            stepping = PlayerUtils.isOnSteps(p);
        return !movementBase.isPlayerOnGround(p) || !PlayerUtils.isLocationOnGroundNTAC(to) || stepping;
    }

    @Override
    public void loadConfig()
    {
        YamlConfiguration config = pl.getConfiguration();
        sprinting = Double.valueOf(config.getString("Speed.Sprinting"));
        sneaking = Double.valueOf(config.getString("Speed.Sneaking"));
        cobweb = Double.valueOf(config.getString("Speed.Cobweb"));
        ice = Double.valueOf(config.getString("Speed.Ice"));
        jump = Double.valueOf(config.getString("Speed.Jump-Multiplier"));
        velocity = Double.valueOf(config.getString("Speed.Velocity-Multiplier"));
        speedPotion = Double.valueOf(config.getString("Speed.Speed-Potion"));
        slowPotion = Double.valueOf(config.getString("Speed.Slow-Potion"));
        stairs = Double.valueOf(config.getString("Speed.Stairs"));
        vlDecrease = Double.valueOf(config.getString("Speed.VL-Decrease"));
        maxVL = Double.valueOf(config.getString("Speed.Max-VL"));
        actionData = new ActionData(config, "Speed.Actions");
    }
}
