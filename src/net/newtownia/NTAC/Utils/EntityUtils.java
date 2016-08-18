package net.newtownia.NTAC.Utils;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Vinc0682 on 16.05.2016.
 */
public class EntityUtils
{
    private static List<EntityType> passiveEntitys = Arrays.asList(EntityType.BOAT, EntityType.MINECART,
            EntityType.ITEM_FRAME, EntityType.ARMOR_STAND, EntityType.MINECART_CHEST, EntityType.MINECART_COMMAND,
            EntityType.MINECART_FURNACE, EntityType.MINECART_HOPPER, EntityType.MINECART_MOB_SPAWNER,
            EntityType.MINECART_TNT, EntityType.SHULKER, EntityType.SHULKER_BULLET, EntityType.AREA_EFFECT_CLOUD,
            EntityType.DRAGON_FIREBALL, EntityType.FIREBALL, EntityType.FISHING_HOOK, EntityType.LIGHTNING,
            EntityType.PRIMED_TNT, EntityType.WEATHER, EntityType.DROPPED_ITEM, EntityType.ARROW,
            EntityType.LINGERING_POTION, EntityType.LEASH_HITCH, EntityType.ENDER_PEARL);

    public static Entity getEntityByEntityID(int entityID, World world)
    {
        Entity result = null;
        for (Entity en : world.getEntities())
            if (en.getEntityId() == entityID)
                result = en;
        return result;
    }

    public static boolean isPassive(EntityType type)
    {
        return passiveEntitys.contains(passiveEntitys);
    }
    public static boolean isPassive(Entity entity)
    {
        return isPassive(entity.getType());
    }
}
