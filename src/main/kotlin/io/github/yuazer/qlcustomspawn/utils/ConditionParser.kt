package io.github.yuazer.qlcustomspawn.utils

import com.cobblemon.mod.common.api.pokemon.stats.Stats
import com.cobblemon.mod.common.pokemon.Pokemon
import io.github.yuazer.qlcustomspawn.api.extension.NMSExtension.getBukkitItem
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.platform.compat.replacePlaceholder
import top.maplex.arim.Arim

class ConditionParser(val pokemon: Pokemon) {
    lateinit var pokemonPlaceholder: MutableMap<String, Any>

    init {
        pokemonPlaceholder = mutableMapOf()
        pokemonPlaceholder["%pokemon_name%"] = pokemon.species.name
        pokemonPlaceholder["%pokemon_level%"] = pokemon.level
        pokemonPlaceholder["%pokemon_gender%"] = pokemon.gender.name
        pokemonPlaceholder["%pokemon_shiny%"] = pokemon.shiny
        pokemonPlaceholder["%pokemon_isLegendary%"] = pokemon.isLegendary()
        pokemonPlaceholder["%pokemon_isMythical%"] = pokemon.isMythical()
        pokemonPlaceholder["%pokemon_nature%"] = pokemon.nature.name
        pokemonPlaceholder["%pokemon_ability%"] = pokemon.ability.name
        pokemonPlaceholder["%pokemon_ivs_attack%"] = pokemon.ivs[Stats.ATTACK]!!
        pokemonPlaceholder["%pokemon_ivs_defense%"] = pokemon.ivs[Stats.DEFENCE]!!
        pokemonPlaceholder["%pokemon_ivs_hp%"] = pokemon.ivs[Stats.HP]!!
        pokemonPlaceholder["%pokemon_ivs_speed%"] = pokemon.ivs[Stats.SPEED]!!
        pokemonPlaceholder["%pokemon_ivs_special_attack%"] = pokemon.ivs[Stats.SPECIAL_ATTACK]!!
        pokemonPlaceholder["%pokemon_ivs_special_defense%"] = pokemon.ivs[Stats.SPECIAL_DEFENCE]!!
        pokemonPlaceholder["%pokemon_evs_attack%"] = pokemon.evs[Stats.ATTACK]!!
        pokemonPlaceholder["%pokemon_evs_defense%"] = pokemon.evs[Stats.DEFENCE]!!
        pokemonPlaceholder["%pokemon_evs_hp%"] = pokemon.evs[Stats.HP]!!
        pokemonPlaceholder["%pokemon_evs_speed%"] = pokemon.evs[Stats.SPEED]!!
        pokemonPlaceholder["%pokemon_evs_special_attack%"] = pokemon.evs[Stats.SPECIAL_ATTACK]!!
        pokemonPlaceholder["%pokemon_evs_special_defense%"] = pokemon.evs[Stats.SPECIAL_DEFENCE]!!
        pokemonPlaceholder["%pokemon_moves_0%"] = pokemon.moveSet[0]?.name ?: "null"
        pokemonPlaceholder["%pokemon_moves_1%"] = pokemon.moveSet[1]?.name ?: "null"
        pokemonPlaceholder["%pokemon_moves_2%"] = pokemon.moveSet[2]?.name ?: "null"
        pokemonPlaceholder["%pokemon_moves_3%"] = pokemon.moveSet[3]?.name ?: "null"
        pokemonPlaceholder["%pokemon_nickname%"] = pokemon.nickname ?: pokemon.species.name
        pokemonPlaceholder["%pokemon_form%"] = pokemon.form.name
        pokemonPlaceholder["%pokemon_stats_hp%"] = pokemon.getStat(Stats.HP)
        pokemonPlaceholder["%pokemon_stats_attack%"] = pokemon.getStat(Stats.ATTACK)
        pokemonPlaceholder["%pokemon_stats_defense%"] = pokemon.getStat(Stats.DEFENCE)
        pokemonPlaceholder["%pokemon_stats_speed%"] = pokemon.getStat(Stats.SPEED)
        pokemonPlaceholder["%pokemon_stats_special_attack%"] = pokemon.getStat(Stats.SPECIAL_ATTACK)
        pokemonPlaceholder["%pokemon_stats_special_defense%"] = pokemon.getStat(Stats.SPECIAL_DEFENCE)
        pokemonPlaceholder["%pokemon_exp%"] = pokemon.experience
        pokemonPlaceholder["%pokemon_held_item%"] = pokemon.heldItem().copy().getBukkitItem()?.type?.name ?: "null"
        pokemonPlaceholder["%pokemon_world%"] =
            pokemon.entity?.uuid?.let { Bukkit.getEntity(it)?.world?.name ?: "null" } ?: "null"
    }

    fun parse(condition: String): Boolean {
        pokemonPlaceholder.forEach { (k, v) ->
            condition.replace(k, v.toString())
        }
        return Arim.evaluator.evaluate(condition)
    }

    fun parseWithPlayer(condition: String, player: Player): Boolean {
        pokemonPlaceholder.forEach { (k, v) ->
            condition.replace(k, v.toString())
        }
        return Arim.evaluator.evaluate(condition.replacePlaceholder(player))
    }

    fun parse(condition: List<String>, any: Boolean = false): Boolean {
        return if (any) {
            condition.any { parse(it) }
        } else {
            condition.all { parse(it) }
        }
    }

    fun parseWithPlayer(condition: List<String>, player: Player, any: Boolean = false): Boolean {
        return if (any) {
            condition.any { parseWithPlayer(it, player) }
        } else {
            condition.all { parseWithPlayer(it, player) }
        }
    }
}