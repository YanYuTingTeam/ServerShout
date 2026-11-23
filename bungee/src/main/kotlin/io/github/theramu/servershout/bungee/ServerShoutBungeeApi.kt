package io.github.theramu.servershout.bungee

import io.github.theramu.servershout.bungee.command.BungeeCommandAdapter
import io.github.theramu.servershout.bungee.listener.PlayerEventListener
import io.github.theramu.servershout.bungee.listener.PluginChannelMessageListener
import io.github.theramu.servershout.bungee.platform.BungeePlatform
import io.github.theramu.servershout.bungee.platform.logging.BungeePlatformLogger
import io.github.theramu.servershout.common.ServerShoutProxyApi
import io.github.theramu.servershout.common.platform.player.PlatformPlayer
import io.github.theramu.servershout.common.platform.server.PlatformServer
import java.util.UUID
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.config.ServerInfo
import org.bstats.bungeecord.Metrics
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream

/**
 * @author TheRamU
 * @since 2024/8/28 18:16
 */
class ServerShoutBungeeApi(
    private val plugin: ServerShoutBungeePlugin
) : ServerShoutProxyApi() {

    override val platform = BungeePlatform()
    override val logger = BungeePlatformLogger()
    override val dataFolder = plugin.dataFolder
    override val useShoutBroadcast = true
    val serverInviteHandler = ServerInviteHandler()
    private var metrics: Metrics? = null

    override fun onEnable() {
        super.onEnable()
        ProxyServer.getInstance().registerChannel("servershout:main")
        ProxyServer.getInstance().registerChannel(SHOUT_MESSAGE_CHANNEL)
        ProxyServer.getInstance().registerChannel("BungeeCord")
        ProxyServer.getInstance().pluginManager.registerListener(plugin, PlayerEventListener())
        ProxyServer.getInstance().pluginManager.registerListener(plugin, PluginChannelMessageListener())
        registerCommands()
        metrics = Metrics(plugin, 6453)
        logger.info("&aServerShout is ready!")
    }

    override fun onDisable() {
        super.onDisable()
        ProxyServer.getInstance().pluginManager.unregisterListeners(plugin)
        ProxyServer.getInstance().pluginManager.unregisterCommands(plugin)
        ProxyServer.getInstance().unregisterChannel(SHOUT_MESSAGE_CHANNEL)
        metrics?.shutdown()
    }

    private fun registerCommands() {
        ProxyServer.getInstance().pluginManager.registerCommand(plugin, BungeeCommandAdapter())
    }

    override fun sendUpdate(playerName: String) {
        ProxyServer.getInstance().getPlayer(playerName)?.let { player ->
            ByteArrayOutputStream().use { bytes ->
                DataOutputStream(bytes).use { out ->
                    out.writeUTF("UPDATE_BALANCE")
                    out.writeUTF(player.uniqueId.toString())
                }
                player.server.sendData("servershout:main", bytes.toByteArray())
            }
        }
    }

    override fun sendServerInvite(playerUUID: UUID, inviterUUID: UUID, serverName: String) {
        serverInviteHandler.sendServerInvite(playerUUID, inviterUUID, serverName)
    }

    override fun sendShoutData(player: PlatformPlayer, data: ByteArray) {
        ProxyServer.getInstance().getPlayer(player.uuid)?.server?.sendData(SHOUT_MESSAGE_CHANNEL, data)
    }

    override fun sendShoutData(servers: List<PlatformServer>, data: ByteArray) {
        servers.forEach { server ->
            (server.handle as? ServerInfo)?.sendData(SHOUT_MESSAGE_CHANNEL, data, false)
        }
    }
}
