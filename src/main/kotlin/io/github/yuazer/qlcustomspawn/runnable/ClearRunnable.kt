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

class ClearRunnable(private val mode: String) : BukkitRunnable() {

    private val cfg = Qlcustomspawn.config
    private val periodSeconds = cfg.getInt("clear.period").coerceAtLeast(1)
    private val waitSeconds = cfg.getInt("clear.wait_time")
    private val clearConditions = cfg.getStringList("clear.conditions")
    private val cleanWorlds = cfg.getStringList("clear.clean_worlds")
        .mapNotNull(Bukkit::getWorld)                // 提前转换成 World；null 的世界直接过滤
        .toSet()

    var counter = periodSeconds
        get() = field
    private val candidates = mutableSetOf<UUID>()   // 待检测实体 UUID

    override fun run() {
        if (!mode.equals("wait", ignoreCase = true)) return

        // 收集存活 ≥ waitSeconds 且尚未进入候选集的实体
        cleanWorlds.flatMap { it.entities }
            .filter { it.isCobblemon() && it.uniqueId !in candidates }
            .forEach { entity ->
                val livedSeconds = entity.ticksLived / 20
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
        // 对候选集做一次真正清理
        if (--counter <= 0) {
            var clearCount = 0
            counter = periodSeconds                               // 重置周期

            // 复制一份
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
                //不知道为什么0秒的有时候执行有时候不执行,先并入主线程提交
                submit {
                    onlinePlayers.forEach { player ->
                        kether.replace(Pair("%count%", clearCount.toString())).runKether(player)
                    }
//                    cancel()
                }
            }
        }
    }
}
