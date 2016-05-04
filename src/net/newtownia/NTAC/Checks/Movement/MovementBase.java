package net.newtownia.NTAC.Checks.Movement;

import com.comphenix.packetwrapper.WrapperPlayClientBlockDig;
import com.comphenix.packetwrapper.WrapperPlayClientBlockPlace;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import net.newtownia.NTAC.Utils.ItemUtils;
import net.newtownia.NTAC.Utils.PlayerUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MovementBase implements Listener
{
    private Map<UUID, Long> playerStartMoveTimes;
    private Map<UUID, Location> playerStartMoveLocations;

    private Map<UUID, Long> playerLastTPTimes;
    private Map<UUID, Location> playerLastTPLocations;

    private Map<UUID, Boolean> playerOnGround;
    private Map<UUID, Boolean> playerUsingItem;

    int newMoveTimeThreshold = 500;

    private ArrayList<AbstractMovementCheck> movementChecks;

    PacketAdapter useItemAdapter;

    public MovementBase()
    {
        playerStartMoveLocations = new HashMap<>();
        playerStartMoveTimes = new HashMap<>();

        playerLastTPTimes = new HashMap<>();
        playerLastTPLocations = new HashMap<>();

        playerOnGround = new HashMap<>();
        playerUsingItem = new HashMap<>();

        movementChecks = new ArrayList<>();

        //Disabled since it's buggy as hell
        /*useItemAdapter = new PacketAdapter(NTAC.getInstance(), ListenerPriority.HIGH, PacketType.Play.Client.BLOCK_DIG,
                PacketType.Play.Client.BLOCK_PLACE) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                handleItemUsePacket(event);
            }
        };
        ProtocolLibrary.getProtocolManager().addPacketListener(useItemAdapter);*/
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event)
    {
        updateCache(event);
        raiseChecks(event);
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event)
    {
        playerLastTPTimes.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
        playerLastTPLocations.put(event.getPlayer().getUniqueId(), event.getTo());
    }

    public void updateCache(PlayerMoveEvent event)
    {
        Player p = event.getPlayer();
        UUID pUUID = p.getUniqueId();

        if(!playerStartMoveTimes.containsKey(pUUID))
        {
            playerStartMoveTimes.put(pUUID, System.currentTimeMillis());
            playerStartMoveLocations.put(pUUID, event.getFrom());
        }
        else
        {
            if(hasPlayerMoveTimePassed(pUUID, newMoveTimeThreshold))
            {
                playerStartMoveTimes.put(pUUID, System.currentTimeMillis());
                playerStartMoveLocations.put(pUUID, event.getFrom());
            }
        }

        playerOnGround.put(pUUID, PlayerUtils.isPlayerOnGround(p));
    }

    public void raiseChecks(PlayerMoveEvent event)
    {
        for (AbstractMovementCheck check : movementChecks)
            check.onPlayerMove(event);
    }

    //Useless
    private void handleItemUsePacket(PacketEvent event)
    {
        Player p = event.getPlayer();
        UUID pUUID = p.getUniqueId();

        if (event.getPacketType() == PacketType.Play.Client.BLOCK_DIG)
        {
            p.sendMessage("Gotta dig packet");
            WrapperPlayClientBlockDig packet = new WrapperPlayClientBlockDig(event.getPacket());

            if (packet.getStatus() == EnumWrappers.PlayerDigType.RELEASE_USE_ITEM)
                playerUsingItem.put(pUUID, false);
        }
        else if (event.getPacketType() == PacketType.Play.Client.BLOCK_PLACE)
        {
            p.sendMessage("Gotta place packet");
            WrapperPlayClientBlockPlace packet = new WrapperPlayClientBlockPlace(event.getPacket());
            if (ItemUtils.isUsable(p.getInventory().getItemInMainHand()))
                playerUsingItem.put(pUUID, true);
        }
    }

    //region Getters for the caching

    public boolean hasPlayerMoveTimePassed(Player p, int milliseconds)
    {
        return System.currentTimeMillis() >= playerStartMoveTimes.get(p.getUniqueId()) + milliseconds;
    }

    public boolean hasPlayerMoveTimePassed(UUID pUUID, int milliseconds)
    {
        return System.currentTimeMillis() >= playerStartMoveTimes.get(pUUID) + milliseconds;
    }

    public Location getPlayerMoveStartLocation(Player p)
    {
        if(!playerStartMoveLocations.containsKey(p.getUniqueId()))
            return null;

        return playerStartMoveLocations.get(p.getUniqueId());
    }

    public long getPlayerLastTPTime(Player p)
    {
        if (!playerLastTPTimes.containsKey(p.getUniqueId()))
            return -1;
        return playerLastTPTimes.get(p.getUniqueId());
    }

    public Location getPlayerLastTPLocation(Player p)
    {
        if (!playerLastTPLocations.containsKey(p.getUniqueId()))
            return null;
        return playerLastTPLocations.get(p.getUniqueId());
    }

    public boolean isPlayerOnGround(Player p)
    {
        UUID pUUID = p.getUniqueId();
        if (!playerOnGround.containsKey(pUUID))
        {
            playerOnGround.put(pUUID, PlayerUtils.isPlayerOnGround(p));
        }
        return playerOnGround.get(pUUID);
    }

    public boolean isPlayerUsingItem(UUID pUUID)
    {
        if (!playerUsingItem.containsKey(pUUID))
            return false;
        return playerUsingItem.get(pUUID);
    }
    //endregion

    //region Observator functions
    public void registerMovementCheck(AbstractMovementCheck movementCheck)
    {
        movementChecks.add(movementCheck);
    }

    public void unregisterMovementCheck(AbstractMovementCheck movementCheck)
    {
        movementChecks.remove(movementCheck);
    }
    //endregion
}
