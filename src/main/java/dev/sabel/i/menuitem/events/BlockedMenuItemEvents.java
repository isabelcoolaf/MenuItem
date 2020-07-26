package dev.sabel.i.menuitem.events;

import dev.sabel.i.menuitem.MenuItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class BlockedMenuItemEvents implements Listener {

    private final MenuItem plugin;

    public BlockedMenuItemEvents(MenuItem p) {
        plugin = p;
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () ->
            Bukkit.getOnlinePlayers().forEach(x -> {
            PlayerInventory inv = x.getInventory();
            if (inv.getItemInMainHand().getType().equals(Material.BOW)
                    || inv.getItemInMainHand().getType().equals(Material.CROSSBOW)
                    || inv.getItemInOffHand().getType().equals(Material.BOW)
                    || inv.getItemInOffHand().getType().equals(Material.CROSSBOW)) {
                if (!Tag.ITEMS_ARROWS.getValues().contains(p.itemInstance.getType())) return;
                inv.setItem(8, p.blockedItem);
                return;
            }
            if (inv.getItem(8) == null || !inv.getItem(8).equals(p.itemInstance)) inv.setItem(8, p.itemInstance);
        }), 0, 0);
    }

    private boolean testItem(ItemStack i) {
        try {
            return (i.equals(plugin.itemInstance) || i.equals(plugin.blockedItem));
        } catch (NullPointerException e) {
            return false;
        }
    }

    @EventHandler
    public void onSwapHands(PlayerSwapHandItemsEvent e) {
        if (e.getOffHandItem() == null) return;

        if (testItem(e.getOffHandItem())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        if (e.getClick().equals(ClickType.NUMBER_KEY)) {
            if (e.getHotbarButton() == 8 || testItem(e.getCurrentItem())) {
                e.setCancelled(true);
                return;
            }
        }
        if (testItem(e.getCurrentItem())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInvDrag(InventoryDragEvent e) {
        if (testItem(e.getOldCursor())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemMove(InventoryMoveItemEvent e) {
        if (testItem(e.getItem())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if (testItem(e.getItemDrop().getItemStack())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        e.getDrops().remove(plugin.itemInstance);
        e.getDrops().remove(plugin.blockedItem);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        e.getPlayer().getInventory().setItem(8, plugin.itemInstance);
    }

}
