package dev.sabel.i.menuitem.events;

import dev.sabel.i.menuitem.MenuItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class ItemInteract implements Listener {

    private final MenuItem plugin;

    public ItemInteract(MenuItem p) { plugin = p; }

    private boolean testItem(ItemStack i) {
        try {
            return (i.equals(plugin.itemInstance) || i.equals(plugin.blockedItem));
        } catch (NullPointerException e) {
            return false;
        }
    }

    @EventHandler
    public void interactEvent(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        ItemStack i = e.getItem();
        if (e.getHand() == null || e.getHand().equals(EquipmentSlot.OFF_HAND)) return;
        if (testItem(i)) {
            e.setCancelled(true);
            final boolean opStatus = p.isOp();
            if (!opStatus) p.setOp(true);
            try {
                p.chat("/" + plugin.config.getString("ItemAction"));
            } catch (RuntimeException ignored) {}
            finally {
                if (!opStatus) p.setOp(false);
            }
        }
    }

    @EventHandler
    public void armorStandInteract(PlayerArmorStandManipulateEvent e) {
        Player p = e.getPlayer();
        if (testItem(e.getPlayerItem())) {
            e.setCancelled(true);
            final boolean opStatus = p.isOp();
            p.setOp(true);
            try {
                p.chat("/" + plugin.config.getString("ItemAction"));
            } catch (RuntimeException ignored) {}
            finally {
                p.setOp(opStatus);
            }
        }
    }

}
