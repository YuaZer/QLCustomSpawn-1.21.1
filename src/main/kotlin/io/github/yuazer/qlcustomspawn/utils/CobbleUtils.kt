package io.github.yuazer.qlcustomspawn.utils

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.pokemon.abilities.HiddenAbilityType
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_21_R1.CraftServer
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftEntity
import org.bukkit.entity.Entity

object CobbleUtils {
    fun Pokemon.hasHiddenAbility(): Boolean {
        // 找出所有隐藏特性
        val possibleHidden = this.form.abilities.mapping
            .flatMap { it.value }
            .filter { it.type == HiddenAbilityType }
        // 没有隐藏特性直接返回 false
        if (possibleHidden.isEmpty()) return false
        // 所有特性名称
        val allAbilityNames = this.form.abilities.mapping
            .flatMap { it.value }
            .map { it.template.name }
            .toSet() // 防止重复
        // 所有隐藏特性名称
        val hiddenAbilityNames = possibleHidden
            .map { it.template.name }
            .toSet()

        // 如果所有特性名都和隐藏特性名完全一样，则返回 false
        return if (allAbilityNames == hiddenAbilityNames) false else{
//            println("隐藏特性名称: $hiddenAbilityNames")
            true
        }

    }
    fun putPokemonData(pokemon:Pokemon,key: String,value: String){
        pokemon.persistentData.putString(key,value)
    }
    fun getPokemonData(pokemon:Pokemon,key: String): String?{
        return pokemon.persistentData.getString(key)
    }
    fun Pokemon.isHiddenAbility(): Boolean {
        return this.ability.template.name in this.form.abilities.mapping.flatMap { it.value }
            .filter { it.type == HiddenAbilityType }
            .map { it.template.name }
    }
    fun PokemonEntity.getBukkitEntity(): Entity? {
        val entity = CraftEntity.getEntity<net.minecraft.world.entity.Entity?>(Bukkit.getServer() as CraftServer, this)
        return entity
    }
}