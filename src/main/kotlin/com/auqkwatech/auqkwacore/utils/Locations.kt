package com.auqkwatech.auqkwacore.utils

import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block

fun ofBlockPosition(location: Location): BlockLocation {
    return BlockLocation(location.world!!.name, location.blockX, location.blockY, location.blockZ)
}

data class BlockLocation(val world: String, val x: Int, val y: Int, val z: Int) {

    fun toBukkitWorld(): World {
        return server.getWorld(world)!!
    }

    fun toBukkitBlock(): Block {
        return toBukkitWorld().getBlockAt(x, y, z)
    }

    fun toBukkitLocation(): Location {
        return toBukkitBlock().location
    }
}