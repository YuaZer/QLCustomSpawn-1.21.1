package io.github.yuazer.qlcustomspawn.utils

import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.pokemon.abilities.HiddenAbilityType

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
    fun Pokemon.isHiddenAbility(): Boolean {
        return this.ability.template.name in this.form.abilities.mapping.flatMap { it.value }
            .filter { it.type == HiddenAbilityType }
            .map { it.template.name }
    }
}