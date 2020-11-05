package dev.sabel.i.menuitem.events;

import dev.sabel.i.menuitem.MenuItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;

public class BlockedMenuItemEvents implements Listener {

    private final MenuItem plugin;

    public BlockedMenuItemEvents(MenuItem p) {
        plugin = p;
        ArrayList<Material> arrows = new ArrayList<>();
        arrows.add(Material.ARROW);
        arrows.add(Material.SPECTRAL_ARROW);
        arrows.add(Material.TIPPED_ARROW);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () ->
            Bukkit.getOnlinePlayers().forEach(x -> {
                PlayerInventory inv = x.getInventory();
                if (inv.getItem(8) == null || !(inv.getItem(8).equals(p.itemInstance) || inv.getItem(8).equals(p.blockedItem))) inv.setItem(8, p.itemInstance);
                if (!arrows.contains(p.itemInstance.getType()) && !(p.itemInstance.getType() == Material.FIREWORK_ROCKET)) return;
                if (inv.getItemInMainHand().getType() == Material.BOW
                   || inv.getItemInOffHand().getType() == Material.BOW) {
                    if (arrows.contains(p.itemInstance.getType())) {
                       if (!inv.getItem(8).equals(p.blockedItem)) inv.setItem(8, p.blockedItem);
                       return;
                    }
                }
                try {
                    if (inv.getItemInMainHand().getType() == Material.CROSSBOW
                        || inv.getItemInOffHand().getType() == Material.CROSSBOW) {
                        if (!arrows.contains(p.itemInstance.getType()) && !(p.itemInstance.getType() == Material.FIREWORK_ROCKET)) return;
                        if (inv.getItemInOffHand().getType() == Material.CROSSBOW && (inv.getItemInMainHand().equals(p.itemInstance) || inv.getItemInMainHand().equals(p.blockedItem))) {
                            if (!inv.getItem(8).equals(p.blockedItem)) inv.setItem(8, p.blockedItem);
                            return;
                        }
                        if (arrows.contains(p.itemInstance.getType())) {
                            if (!inv.getItem(8).equals(p.blockedItem)) inv.setItem(8, p.blockedItem);
                            return;
                        }
                    }
                } catch (NoSuchFieldError ignored) {}
                if (!inv.getItem(8).equals(p.itemInstance)) inv.setItem(8, p.itemInstance);
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
        if (e.getClick() == ClickType.NUMBER_KEY) {
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
