package com.blub.blubwars.instance;

import com.blub.blubwars.GameState;
import com.blub.blubwars.manager.ConfigManager;
import com.blub.blubwars.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.entity.Cat;
import org.bukkit.entity.EntityType;

import java.time.chrono.JapaneseEra;
import java.util.HashMap;
import java.util.UUID;

public class Game {

    private HashMap<UUID, Integer> catLives;
    private Arena arena;

    public Game(Arena arena) {
        this.arena = arena;
        catLives = new HashMap<>();
    }

    public void start() {
        arena.setState(GameState.LIVE);
        arena.sendMessage(ChatColor.AQUA + "Game has started!");
        arena.sendTitle(ChatColor.GRAY + "Game has started!", " ");
        for (Team team : Team.values()){
            if (team != null){
                if (arena.getTeamCount(team) > 0){
                    spawnCat(team);
                }
            }
        }
    }

    public void spawnCat(Team team){
        Cat cat = (Cat) arena.getWorld().spawnEntity(arena.getTeamSpawn(team), EntityType.CAT);
        catLives.put(cat.getUniqueId(), ConfigManager.getCatLives());
        DyeColor dyeColor = DyeColor.WHITE;
        switch (team){
            case RED:
                dyeColor = DyeColor.RED;
                break;
            case BLUE:
                dyeColor = DyeColor.BLUE;
                break;
            case GREEN:
                dyeColor = DyeColor.GREEN;
                break;
            case PINK:
                dyeColor = DyeColor.PINK;
                break;
        }
        cat.setCollarColor(dyeColor);
        cat.setCustomName(team.getDisplay() + " cat, lives: " + catLives.get(cat.getUniqueId()));
    }

    public void respawnCat(UUID catUUID){
        catLives.replace(catUUID, catLives.get(catUUID)-1);
        Cat respawnedCat = (Cat) Bukkit.getEntity(catUUID);
        if (catLives.get(catUUID) > 1){
            Cat newCat = (Cat) arena.getWorld().spawnEntity(arena.getTeamSpawn(arena.getTeam(catUUID)), EntityType.CAT);
            newCat.setCollarColor(respawnedCat.getCollarColor());
            newCat.setCustomName(arena.getTeam(catUUID).getDisplay() + " cat, lives: " + catLives.get(catUUID));
        } else {
            catLives.remove(catUUID);
            if (catLives.size() < 2){
                for (UUID target : catLives.keySet()){
                    arena.sendMessage(ChatColor.AQUA + "Team " + arena.getTeam(target).getDisplay() +
                            ChatColor.AQUA + " has won!");
                    arena.sendTitle(ChatColor.AQUA + "Team " + arena.getTeam(target).getDisplay() +
                            ChatColor.AQUA + " has won!", ChatColor.GRAY + "Thank you for playing!");
                }
                arena.reset();
            } else {
                arena.sendMessage(arena.getTeam(catUUID).getDisplay() + ChatColor.AQUA + " teams cat has been eliminated!");
            }
        }
    }

    public HashMap<UUID,Integer> getCatLives() { return catLives; }
    public void clearCatLives() {  catLives.clear(); }
}
