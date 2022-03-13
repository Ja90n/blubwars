package com.blub.blubwars.listener;

import com.blub.blubwars.Blubwars;
import com.blub.blubwars.instance.Arena;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class ItemListeners implements Listener {

    private Blubwars blubwars;

    public ItemListeners(Blubwars blubwars) {
        this.blubwars = blubwars;
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent e){
        if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
            Player player = e.getPlayer();
            if (player.getInventory().getItemInMainHand().getType().equals(Material.FIRE_CHARGE) ||
                    player.getInventory().getItemInOffHand().getType().equals(Material.FIRE_CHARGE)){
                Fireball fireball = player.launchProjectile(Fireball.class);
                fireball.setVelocity(player.getLocation().getDirection().multiply(2));
                fireball.setIsIncendiary(false);
                player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount()-1);
            }
        }
    }

    @EventHandler
    public void onExplosion(EntityExplodeEvent e){
        for (Arena arena : blubwars.getArenaManager().getArenas()){
            if (e.getEntity().getWorld().equals(arena.getWorld())){
                for (Block block : e.blockList()){
                    if (block.getType().equals(Material.RED_WOOL) || block.getType().equals(Material.BLUE_WOOL) ||
                            block.getType().equals(Material.GREEN_WOOL) || block.getType().equals(Material.PINK_WOOL)){
                        e.setCancelled(true);
                    }
                }
                for (Entity entity : e.getEntity().getNearbyEntities(5,5,5)){
                    Vector vector = entity.getLocation().toVector().subtract(e.getLocation().toVector()).multiply(5);
                    entity.setVelocity(vector);
                }
            }
        }
    }
}