package net.newtownia.NTAC.Checks.Movement;

import com.comphenix.packetwrapper.WrapperPlayClientBlockDig;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import net.newtownia.NTAC.Action.ActionData;
import net.newtownia.NTAC.Action.ViolationManager;
import net.newtownia.NTAC.NTAC;
import net.newtownia.NTAC.Utils.ItemUtils;
import net.newtownia.NTAC.Utils.PunishUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class NoSlowBlock extends AbstractMovementCheck
{
    int useReleaseThreshold = 40;
    int invalidateThreshold = 1000;
    int invalidateFreq = 5;

    Map<UUID, Long> lastBlockToggleTime;

    ViolationManager vlManger;
    ActionData actionData;

    PacketAdapter diggingPacketEvent;

    public NoSlowBlock(NTAC pl, MovementBase movementBase)
    {
        super(pl, movementBase, "NoSlowBlock");

        vlManger = new ViolationManager();
        lastBlockToggleTime = new HashMap<>();

        diggingPacketEvent = new PacketAdapter(pl, ListenerPriority.HIGH, PacketType.Play.Client.BLOCK_DIG) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                handleDiggingPacket(event);
            }
        };
        ProtocolLibrary.getProtocolManager().addPacketListener(diggingPacketEvent);

        Bukkit.getScheduler().runTaskTimer(pl, new Runnable() {
            @Override
            public void run() {
                vlManger.resetAllOldViolation(invalidateThreshold);
            }
        }, invalidateFreq, invalidateFreq);

        loadConfig();
    }

    private void handleDiggingPacket(PacketEvent event)
    {
        if(!isEnabled())
            return;
        if(event.getPacketType() != PacketType.Play.Client.BLOCK_DIG)
            return;
        if(event.getPlayer().hasPermission("ntac.bypass.noslowblock"))
            return;
        if (!ItemUtils.isSword(event.getPlayer().getItemInHand()))
            return;

        Player p = event.getPlayer();
        UUID pUUID = p.getUniqueId();
        WrapperPlayClientBlockDig packet = new WrapperPlayClientBlockDig(event.getPacket());

        if(packet.getStatus() == EnumWrappers.PlayerDigType.RELEASE_USE_ITEM)
        {
            if(!lastBlockToggleTime.containsKey(pUUID))
            {
                lastBlockToggleTime.put(pUUID, System.currentTimeMillis());
                return;
            }

            if(lastBlockToggleTime.get(pUUID) + useReleaseThreshold > System.currentTimeMillis())
            {
                vlManger.addViolation(p, 1);

                int vl = vlManger.getViolation(p);
                if(actionData.doesLastViolationCommandsContains(vl, "cancel"))
                {
                    Location resetLoc = vlManger.getFirstViolationLocation(p);
                    if(resetLoc != null)
                        p.teleport(resetLoc);
                }
                PunishUtils.runViolationAction(p, vl, vl, actionData);
            }

            lastBlockToggleTime.put(pUUID, System.currentTimeMillis());
        }
    }

    @Override
    public void loadConfig()
    {
        useReleaseThreshold = Integer.parseInt(pl.getConfiguration().getString("NoSlowBlock.Use-Release-Threshold"));
        invalidateThreshold = Integer.parseInt(pl.getConfiguration().getString("NoSlowBlock.Invalidate-Threshold"));
        invalidateFreq = Integer.parseInt(pl.getConfiguration().getString("NoSlowBlock.Invalidate-Freq"));

        actionData = new ActionData(pl.getConfiguration(), "NoSlowBlock.Actions");
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent event) {
        //Do nothing
    }
}
