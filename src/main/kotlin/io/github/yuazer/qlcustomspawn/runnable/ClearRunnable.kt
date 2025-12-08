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
import taboolib.platform.util.onlinePlayers
import java.util.LinkedHashSet
import java.util.UUID

class ClearRunnable(private val mode: String) : BukkitRunnable() {

    private val cfg = Qlcustomspawn.config
    private val periodSeconds = cfg.getInt("clear.period").coerceAtLeast(1)
    private val waitSeconds = cfg.getInt("clear.wait_time")
    private val clearConditions = cfg.getStringList("clear.conditions")
    private val cleanWorlds = cfg.getStringList("clear.clean_worlds")
        .mapNotNull(Bukkit::getWorld)
        .toSet()

    var counter = periodSeconds
        private set

    private val candidates = LinkedHashSet<UUID>()

    override fun run() {
        submit {
            tick()
        }
    }

    private fun tick() {
        collectCandidates()
        notifyCountdown()

        if (--counter > 0) {
            return
        }

        val clearCount = clearCandidates()
        counter = periodSeconds
        dispatchClearMessage(clearCount)
    }

    private fun collectCandidates() {
        cleanWorlds.flatMap { it.entities }
            .filter { it.isCobblemon() && it.uniqueId !in candidates }
            .forEach { entity ->
                if (entity.ticksLived / 20 >= waitSeconds) {
                    candidates += entity.uniqueId
                }
            }
    }

    private fun notifyCountdown() {
        val timeList = cfg.getConfigurationSection("clear.kether")?.getKeys(false) ?: return
        if (!timeList.contains(counter.toString()) || counter == 0) return

        val kether = cfg.getStringList("clear.kether.$counter")
        onlinePlayers.forEach { player ->
            kether.runKether(player)
        }
    }

    private fun clearCandidates(): Int {
        var clearCount = 0
        val iterator = candidates.iterator()

        while (iterator.hasNext()) {
            val uid = iterator.next()
            val bukkitEntity = Bukkit.getEntity(uid)

            if (bukkitEntity == null || !bukkitEntity.isCobblemon()) {
                iterator.remove()
                continue
            }

            val pokemon = bukkitEntity.getNMSEntity() as? PokemonEntity
            if (pokemon == null) {
                iterator.remove()
                continue
            }

            val conditionParser = ConditionParser(pokemon.pokemon)
            val shouldKeep = try {
                conditionParser.parse(clearConditions, any = true) ||
                        pokemon.isBusy ||
                        pokemon.isBattling ||
                        pokemon.pokemon.isPlayerOwned()
            } catch (e: Exception) {
                e.printStackTrace()
                true
            }

            if (!shouldKeep) {
                clearCount++
                if (pokemon.isAlive) {
                    pokemon.remove(RemovalReason.KILLED)
                }
            }
            iterator.remove()
        }

        return clearCount
    }

    private fun dispatchClearMessage(clearCount: Int) {
        val kether = cfg.getStringList("clear.kether.0")
        if (kether.isEmpty()) return

        submit {
            onlinePlayers.forEach { player ->
                kether.replace("%count%" to clearCount.toString()).runKether(player)
            }
        }
    }
}
