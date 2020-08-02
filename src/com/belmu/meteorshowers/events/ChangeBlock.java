package com.belmu.meteorshowers.events;

import com.belmu.meteorshowers.Countdown;
import com.belmu.meteorshowers.Main;
import com.belmu.meteorshowers.Meteors;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

public class ChangeBlock implements Listener {

    @EventHandler
    public void onChange(EntityChangeBlockEvent e) {

        if(!e.isCancelled()) {

            Entity entity = e.getEntity();
            Block b = e.getBlock();
            World world = b.getWorld();

            FileConfiguration cfg = Main.getInstance().getConfig();

            if (entity instanceof FallingBlock) {

                FallingBlock block = (FallingBlock) entity;

                if (Meteors.meteors.contains(block)) {

                    Meteors.meteors.remove(block);

                    Countdown effects = new Countdown(Main.getInstance(),
                            1,
                            () -> {

                            },
                            () -> {

                                Location bl = block.getLocation();

                                int radius = 2;

                                Location loc = new Location(world, bl.getX(), bl.getY() - 1, bl.getZ());

                                Material main = Material.GLOWSTONE;

                                loc.getBlock().setType(main);

                                for (int x = (loc.getBlockX() - radius); x <= (loc.getBlockX() + radius); x++) {

                                    for (int y = (loc.getBlockY() - radius); y <= (loc.getBlockY() + radius); y++) {

                                        for (int z = (loc.getBlockZ() - radius); z <= (loc.getBlockZ() + radius); z++) {

                                            Block around = world.getBlockAt(x, y, z);

                                            Material effect = Material.COAL_BLOCK;

                                            around.setType(effect);

                                            try {

                                                int number = cfg.getConfigurationSection("meteor-crash-blocks").getKeys(true).size() + 1;

                                                cfg.set("meteor-crash-blocks" + "." + "meteor_" + number + "." + "around" + "." + "material", effect.toString());
                                                cfg.set("meteor-crash-blocks" + "." + "meteor_" + number + "." + "around" + "." + "x", x);
                                                cfg.set("meteor-crash-blocks" + "." + "meteor_" + number + "." + "around" + "." + "y", y);
                                                cfg.set("meteor-crash-blocks" + "." + "meteor_" + number + "." + "around" + "." + "z", z);

                                                cfg.set("meteor-crash-blocks" + "." + "meteor_" + number + "." + "main-meteor" + "." + "material", main.toString());
                                                cfg.set("meteor-crash-blocks" + "." + "meteor_" + number + "." + "main-meteor" + "." + "x", x);
                                                cfg.set("meteor-crash-blocks" + "." + "meteor_" + number + "." + "main-meteor" + "." + "y", y);
                                                cfg.set("meteor-crash-blocks" + "." + "meteor_" + number + "." + "main-meteor" + "." + "z", z);

                                            } catch (Exception npe) {

                                                cfg.set("meteor-crash-blocks" + "." + "meteor_1" + "." + "around" + "." + "material", "COAL_BLOCK");
                                                cfg.set("meteor-crash-blocks" + "." + "meteor_1" + "." + "around" + "." + "x", x);
                                                cfg.set("meteor-crash-blocks" + "." + "meteor_1" + "." + "around" + "." + "y", y);
                                                cfg.set("meteor-crash-blocks" + "." + "meteor_1" + "." + "around" + "." + "z", z);

                                                cfg.set("meteor-crash-blocks" + "." + "meteor_1" + "." + "main-meteor" + "." + "material", main.toString());
                                                cfg.set("meteor-crash-blocks" + "." + "meteor_1" + "." + "main-meteor" + "." + "x", x);
                                                cfg.set("meteor-crash-blocks" + "." + "meteor_1" + "." + "main-meteor" + "." + "y", y);
                                                cfg.set("meteor-crash-blocks" + "." + "meteor_1" + "." + "main-meteor" + "." + "z", z);

                                            }

                                        }

                                    }

                                }

                            },
                            (t) -> {
                            }
                    );
                    effects.scheduleTimer();

                    block.remove();
                    b.setType(Material.AIR);

                    Main.getInstance().saveConfig();

                }

            }

        } else {

            e.getEntity().remove();

        }

    }

}
