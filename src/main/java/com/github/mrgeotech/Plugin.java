package com.github.mrgeotech;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class Plugin extends JavaPlugin implements CommandExecutor {

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
        Bukkit.getPluginCommand("start").setExecutor(this);
        Bukkit.getPluginCommand("killvillagers").setExecutor(this);
        Objects.requireNonNull(Bukkit.getWorld("Novigrad")).getEntities().forEach(Entity::remove);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        final Player player = (Player) sender;
        if (label.equalsIgnoreCase("start")) {
            if (player.getWorld().getName().equalsIgnoreCase("Novigrad")) {
                Plugin plugin = this;
                Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
                    for (int i = 0; i < 50; i++) {
                        Bukkit.getScheduler().runTask(plugin, () -> {
                            Entity entity = player.getWorld().spawnEntity(
                                    new Location(player.getWorld(),
                                            800,
                                            230,
                                            1300),
                                    EntityType.VILLAGER
                            );
                            entity.setInvulnerable(true);
                            if (!names.hasNext()) return;
                            String name = names.next();
                            switch (name) {
                                case "Zander":
                                    name = ChatColor.RED + "" + ChatColor.BOLD + name;
                                    break;
                                case "MrGeoTech":
                                    name = ChatColor.BLUE + "" + ChatColor.BOLD + name;
                                    break;
                                case "FourSeasonsHosting":
                                    name = ChatColor.GREEN + "" + ChatColor.BOLD + name;
                                    break;
                            }
                            entity.setCustomName(name);
                            entity.setCustomNameVisible(true);
                        });
                        count++;
                    }
                    for (int i = 0; i < 950; i++) {
                        Bukkit.getScheduler().runTask(plugin, () -> {
                            Entity entity = player.getWorld().spawnEntity(
                                    new Location(player.getWorld(),
                                            ThreadLocalRandom.current().nextDouble() * 300 + 650,
                                            256,
                                            ThreadLocalRandom.current().nextDouble() * 300 + 1150),
                                    EntityType.VILLAGER
                            );
                            entity.setInvulnerable(true);
                            if (!names.hasNext()) return;
                            entity.setCustomName(names.next());
                            entity.setCustomNameVisible(true);
                        });
                        count++;
                    }
                    player.sendMessage(count + " entities");
                }, 10, 10);
            }
        } else {
            player.getWorld().getEntities().forEach(Entity::remove);
        }
        return true;
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
