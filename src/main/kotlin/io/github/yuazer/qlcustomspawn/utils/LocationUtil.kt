package io.github.yuazer.qlcustomspawn.utils

import io.github.yuazer.qlcustomspawn.api.extension.EntityExtension.isCobblemon
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Entity
import kotlin.random.Random

object LocationUtils {
    fun Entity.isInArea(loc1: Location, loc2: Location): Boolean {
        return this.location.isInArea(loc1, loc2)
    }
    fun Location.isInArea(loc1: Location, loc2: Location): Boolean {
        if (this.world==null||loc1.world==null||loc2.world==null) {
            println("两个点必须在同一个世界")
            return false
        }
        val minX = minOf(loc1.x, loc2.x)
        val maxX = maxOf(loc1.x, loc2.x)
        val minY = minOf(loc1.y, loc2.y)
        val maxY = maxOf(loc1.y, loc2.y)
        val minZ = minOf(loc1.z, loc2.z)
        val maxZ = maxOf(loc1.z, loc2.z)
        return this.x in minX..maxX && this.y in minY..maxY && this.z in minZ..maxZ
    }
    fun getCobblemonInArea(loc1: Location, loc2: Location): Int {
        // 两个点必须在同一个世界
        if (loc1.world==null||loc2.world==null) {
            println("两个点必须在同一个世界1")
            return 0
        }
        if (!loc1.world!!.name.equals(loc2.world!!.name,true) ){
            println("两个点必须在同一个世界2")
            return 0
        }

        val world = loc1.world
        if (world==null){
            println("loc1世界为空")
            return 0
        }

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
            entity.isCobblemon() && x in minX..maxX && y in minY..maxY && z in minZ..maxZ
        }
    }


    fun getRandomGroundLocation(
        loc1: Location,
        loc2: Location
    ): Location? {
        val world = loc1.world ?: return null

        val minX = minOf(loc1.blockX, loc2.blockX)
        val maxX = maxOf(loc1.blockX, loc2.blockX)
        val minZ = minOf(loc1.blockZ, loc2.blockZ)
        val maxZ = maxOf(loc1.blockZ, loc2.blockZ)
        val minY = minOf(loc1.blockY, loc2.blockY)
        val maxY = maxOf(loc1.blockY, loc2.blockY)

        fun isLava(type: Material) = type == Material.LAVA

        fun isDangerous(type: Material): Boolean = when (type) {
            Material.LAVA,
            Material.FIRE,
            Material.SOUL_FIRE,
            Material.CAMPFIRE,
            Material.SOUL_CAMPFIRE,
            Material.MAGMA_BLOCK,
            Material.CACTUS,
            Material.SWEET_BERRY_BUSH -> true
            else -> false
        }

        /** 玩家身体是否能占据这个方块 */
        fun isBodySafe(type: Material): Boolean {
            if (isDangerous(type)) return false
            return type == Material.AIR || type == Material.WATER
        }

        /** 脚下是否是安全支撑 */
        fun isSafeGround(type: Material): Boolean {
            if (isDangerous(type)) return false
            return type.isSolid || type == Material.WATER
        }

        // 明确：最多随机 100 次 XZ
        repeat(100) {
            val x = Random.nextInt(minX, maxX + 1)
            val z = Random.nextInt(minZ, maxZ + 1)

            // 从高到低找地面
            for (y in maxY downTo minY) {
                val feet = Location(world, x.toDouble(), y.toDouble(), z.toDouble())
                val head = feet.clone().add(0.0, 1.0, 0.0)
                val below = feet.clone().subtract(0.0, 1.0, 0.0)

                val feetType = feet.block.type
                val headType = head.block.type
                val belowType = below.block.type

                if (!isBodySafe(feetType)) continue
                if (!isBodySafe(headType)) continue
                if (!isSafeGround(belowType)) continue

                // 返回方块中心点，体验更好
                return feet.add(0.5, 0.0, 0.5)
            }
        }

        return null
    }


}
