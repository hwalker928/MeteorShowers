package com.belmu.meteorshowers;

import com.belmu.meteorshowers.commands.setAlert;
import com.belmu.meteorshowers.events.ChangeBlock;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Main extends JavaPlugin implements Listener {

    public static boolean ifMeteors = false;

    public static Map<Player, Location> meteorLocation = new HashMap<>();

    ////////////////////////////////////////
    //                                    //
    //     *Plugin developed by Belmu*    //
    //        *Twitter : @BelmuTM*        //
    //                                    //
    ////////////////////////////////////////

    /*

        Don't ever try to steal my sources you fool ʕ•ᴥ•ʔ

     */

    public static String prefix = "§8[§6Meteor§8]";

    public static Main instance;

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {

        Bukkit.getPluginManager().registerEvents(this, this);
        instance = this;
        
        Bukkit.getPluginManager().registerEvents(new ChangeBlock(), this);

        getCommand("setalert").setExecutor(new setAlert());

        FileConfiguration cfg = Main.getInstance().getConfig();

        if (cfg.get("alert-message") == null) {

            cfg.set("alert-message", "§4§k|!| §6Meteors§7 incoming!§4§k |!|");

        }

        if (cfg.get("meteor-worlds") == null) {

            cfg.set("meteor-world", "world");

        }

        if (cfg.get("meteor-interval") == null) {

            cfg.set("meteor-interval" + "." + "min", 5);
            cfg.set("meteor-interval" + "." + "max", 10);

        }

        if (cfg.get("meteor-duration") == null) {

            cfg.set("meteor-duration", 60);

        }

        Main.getInstance().saveConfig();

        try {

            World world = Bukkit.getWorld(cfg.get("meteor-world").toString());

            int min = cfg.getInt("meteor-interval" + "." + "min");
            int max = cfg.getInt("meteor-interval" + "." + "max");

            Random r = new Random();
            int upper = ((max - min) + 1) + min; //((max - min) + 1) + min;

            long ticks = (r.nextInt(upper) * 60) * 20;

            new BukkitRunnable() {

                @Override
                public void run() {

                    try {

                        Random r = new Random();
                        Player p = world.getPlayers().get(r.nextInt(getPlayers(world).size()));

                        Countdown meteorShower = new Countdown(Main.getInstance(),
                                cfg.getInt("meteor-duration"),
                                () -> {

                                    ifMeteors = true;

                                    int upper = ((3 - 1) + 1) + 1; //((max - min) + 1) + min;
                                    int radius = getRadius(upper);
                                    int radiusAlert = radius + 25;

                                    int height = 200;

                                    meteorLocation.put(p, p.getLocation());

                                    Location pLoc = meteorLocation.get(p);

                                    double x1 = pLoc.getX() + radius;
                                    double z1 = pLoc.getZ() + radius;

                                    double x2 = pLoc.getX() - radius;
                                    double z2 = pLoc.getZ() - radius;

                                    Location loc1 = new Location(world, x1, height, z1);
                                    Location loc2 = new Location(world, x2, height, z2);

                                    new BukkitRunnable() {

                                        @Override
                                        public void run() {

                                            if (ifMeteors) {

                                                for (Entity player : getNearbyEntities(loc1.add(loc1.subtract(loc2).multiply(0.5)), radiusAlert)) {

                                                    if (player instanceof Player) {

                                                        sendPacket(((Player) player), cfg.get("alert-message").toString());
                                                        ((Player) player).playSound(player.getLocation(), Sound.NOTE_PLING, 1f, 0.03f);

                                                    }

                                                }

                                            }

                                        }

                                    }.runTaskTimer(Main.getInstance(), 0, 60);

                                    new BukkitRunnable() {

                                        @Override
                                        public void run() {

                                            if (!ifMeteors) {

                                                this.cancel();

                                            }

                                            Meteors.fallMeteor(world, randomLocation(loc1, loc2));

                                        }

                                    }.runTaskTimer(Main.getInstance(), 0, 90);

                                },
                                () -> {

                                    ifMeteors = false;
                                    meteorLocation.remove(p);

                                },
                                (t) -> {
                                }
                        );
                        meteorShower.scheduleTimer();

                    } catch(NullPointerException npe) {

                        return;

                    }

                }

            }.runTaskTimer(Main.getInstance(), 0, ticks);

        } catch (NullPointerException npe) {

            System.out.println("Error: Could not find the indicated world.");

        }

        saveConfig();

    }

    public static int getRadius(int upper) {

        if(upper == 1) {

            return 15;

        } else if(upper == 2) {

            return 20;

        } else if(upper == 3) {

            return 30;

        }

        return 0;

    }

    public static void sendPacket(Player p, String text) {

        PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + text + "\"}"), (byte) 2);
        ((CraftPlayer)p).getHandle().playerConnection.sendPacket(packet);

    }

    public static Entity[]  getNearbyEntities(Location l, int radius){

        int chunkRadius = radius < 16 ? 1 : (radius - (radius % 16))/16;

        HashSet<Entity> radiusEntities = new HashSet<Entity>();

        for (int chX = -chunkRadius; chX <= chunkRadius; chX ++){

            for (int chZ = -chunkRadius; chZ <= chunkRadius; chZ++){

                int x=(int) l.getX(), y=(int) l.getY(), z=(int) l.getZ();

                for (Entity e : new Location(l.getWorld(),x+(chX*16),y,z+(chZ*16)).getChunk().getEntities()){

                    if (e.getLocation().distance(l) <= radius && e.getLocation().getBlock() != l.getBlock()) radiusEntities.add(e);

                }

            }

        }

        return radiusEntities.toArray(new Entity[radiusEntities.size()]);

    }

    Location randomLocation(Location min, Location max) {

        Location range = new Location(min.getWorld(), Math.abs(max.getX() - min.getX()), min.getY(), Math.abs(max.getZ() - min.getZ()));
        return new Location(min.getWorld(), (Math.random() * range.getX()) + (min.getX() <= max.getX() ? min.getX() : max.getX()), range.getY(), (Math.random() * range.getZ()) + (min.getZ() <= max.getZ() ? min.getZ() : max.getZ()));

    }

    public static List<Player> getPlayers(World world) {

        List<Player> players = new ArrayList<>();

        for(Player player : world.getPlayers()) {

            players.add(player);

        }

        if(players.size() > 0) {

            return players;

        } else {

            return null;

        }

    }

}
