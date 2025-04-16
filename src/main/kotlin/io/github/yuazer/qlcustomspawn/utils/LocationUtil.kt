package io.github.yuazer.qlcustomspawn.utils

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import kotlin.random.Random

object LocationUtils {


    fun getCobblemonInArea(loc1: Location, loc2: Location): Int {
        // 两个点必须在同一个世界
        if (loc1.world != loc2.world) return 0

        val world = loc1.world ?: return 0

        // 获取边界范围
        val minX = minOf(loc1.x, loc2.x)
        val maxX = maxOf(loc1.x, loc2.x)
        val minY = minOf(loc1.y, loc2.y)
        val maxY = maxOf(loc1.y, loc2.y)
        val minZ = minOf(loc1.z, loc2.z)
        val maxZ = maxOf(loc1.z, loc2.z)

        // 过滤在该立方体范围内的实体
        return world.entities.count { entity ->
            val x = entity.location.x
            val y = entity.location.y
            val z = entity.location.z
            (entity.type.key.namespace.equals(
                "cobblemon",
                true
            )) && x in minX..maxX && y in minY..maxY && z in minZ..maxZ
        }
    }


    fun getRandomGroundLocation(loc1: Location, loc2: Location): Location? {
        val world: World = loc1.world ?: return null

        val minX = minOf(loc1.blockX, loc2.blockX)
        val maxX = maxOf(loc1.blockX, loc2.blockX)
        val minZ = minOf(loc1.blockZ, loc2.blockZ)
        val maxZ = maxOf(loc1.blockZ, loc2.blockZ)
        val minY = minOf(loc1.blockY, loc2.blockY)
        val maxY = maxOf(loc1.blockY, loc2.blockY)

        repeat(100) { // 最多尝试100次
            val x = Random.nextInt(minX, maxX + 1)
            val z = Random.nextInt(minZ, maxZ + 1)

            for (y in maxY downTo minY) {
                val current = Location(world, x.toDouble(), y.toDouble(), z.toDouble())
                val below = current.clone().subtract(0.0, 1.0, 0.0)
                val above = current.clone().add(0.0, 1.0, 0.0)

                if (
                    current.block.type == Material.AIR &&
                    above.block.type == Material.AIR &&
                    below.block.type.isSolid
                ) {
                    return current
                }
            }
        }

        return null // 找不到有效坐标
    }

}
