package net.newtownia.NTAC.Checks.Render;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import net.newtownia.NTAC.Checks.AbstractCheck;
import net.newtownia.NTAC.NTAC;
import net.newtownia.NTAC.Utils.FakePlayer.FakePlayer;
import net.newtownia.NTAC.Utils.FakePlayer.Identity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

/**
 * Created by Vinc0682 on 07.05.2016.
 */
public class Tracers extends AbstractCheck
{
    int radius = 30;
    int minY = -1;
    int maxY = 10;

    private ArrayList<FakePlayer> bots;
    private ArrayList<Vector> relatives;
    private Identity id;
    private Random rnd;

    private PacketAdapter moveLookPacketEvent;

    public Tracers(NTAC pl)
    {
        super(pl, "Tracers");
        bots = new ArrayList<>();
        relatives = new ArrayList<>();
        rnd = new Random();

        id = new Identity();
        id.visible = false;
        id.name = " ";
        id.uuid = UUID.randomUUID();
        id.type = EntityType.PLAYER;
        id.isAlreadyOnline = false;

        loadConfig();

        for (int i = 0; i < 10; i += 1)
        {
            bots.add(new FakePlayer(rnd.nextInt(9999) + 10000));
            double x = rnd.nextDouble() * radius * 2 - radius;
            double z = rnd.nextDouble() * radius * 2 - radius;
            double dist = x * x + z * z;
            double y = minY + (maxY - minY) * dist / radius;
            relatives.add(new Vector(x, y, z));
        }

        moveLookPacketEvent = new PacketAdapter(pl, ListenerPriority.HIGH, PacketType.Play.Client.POSITION,
                PacketType.Play.Client.LOOK,
                PacketType.Play.Client.POSITION_LOOK) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                onPlayerMove(event);
            }
        };

        ProtocolLibrary.getProtocolManager().addPacketListener(moveLookPacketEvent);
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event)
    {
        if (bots.size() <= 0)
            return;

        Player p = event.getPlayer();

        id.isAlreadyOnline = false;
        bots.get(0).spawnForPlayerWithIdentity(p, p.getLocation().clone().add(relatives.get(0)), id);
        id.isAlreadyOnline = true;
        for (int i = 1; i < bots.size(); i += 1)
        {
            bots.get(i).spawnForPlayerWithIdentity(p, p.getLocation().clone().add(relatives.get(i)), id);
        }
    }

    private void onPlayerMove(PacketEvent event)
    {
        for (int i = 0; i < bots.size(); i += 1)
            bots.get(i).moveTo(event.getPlayer(), event.getPlayer().getLocation().clone().add(relatives.get(i)));
    }

    @Override
    public void loadConfig()
    {

    }
}
