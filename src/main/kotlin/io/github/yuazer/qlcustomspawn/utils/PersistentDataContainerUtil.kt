package io.github.yuazer.qlcustomspawn.utils

import io.github.yuazer.qlcustomspawn.Qlcustomspawn
import org.bukkit.NamespacedKey
import org.bukkit.entity.Entity
import org.bukkit.persistence.PersistentDataType
import taboolib.platform.BukkitPlugin
import java.util.Locale

/**
 * PersistentDataContainer 相关的便捷方法。
 */
object PersistentDataContainerUtil {
    private const val DEFAULT_KEY = "qlcustomspawn-container-id"

    private val plugin get() = BukkitPlugin.getInstance()

    /**
     * 从配置中读取 NamespacedKey。若配置为空则回退到默认值。
     */
    private val namespacedKey: NamespacedKey
        get() {
            val rawKey = Qlcustomspawn.config.getString("pdc-key")?.ifBlank { null }
                ?: DEFAULT_KEY
            return NamespacedKey(plugin, rawKey.lowercase(Locale.getDefault()))
        }

    /**
     * 为实体写入容器 ID。
     */
    fun setContainerId(entity: Entity, containerId: String) {
        entity.persistentDataContainer.set(namespacedKey, PersistentDataType.STRING, containerId)
    }

    /**
     * 获取实体绑定的容器 ID。
     */
    fun getContainerId(entity: Entity): String? {
        return entity.persistentDataContainer.get(namespacedKey, PersistentDataType.STRING)
    }

    /**
     * 判断实体是否由指定容器生成。
     */
    fun isFromContainer(entity: Entity, containerId: String): Boolean {
        return getContainerId(entity) == containerId
    }
}
