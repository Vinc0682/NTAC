package net.newtownia.NTAC.Utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vinc0682 on 21.03.2016.
 */
public class ItemUtils
{
    private static List<Material> swords;
    private static List<Material> foods;

    public static List<Material> getSwords()
    {
        if (swords == null)
        {
            swords = new ArrayList<>();
            swords.add(Material.WOOD_SWORD);
            swords.add(Material.STONE_SWORD);
            swords.add(Material.IRON_SWORD);
            swords.add(Material.DIAMOND_SWORD);
        }
        return swords;
    }

    public static boolean isSword(Material m) { return getSwords().contains(m); }
    public static boolean isSword(ItemStack stack)
    {
        if (stack == null)
            return false;
        return isSword(stack.getType());
    }

    public static List<Material> getFoods()
    {
        if (foods == null)
        {
            foods = new ArrayList<>();
            foods.add(Material.APPLE);
            foods.add(Material.BREAD);
            foods.add(Material.PORK);
            foods.add(Material.GRILLED_PORK);
            foods.add(Material.GOLDEN_APPLE);
            foods.add(Material.RAW_FISH);
            foods.add(Material.COOKED_FISH);
            foods.add(Material.COOKIE);
            foods.add(Material.MELON);
            foods.add(Material.RAW_BEEF);
            foods.add(Material.COOKED_BEEF);
            foods.add(Material.RAW_CHICKEN);
            foods.add(Material.COOKED_CHICKEN);
            foods.add(Material.ROTTEN_FLESH);
            foods.add(Material.SPIDER_EYE);
            foods.add(Material.CARROT_ITEM);
            foods.add(Material.POTATO_ITEM);
            foods.add(Material.BAKED_POTATO);
            foods.add(Material.POISONOUS_POTATO);
            foods.add(Material.PUMPKIN_PIE);
            foods.add(Material.RABBIT);
            foods.add(Material.COOKED_RABBIT);
            foods.add(Material.RABBIT_STEW);
            foods.add(Material.MUTTON);
            foods.add(Material.COOKED_MUTTON);
        }
        return foods;
    }

    public static boolean isFood(Material m)
    {
        return getFoods().contains(m);
    }
    public static boolean isFood(ItemStack stack)
    {
        if (stack == null)
            return false;
        return isFood(stack.getType());
    }

    public static boolean isUsable(Material m)
    {
        if (isSword(m))
            return true;
        if (isFood(m))
            return true;
        if (m == Material.BOW)
            return true;
        return false;
    }
    public static boolean isUsable(ItemStack stack)
    {
        if (stack == null)
            return false;
        return isUsable(stack.getType());
    }
}
