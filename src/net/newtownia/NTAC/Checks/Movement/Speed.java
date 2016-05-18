package net.newtownia.NTAC.Checks.Movement;

import net.newtownia.NTAC.Action.ViolationManager;
import net.newtownia.NTAC.NTAC;
import net.newtownia.NTAC.Utils.PlayerUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

/**
 * Created by HorizonCode on 17.05.2016. Recoded by VincBreaker on 18.05.2016.
 */
public class Speed extends AbstractMovementCheck
{
    private double sprinting = 0.415;
    private double sneaking = 0.215;
    private double cobweb = 0.1;
    private double ice = 0.71;
    private double jump = 1.8;
    private double velocity = 2;
    private double speedPotion = 1.32;
    private double slowPotion = 0.997;
    private double bandFactor = 5;

    private ViolationManager bandVlManager;

    public Speed(NTAC pl, MovementBase movementBase)
    {
        super(pl, movementBase, "Speed");
        bandVlManager = new ViolationManager();
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent event)
    {
        if (!isEnabled())
            return;
        Player p = event.getPlayer();
        UUID pUUID = p.getUniqueId();
        if (p.hasPermission("ntac.bypass.speed") || p.isFlying() || p.isInsideVehicle())
            return;

        if (PlayerUtils.isUnderBlock(p))
            return;
        if (p.isFlying())
            return;

        Location from = event.getFrom();
        Location to = event.getTo();
        double dX = from.getX() - to.getX();
        double dZ = from.getZ() - to.getZ();
        double distSq = dX * dX + dZ * dZ;

        if (movementBase.isTeleporting(pUUID)) // @HorizonCode: This will fix my boost
            return;

        double speed = sprinting;
        if (p.isSneaking())
            speed = sneaking;
        if (PlayerUtils.isOnIce(p))
            speed = ice;
        if (PlayerUtils.isInWeb(p.getLocation()))
            speed = cobweb;
        if (!movementBase.isPlayerOnGround(p))
            speed *= jump;
        if (System.currentTimeMillis() - movementBase.getLastVeleocityTime(pUUID) > 1000)
            speed *= velocity;
        if (p.hasPotionEffect(PotionEffectType.SPEED))
            speed *= PlayerUtils.getPotionEffect(p, PotionEffectType.SPEED).getAmplifier() * speedPotion;
        if (p.hasPotionEffect(PotionEffectType.SLOW))
            speed *= PlayerUtils.getPotionEffect(p, PotionEffectType.SLOW).getAmplifier() * slowPotion;
        speed *= 0.1;

        p.sendMessage("Expected speed: " + speed + " DistSQ: " + distSq);

        if (distSq > speed)
        {
            double vlIncrement = (distSq - speed) * bandFactor;
            bandVlManager = new ViolationManager();
            bandVlManager.addViolation(p, vlIncrement);

            if (bandVlManager.getViolation(p) > 8)
            {
                p.teleport(bandVlManager.getFirstViolationLocation(p));
            }
        }
        else
            bandVlManager.resetPlayerViolation(p);
    }


    @Override
    public void loadConfig()
    {

    }
}
