package io.github.yuazer.qlcustomspawn.api.extension

import org.bukkit.Bukkit
import org.bukkit.Location

object LocationExtension {
    /**
     * 将 Location 转换为字符串格式，格式如下：
     * world_name,x,y,z,yaw,pitch
     */
    fun Location.locToString(): String {
        return "${this.world?.name}," +
                "${this.x}," +
                "${this.y}," +
                "${this.z}," +
                "${this.yaw}," +
                "${this.pitch}"
    }

    /**
     * 从字符串格式还原 Location，如果格式错误或 world 不存在则返回 null。
     */
    fun String.toLocation(): Location? {
        val parts = this.split(",")
        if (parts.size < 6) {
            println("参数数量少于6")
            return null
        }

        val world = Bukkit.getWorld(parts[0])
        if (world == null) {
            println("世界不存在")
            return null
        }
        val x = parts[1].toDoubleOrNull()
        if (x == null) {
            println("x坐标错误")
            return null
        }
        val y = parts[2].toDoubleOrNull()
        if (y == null) {
            println("y坐标错误")
            return null
        }
        val z = parts[3].toDoubleOrNull()
        if (z == null) {
            println("z坐标错误")
            return null
        }
        val yaw = parts[4].toFloatOrNull() ?: 0f
        val pitch = parts[5].toFloatOrNull() ?: 0f

        return Location(world, x, y, z, yaw, pitch)
    }
}