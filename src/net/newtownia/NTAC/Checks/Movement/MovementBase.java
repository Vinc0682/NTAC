package net.newtownia.NTAC.Checks.Movement;

import com.comphenix.packetwrapper.WrapperPlayClientBlockDig;
import com.comphenix.packetwrapper.WrapperPlayClientBlockPlace;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import net.newtownia.NTAC.Utils.MaterialUtils;
import net.newtownia.NTAC.Utils.MathUtils;
import net.newtownia.NTAC.Utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerVelocityEvent;

import java.util.*;

public class MovementBase implements Listener
{
    private Map<UUID, Long> playerStartMoveTimes;
    private Map<UUID, Location> playerStartMoveLocations;

    private Map<UUID, Long> playerLastTPTimes;
    private Map<UUID, Location> playerLastTPLocations;
    private List<UUID> teleportedPlayers;

    private Map<UUID, Boolean> playerOnGround;
    private Map<UUID, Integer> playerOnGroundMoves;
    private Map<UUID, Long> playerLastVelocityTime;
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
        teleportedPlayers = new ArrayList<>();

        playerOnGround = new HashMap<>();
        playerOnGroundMoves = new HashMap<>();
        playerLastVelocityTime = new HashMap<>();
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
        Location origTo = event.getTo().clone();
        Location origFrom = event.getFrom().clone();

        updateCachePreMove(event);
        raiseChecks(event);
        updateCachePostMove(event, origTo, origFrom);
    }

    public void updateCachePreMove(PlayerMoveEvent event)
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
        if (isPlayerOnGround(p))
        {
            if (!playerOnGroundMoves.containsKey(pUUID))
                playerOnGroundMoves.put(pUUID, 1);
            else
                playerOnGroundMoves.put(pUUID, playerOnGroundMoves.get(pUUID) + 1);
        }
        else
            playerOnGroundMoves.put(pUUID, 0);
    }

    public void raiseChecks(PlayerMoveEvent event)
    {
        for (AbstractMovementCheck check : movementChecks)
            check.onPlayerMove(event);
    }

    public void updateCachePostMove(PlayerMoveEvent event, Location origTo, Location origFrom)
    {
        Player p = event.getPlayer();
        UUID pUUID = p.getUniqueId();

        if (teleportedPlayers.contains(pUUID))
            teleportedPlayers.remove(pUUID);
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event)
    {
        playerLastTPTimes.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
        playerLastTPLocations.put(event.getPlayer().getUniqueId(), event.getTo());
        teleportedPlayers.add(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onVelocity(PlayerVelocityEvent event)
    {
        playerLastVelocityTime.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
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
            if (MaterialUtils.isUsable(p.getInventory().getItemInMainHand()))
                playerUsingItem.put(pUUID, true);
        }
    }

    //region Getters for the caching

    public long getLastVelocityTime(UUID pUUID)
    {
        if (!playerLastVelocityTime.containsKey(pUUID))
            return 0;
        return playerLastVelocityTime.get(pUUID);
    }

    public boolean hasVelocityTimePassed(UUID pUUID, int time)
    {
        return System.currentTimeMillis() - getLastVelocityTime(pUUID) > time;
    }

    public boolean hasPlayerMoveTimePassed(Player p, int milliseconds)
    {
        return hasPlayerMoveTimePassed(p.getUniqueId(), milliseconds);
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

    public long getPlayerLastTPTime(UUID pUUID)
    {
        if (!playerLastTPTimes.containsKey(pUUID))
            return -1;
        return playerLastTPTimes.get(pUUID);
    }

    public Location getPlayerLastTPLocation(UUID pUUID)
    {
        if (!playerLastTPLocations.containsKey(pUUID))
            return null;
        return playerLastTPLocations.get(pUUID);
    }

    public boolean isTeleportingTo(UUID pUUID, Location to)
    {
        Location tpTo = getPlayerLastTPLocation(pUUID);
        return isTeleporting(pUUID) && tpTo != null && MathUtils.isPositionSame(to, tpTo, 0);
    }

    public boolean isTeleporting(UUID pUUID)
    {
        return teleportedPlayers.contains(pUUID);
    }

    public boolean isPlayerOnGround(UUID pUUID)
    {
        if (!playerOnGround.containsKey(pUUID))
        {
            playerOnGround.put(pUUID, PlayerUtils.isPlayerOnGround(Bukkit.getPlayer(pUUID)));
        }
        return playerOnGround.get(pUUID);
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

    public int getPlayerOnGroundMoves(UUID pUUID)
    {
        if (!playerOnGroundMoves.containsKey(pUUID))
        {
            if (isPlayerOnGround(pUUID))
                playerOnGroundMoves.put(pUUID, 1);
            else
                playerOnGroundMoves.put(pUUID, 0);
        }
        return playerOnGroundMoves.get(pUUID);
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
