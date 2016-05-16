package net.newtownia.NTAC.Utils;

import org.bukkit.World;
import org.bukkit.entity.Entity;

/**
 * Created by Vinc0682 on 16.05.2016.
 */
public class EntityUtils
{
    public static Entity getEntityByEntityID(int entityID, World world)
    {
        Entity result = null;
        for (Entity en : world.getEntities())
            if (en.getEntityId() == entityID)
                result = en;
        return result;
    }
}
