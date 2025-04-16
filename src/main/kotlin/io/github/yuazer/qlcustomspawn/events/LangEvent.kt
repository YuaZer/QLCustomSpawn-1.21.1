package io.github.yuazer.qlcustomspawn.events

import io.github.yuazer.qlcustomspawn.Qlcustomspawn
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.lang.event.PlayerSelectLocaleEvent
import taboolib.module.lang.event.SystemSelectLocaleEvent

object LangEvent {
    @SubscribeEvent
    fun lang(event: PlayerSelectLocaleEvent) {
        event.locale = Qlcustomspawn.config.getString("Lang", "zh_CN")!!
    }

    @SubscribeEvent
    fun lang(event: SystemSelectLocaleEvent) {
        event.locale = Qlcustomspawn.config.getString("Lang", "zh_CN")!!
    }
}