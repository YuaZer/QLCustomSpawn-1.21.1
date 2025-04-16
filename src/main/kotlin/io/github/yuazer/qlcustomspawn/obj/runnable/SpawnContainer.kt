package io.github.yuazer.qlcustomspawn.obj.runnable

import io.github.yuazer.qlcustomspawn.api.data.CreaterApi
import io.github.yuazer.qlcustomspawn.api.extension.CobbleExtension.createPokemon
import io.github.yuazer.qlcustomspawn.api.extension.LocationExtension.toLocation
import io.github.yuazer.qlcustomspawn.utils.LocationUtils
import io.github.yuazer.qlcustomspawn.utils.RandomUtils
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.scheduler.BukkitRunnable
import taboolib.common.platform.function.submit
import taboolib.module.configuration.Configuration
import taboolib.platform.BukkitPlugin
import top.maplex.arim.Arim

class SpawnContainer(val name: String, val yamlConfig: Configuration) : BukkitRunnable() {
    var locationA: Location
    var locationB: Location
    var period: Int
    var timeCount: Int
        get() = field
    var spawner: MutableMap<String, Double>

    init {
        locationA = yamlConfig.getString("location1")?.toLocation()!!
        locationB = yamlConfig.getString("location2")?.toLocation()!!
        period = yamlConfig.getInt("period")
        timeCount = yamlConfig.getInt("period")
        spawner = mutableMapOf()
        yamlConfig.getConfigurationSection("spawner")!!.getKeys(false).forEach {
            spawner[it] = yamlConfig.getDouble("spawner.$it")
        }
    }

    override fun run() {
        if (timeCount <= 0) {
            //TODO 刷新精灵
            val conditions = yamlConfig.getStringList("conditions")
            var canSpawn = false
            if (conditions.isNotEmpty()) {
                //TODO 满足条件
                if (conditions.all {
                        Arim.evaluator.evaluate(
                            it
                                .replace("%location1_x%", locationA.x.toString())
                                .replace("%location1_y%", locationA.y.toString())
                                .replace("%location1_z%", locationA.z.toString())
                                .replace("%location2_x%", locationB.x.toString())
                                .replace("%location2_y%", locationB.y.toString())
                                .replace("%location2_z%", locationB.z.toString())
                                .replace(
                                    "%cobblemon_count%",
                                    LocationUtils.getCobblemonInArea(locationA, locationB).toString()
                                )
                        )
                    }) {
                    canSpawn = true
                }
            } else {
                canSpawn = true
            }
            if (canSpawn) {
                val result = RandomUtils.pickByWeight(spawner)
                //TODO 根据名称获取生成器,然后获取Pokemon对象
                val creater = CreaterApi.getManager().get(result!!)
                if (!CreaterApi.getManager().contains(creater!!.name)) {
                    println("QLCustomSpawn: Container $name Creater $result Not Found!")
                    return
                }
                val pokemonSpec = creater.getRandomSpec()
                if (pokemonSpec == null) {
                    println("QLCustomSpawn: Container $name Creater $result Spec Not Found!")
                    return
                }
                val randomGroundLocation = LocationUtils.getRandomGroundLocation(locationA, locationB) ?: return
                Bukkit.getScheduler().runTask(BukkitPlugin.getInstance(), Runnable {
                    pokemonSpec.createPokemon(randomGroundLocation)
                })
            }
            //重置时间
            timeCount = period
            return
        }
        timeCount--
    }
}