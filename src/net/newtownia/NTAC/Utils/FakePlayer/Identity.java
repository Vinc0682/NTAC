package net.newtownia.NTAC.Utils.FakePlayer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Identity
{
    public UUID uuid;
    public String name;
    public EntityType type = EntityType.PLAYER;

    public boolean isAlreadyOnline;
    public boolean visible;

    public static class Generator
    {
        private static Random rnd = new Random();

        private static char[] chars = new char[]{'a' , 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
                'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A' , 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
                'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
                '-','0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

        public static String generateName()
        {
            int length = 9 + rnd.nextInt(5);
            return "§c" + getRandomString(length - 2);
        }

        private static String getRandomString(int length)
        {
            StringBuilder sb = new StringBuilder(length);
            for(int i = 0; i < length; i += 1)
            {
                sb.append(chars[rnd.nextInt(chars.length)]);
            }
            return sb.toString();
        }

        public static Identity generateRandomIdentity()
        {
            Identity id = new Identity();

            id.name = generateName();
            id.uuid = UUID.randomUUID();
            id.isAlreadyOnline = false;
            id.type = EntityType.PLAYER;

            return id;
        }

        public static Identity generateIdentityForPlayer(Player p)
        {
            Identity id = new Identity();

            if(Bukkit.getOnlinePlayers().size() == 1)
                id = generateRandomIdentity();
            else
            {
                List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
                onlinePlayers.remove(p);

                Player idPlayer = onlinePlayers.get(new Random().nextInt(onlinePlayers.size()));

                id.name = idPlayer.getName();
                id.uuid = idPlayer.getUniqueId();
                id.isAlreadyOnline = true;
            }

            id.visible = true;

            return id;
        }

        public static Identity generateIdentityForPlayer(Player p, Entity attacked)
        {
            Identity id = generateRandomIdentity();
            if (attacked.getType() == EntityType.PLAYER)
             id = generateIdentityForPlayer(p);
            else
            {
                id.type = attacked.getType();
            }

            return id;
        }
    }
}
