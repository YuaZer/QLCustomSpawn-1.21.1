package io.github.yuazer.qlcustomspawn.obj.runnable

import io.github.yuazer.qlcustomspawn.Qlcustomspawn
import io.github.yuazer.qlcustomspawn.api.data.CreaterApi
import io.github.yuazer.qlcustomspawn.api.extension.CobbleExtension.createPokemon
import io.github.yuazer.qlcustomspawn.api.extension.LocationExtension.toLocation
import io.github.yuazer.qlcustomspawn.utils.LocationUtils
import io.github.yuazer.qlcustomspawn.utils.RandomUtils
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.scheduler.BukkitRunnable
import taboolib.module.configuration.Configuration
import taboolib.platform.BukkitPlugin
import top.maplex.arim.Arim

/**
 * 负责读取配置并定时生成宝可梦的容器。
 */
class SpawnContainer private constructor(
    val name: String,
     val area: SpawnArea,
     val periodSeconds: Int,
     val spawner: Map<String, Double>,
     val conditions: List<String>
) : BukkitRunnable() {

    private val logger = BukkitPlugin.getInstance().logger
    private val debugEnabled
        get() = Qlcustomspawn.config.getBoolean("debug", false)
     var countDown = periodSeconds

    override fun run() {
        if (--countDown > 0) {
            return
        }

        countDown = periodSeconds

        if (!area.isWorldAvailable()) {
            logger.warning("[QLCustomSpawn] 容器 $name 所在世界未就绪，跳过本次生成")
            return
        }

        if (!canSpawn()) {
            return
        }

        val createrKey = RandomUtils.pickByWeight(spawner)
        if (createrKey.isNullOrEmpty()) {
            logger.warning("[QLCustomSpawn] 容器 $name 的生成表为空或权重异常，已跳过")
            return
        }

        val creater = CreaterApi.getManager().get(createrKey)
        if (creater == null) {
            logger.warning("[QLCustomSpawn] 容器 $name 绑定的生成器 $createrKey 未找到")
            return
        }

        val pokemonSpec = creater.getRandomSpec()
        if (pokemonSpec.isEmpty()) {
            logger.warning("[QLCustomSpawn] 容器 $name 的生成器 $createrKey 未提供可用的 spec")
            return
        }

        val spawnLocation = area.randomGroundLocation()
        if (spawnLocation == null) {
            logger.warning("[QLCustomSpawn] 容器 $name 未找到可用的地面坐标，已跳过本次生成")
            return
        }

        pokemonSpec.createPokemon(spawnLocation)
    }

    private fun canSpawn(): Boolean {
        if (conditions.isEmpty()) {
            return true
        }

        val onlinePlayerCount = Bukkit.getOnlinePlayers().size
        if (onlinePlayerCount == 0) {
            logDebugInfo("[QLCustomSpawn] 容器 $name 跳过生成：当前服务器无在线玩家")
            return false
        }

        val cobblemonCount = LocationUtils.getCobblemonInArea(area.pointA, area.pointB)
        val replaceMap = mapOf(
            "%location1_x%" to area.pointA.x.toString(),
            "%location1_y%" to area.pointA.y.toString(),
            "%location1_z%" to area.pointA.z.toString(),
            "%location2_x%" to area.pointB.x.toString(),
            "%location2_y%" to area.pointB.y.toString(),
            "%location2_z%" to area.pointB.z.toString(),
            "%cobblemon_count%" to cobblemonCount.toString()
        )

        val allPassed = conditions.all { condition ->
            val expression = replaceMap.entries.fold(condition) { acc, entry ->
                acc.replace(entry.key, entry.value)
            }

            val result = try {
                Arim.evaluator.evaluate(expression)
            } catch (e: Exception) {
                logDebugWarning("[QLCustomSpawn] 容器 $name 条件 '$condition' 解析失败，解析式 '$expression'，原因：${e.message}")
                false
            }

            logDebugInfo("[QLCustomSpawn] 容器 $name 条件检查：原始='$condition'，解析='$expression'，结果=$result，当前宝可梦数量=$cobblemonCount，在线玩家数=$onlinePlayerCount")
            result
        }

        if (!allPassed) {
            logDebugInfo("[QLCustomSpawn] 容器 $name 条件未全部通过，本次跳过生成")
        }

        return allPassed
    }

    private fun logDebugInfo(message: String) {
        if (debugEnabled) {
            logger.info(message)
        }
    }

    private fun logDebugWarning(message: String) {
        if (debugEnabled) {
            logger.warning(message)
        }
    }

    override fun toString(): String {
        return "SpawnContainer(name=$name, area=$area, periodSeconds=$periodSeconds, spawner=$spawner)"
    }

    data class SpawnArea(val pointA: Location, val pointB: Location) {
        fun isWorldAvailable(): Boolean {
            val worldA = pointA.world
            val worldB = pointB.world
            return worldA != null && worldB != null && worldA.name.equals(worldB.name, true)
        }

        fun randomGroundLocation(): Location? {
            return if (isWorldAvailable()) {
                LocationUtils.getRandomGroundLocation(pointA, pointB)
            } else null
        }
    }

    companion object {
        fun fromConfiguration(name: String, config: Configuration): SpawnContainer? {
            val logger = BukkitPlugin.getInstance().logger

            val locationA = config.getString("location1")?.toLocation()
            val locationB = config.getString("location2")?.toLocation()

            if (locationA == null || locationB == null) {
                logger.warning("[QLCustomSpawn] 容器 $name 的坐标无效，已跳过加载")
                return null
            }

            val period = config.getInt("period").coerceAtLeast(1)
            val conditionList = config.getStringList("conditions")

            val spawnerSection = config.getConfigurationSection("spawner")
            val spawnerMap = spawnerSection?.getKeys(false)?.associateWith { key ->
                config.getDouble("spawner.$key")
            }?.filterValues { it > 0.0 }

            if (spawnerMap.isNullOrEmpty()) {
                logger.warning("[QLCustomSpawn] 容器 $name 缺少 spawner 配置或权重无效")
                return null
            }

            return SpawnContainer(
                name = name,
                area = SpawnArea(locationA, locationB),
                periodSeconds = period,
                spawner = spawnerMap,
                conditions = conditionList
            )
        }
    }
}
