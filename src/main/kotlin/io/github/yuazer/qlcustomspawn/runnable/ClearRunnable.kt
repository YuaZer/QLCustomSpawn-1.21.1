package io.github.yuazer.qlcustomspawn.runnable

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import io.github.yuazer.qlcustomspawn.Qlcustomspawn
import io.github.yuazer.qlcustomspawn.api.extension.EntityExtension.isCobblemon
import io.github.yuazer.qlcustomspawn.api.extension.NMSExtension.getNMSEntity
import io.github.yuazer.qlcustomspawn.api.extension.PlayerExtension.runKether
import io.github.yuazer.qlcustomspawn.utils.ConditionParser
import net.minecraft.world.entity.Entity.RemovalReason
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable
import taboolib.common.platform.function.submit
import taboolib.common5.util.replace
import taboolib.platform.BukkitPlugin
import taboolib.platform.util.onlinePlayers
import java.util.*

/**
 * 服务器定时清理 Cobblemon 实体：
 * 1. 先把“活得太久”的实体记入候选集合；
 * 2. 每隔 [periodSeconds] 触发一次真正检测 -> 移除不符合条件的实体。
 *
 * @param mode 仅在 "wait" 时生效（保留你原来的开关）
 */
class ClearRunnable(private val mode: String) : BukkitRunnable() {

    // —— 只读配置：一次性取出来，避免每 tick 读 Yaml ——
    private val cfg = Qlcustomspawn.config
    private val periodSeconds = cfg.getInt("clear.period").coerceAtLeast(1)
    private val waitSeconds = cfg.getInt("clear.wait_time")
    private val clearConditions = cfg.getStringList("clear.conditions")
    private val cleanWorlds = cfg.getStringList("clear.clean_worlds")
        .mapNotNull(Bukkit::getWorld)                // 提前转换成 World；null 的世界直接过滤
        .toSet()

    // —— 运行期状态 ——
    var counter = periodSeconds
        get() = field// 距下次“真正清理”的倒计时
    private val candidates = mutableSetOf<UUID>()   // 待检测实体 UUID

    override fun run() {
        if (!mode.equals("wait", ignoreCase = true)) return

        // 1. 收集“存活 ≥ waitSeconds” 且尚未进入候选集的实体
        cleanWorlds.flatMap { it.entities }
            .filter { it.isCobblemon() && it.uniqueId !in candidates }
            .forEach { entity ->
                val livedSeconds = entity.ticksLived / 20       // Bukkit 侧 ticksLived 足够用
                if (livedSeconds >= waitSeconds) {
                    candidates += entity.uniqueId
                }
            }
        val timeList = Qlcustomspawn.config.getConfigurationSection("clear.kether")?.getKeys(false)
        timeList?.let {
            if (it.isNotEmpty() && it.contains(counter.toString())) {
                if (counter == 0) return@let
                val kether = Qlcustomspawn.config.getStringList("clear.kether.${counter}")
                onlinePlayers.forEach { player ->
                    kether.runKether(player)
                }
            }
        }
        // 2. 每到周期终点，对候选集做一次真正清理
        if (--counter <= 0) {
            var clearCount = 0
            counter = periodSeconds                               // 重置周期

            // ⚠️ 防止 ConcurrentModification：复制一份遍历
            val snapshot = candidates.toList()
            for (uid in snapshot) {
                val bukkitEntity = Bukkit.getEntity(uid)
                if (bukkitEntity == null || !bukkitEntity.isCobblemon()) {
                    candidates -= uid
                    continue
                }

                val pokemon = bukkitEntity.getNMSEntity() as PokemonEntity
                val conditionParser = ConditionParser(pokemon.pokemon)

                val shouldKeep = try {
                    conditionParser.parse(clearConditions, any = true)||pokemon.isBusy||pokemon.isBattling||pokemon.pokemon.isPlayerOwned()
                } catch (e: Exception) {
                    e.printStackTrace()
                    true // 出现异常时保留实体，避免误杀
                }

                if (!shouldKeep) {
                    clearCount++
                    Bukkit.getScheduler().runTask(BukkitPlugin.getInstance(), Runnable {
                        if (pokemon.isAlive) {
                            pokemon.remove(RemovalReason.KILLED)
                        }
                    })
                }
                candidates -= uid
            }
            val kether = Qlcustomspawn.config.getStringList("clear.kether.0")
            if (kether.isNotEmpty()) {
                onlinePlayers.forEach { player ->
                    kether.replace(Pair("%count%", clearCount.toString())).runKether(player)
                }
            }
        }
    }
}
