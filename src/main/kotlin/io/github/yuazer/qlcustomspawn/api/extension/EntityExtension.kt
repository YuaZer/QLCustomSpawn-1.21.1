package io.github.yuazer.qlcustomspawn.api.extension

import net.minecraft.core.BlockPos
import org.bukkit.FluidCollisionMode
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

object EntityExtension {
    fun Entity.isCobblemon():Boolean{
        return this.type.key.namespace.equals("cobblemon",true)
    }
    /** 默认最大距离（方块数）。 */
    private const val DEFAULT_MAX_DISTANCE = 10

    /**
     * 精简封装：等价于 `player.getTargetBlockExact(...)` ，忽略流体。
     * @return 若未命中方块则返回 null
     */
    fun Player.getLookedBlock(
        maxDistance: Int = DEFAULT_MAX_DISTANCE
    ): Block? =
        getTargetBlockExact(maxDistance, FluidCollisionMode.NEVER)

    /**
     * “获取 Location” 的语法糖：直接返回方块的世界坐标。
     */
    fun Player.getLookedLocation(
        maxDistance: Int = DEFAULT_MAX_DISTANCE
    ): Location? = getLookedBlock(maxDistance)?.location
}