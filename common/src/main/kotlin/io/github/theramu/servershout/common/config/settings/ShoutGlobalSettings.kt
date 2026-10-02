package io.github.theramu.servershout.common.config.settings

import java.io.Serializable

/**
 *
 * @author TheRamU
 * @since 2024/9/1 15:09
 */
data class ShoutGlobalSettings(
    val logging: Boolean,
    val serverMap: Map<String, String>,
    val tokenMap: Map<String, String>,
    val serverList: ServerListSettings
) : Serializable {

    fun getServerDisplayName(serverName: String): String {
        serverMap[serverName]?.let { return it }
        val hitKey = serverMap.keys
            .filter { it.startsWith("+") && serverName.startsWith(it.substring(1)) }
            .maxByOrNull { it.length }
        return hitKey?.let { serverMap[it] } ?: serverName
    }

    companion object {
        @JvmStatic
        fun deserialize(map: Map<String, Any>): ShoutGlobalSettings {
            @Suppress("UNCHECKED_CAST")
            return ShoutGlobalSettings(
                logging = map["logging"] as Boolean,
                serverMap = map["server-map"] as Map<String, String>,
                tokenMap = map["token-map"] as Map<String, String>,
                serverList = ServerListSettings.deserialize(map["server-list"] as Map<String, Any>)
            )
        }
    }
}