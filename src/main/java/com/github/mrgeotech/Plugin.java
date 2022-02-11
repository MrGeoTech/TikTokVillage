package com.github.mrgeotech;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class Plugin extends JavaPlugin implements Listener {

    private int count;
    private Iterator<String> names;

    @Override
    public void onEnable() {
        count = 0;
        try {
            names = getNames();
        } catch (IOException e) {
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
        }
        Bukkit.getPluginManager().registerEvents(this, this);
        Objects.requireNonNull(Bukkit.getWorld("Novigrad")).getEntities().forEach(Entity::remove);
    }

    @EventHandler
    public void onJoin(PlayerSpawnLocationEvent event) {
        if (Objects.requireNonNull(event.getSpawnLocation().getWorld()).getName().equalsIgnoreCase("Novigrad")) {
            World world = event.getSpawnLocation().getWorld();
            Plugin plugin = this;
            Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
                for (int i = 0; i < 900; i++) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                                Entity entity = world.spawnEntity(
                                        new Location(world,
                                                ThreadLocalRandom.current().nextDouble() * 600 + 500,
                                                256,
                                                ThreadLocalRandom.current().nextDouble() * 600 + 1000),
                                        EntityType.VILLAGER
                                );
                                entity.setInvulnerable(true);
                                if (!names.hasNext()) return;
                                entity.setCustomName(names.next());
                                entity.setCustomNameVisible(true);
                            });
                    count++;
                }
                for (int i = 0; i < 100; i++) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        Entity entity = world.spawnEntity(
                                new Location(world,
                                        800,
                                        256,
                                        1300),
                                EntityType.VILLAGER
                        );
                        entity.setInvulnerable(true);
                        if (!names.hasNext()) return;
                        entity.setCustomName(names.next());
                        entity.setCustomNameVisible(true);
                    });
                    count++;
                }
                event.getPlayer().sendMessage(count + " entities");
            }, 10, 10);
        }
    }

    private Iterator<String> getNames() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(getDataFolder().getAbsolutePath() + "/names.txt"));
        String line;
        List<String> names = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            if (!line.equalsIgnoreCase(""))
                names.add(line);
        }
        return names.iterator();
    }

}
