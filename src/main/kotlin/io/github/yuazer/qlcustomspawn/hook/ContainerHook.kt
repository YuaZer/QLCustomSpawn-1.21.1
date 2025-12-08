package io.github.yuazer.qlcustomspawn.hook

import io.github.yuazer.qlcustomspawn.api.data.ContainerApi
import org.bukkit.entity.Player
import taboolib.platform.compat.PlaceholderExpansion

object ContainerHook : PlaceholderExpansion {
    // 变量前缀
    override val identifier: String = "qlcustomspawn"

    // 变量操作
    override fun onPlaceholderRequest(player: Player?, args: String): String {
        if (player == null) {
            return "player null"
        }
        val parts = args.split("<>", limit = 2)
        if (parts.size < 2) {
            return "args error"
        }
        val containerName = parts[0]
        val request = parts[1]
        return ContainerApi.getManager().get(containerName)?.let {
            when (request) {
                "name" -> it.name
                "period" -> it.periodSeconds.toString()
                "timeCount" -> it.countDown.toString()
                else -> "null"
            }
        } ?: "0"
    }
}