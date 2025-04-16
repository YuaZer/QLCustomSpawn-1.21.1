package io.github.yuazer.qlcustomspawn.obj

import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.toPokemon
import io.github.yuazer.qlcustomspawn.utils.RandomUtils
import taboolib.module.configuration.Configuration

class SpawnCreater(val name: String, val configuration: Configuration) {
    var keyChanceMap : MutableMap<String,Double>
    init {
        keyChanceMap = mutableMapOf()
        configuration.getKeys(false).forEach {
            if (configuration.getString("${it}.spec") != null) {
                keyChanceMap[it] = configuration.getDouble("${it}.chance")
            }
        }
    }
    fun getRandomSpec(): String? {
        val key = RandomUtils.pickByWeight(keyChanceMap)
        return configuration.getString("${key}.spec")
    }
    fun getPokemonByKey(key: String): Pokemon? {
        return configuration.getString("${key}.spec")?.toPokemon()
    }
    fun getRandomPokemon(): Pokemon? {
        val randomKey = RandomUtils.pickByWeight(keyChanceMap)
        return randomKey?.let { getPokemonByKey(it) }
    }
}