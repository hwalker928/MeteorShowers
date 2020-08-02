package com.belmu.meteorshowers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftFallingSand;
import org.bukkit.entity.FallingBlock;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class Meteors {

    public static List<FallingBlock> meteors = new ArrayList<>();

    public static void fallMeteor(World world, Location location) {

        CraftFallingSand meteor = (CraftFallingSand) world.spawnFallingBlock(location, Material.GLOWSTONE, (byte) 0);

        meteor.setDropItem(false);
        meteor.setVelocity(new Vector(0, 0, 25));
        meteor.setFireTicks(9999);

        meteors.add(meteor);

    }

}
