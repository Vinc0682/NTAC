package net.newtownia.NTAC.Utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Vinc0682 on 29.05.2016.
 */
public class GuiUtils
{
    public static ItemStack createItem(String name, Material material, String lore)
    {
        return createItem(name, material, Arrays.asList(lore.split("<n>")));
    }
    public static ItemStack createItem(String name, Material material, List<String> lore)
    {
        return createItem(name, material, 0, lore);
    }
    @SuppressWarnings("deprecation")
    public static ItemStack createItem(String name, Material material, int data, List<String> lore)
    {
        ItemStack result = new ItemStack(material, 1, (byte)data);
        ItemMeta meta = result.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        List<String> lines = new ArrayList<>();
        for (String line : lore)
        {
            line = ChatColor.translateAlternateColorCodes('&', line);
            line = line.replace("ae", "ä");
            line = line.replace("oe", "ö");
            line = line.replace("ue", "ü");
            lines.add(line);
        }
        meta.setLore(lines);
        result.setItemMeta(meta);
        return result;
    }

    public static Inventory fillUp(Inventory inventory)
    {
        Inventory result = inventory;
        ItemStack filler = createItem(" ", Material.STAINED_GLASS_PANE, 15, Collections.singletonList(""));
        for (int i = 0; i < result.getSize(); i += 1)
        {
            if (result.getItem(i) == null || result.getItem(i).getType() == Material.AIR)
                result.setItem(i, filler);
        }
        return result;
    }
}
