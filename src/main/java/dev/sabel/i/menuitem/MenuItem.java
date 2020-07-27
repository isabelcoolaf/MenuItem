package dev.sabel.i.menuitem;

import dev.sabel.i.menuitem.events.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class MenuItem extends JavaPlugin {

    public ItemStack itemInstance;
    public ItemStack blockedItem;
    public FileConfiguration config;

    @Override
    public void onEnable() {
        loadConfig();

        blockedItem = new ItemStack(Material.BARRIER);
        ItemMeta blmeta = blockedItem.getItemMeta();
        assert blmeta != null;
        blmeta.setDisplayName(ChatColor.RED + "Blocked!");
        List<String> bllore = Arrays.asList(ChatColor.GRAY + "You are currently holding a bow!", ChatColor.GRAY + "Hold a different item to restore the menu item.");
        blmeta.setLore(bllore);
        blockedItem.setItemMeta(blmeta);

        PluginCommand reloadCommand = getCommand("menuitemreload");
        assert reloadCommand != null;
        reloadCommand.setExecutor(new ReloadCommand(this));
        Bukkit.getPluginManager().registerEvents(new BlockedMenuItemEvents(this), this);
        Bukkit.getPluginManager().registerEvents(new ItemInteract(this), this);

    }

    protected boolean loadConfig() {
        reloadConfig();
        FileConfiguration oldConfig = config;
        config = getConfig();
        config.options().copyDefaults(true);
        saveDefaultConfig();

        final String itemMatString = config.getString("ItemMaterial");
        final boolean itemGlowing = config.getBoolean("ItemGlowing");
        final String itemName = config.getString("ItemName");
        final List<String> loreStringList = config.getStringList("ItemLore");
        final String itemAction = config.getString("ItemAction");
        final int customModelInt = config.getInt("ItemCustomModelData");

        if (itemMatString == null || itemName == null || itemAction == null) {
            if (oldConfig != null) {
                getLogger().warning("Invalid Config when trying to reload!");
                config = oldConfig;
                return false;
            }
            getLogger().severe("Invalid Config on load. Disabling...");
            getServer().getPluginManager().disablePlugin(this);
            return false;
        }

        final List<String> itemLore = new ArrayList<>(loreStringList.size());
        loreStringList.forEach(x -> itemLore.add(ChatColor.translateAlternateColorCodes('&', x)));

        Material itemMat = Material.getMaterial(itemMatString);

        if (itemMat == null) {
            getLogger().warning("Invalid Configured Material. Defaulting to NETHER_STAR. Please check your config.");
            itemMat = Material.NETHER_STAR;
        }

        itemInstance = new ItemStack(itemMat);
        ItemMeta menuMeta = itemInstance.getItemMeta();
        assert menuMeta != null;
        menuMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', itemName));
        menuMeta.setUnbreakable(true);
        menuMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);
        if (itemLore.size() != 0) menuMeta.setLore(itemLore);
        try {
            if (customModelInt != 0) menuMeta.setCustomModelData(customModelInt);
        } catch (NoSuchMethodError ignored) {
            getLogger().warning("This version of MC does not support custom model data! Ignored your config input.");
        }

        itemInstance.setItemMeta(menuMeta);
        if (itemGlowing) itemInstance.addUnsafeEnchantment(Enchantment.DURABILITY, 1);

        return true;
    }
}
