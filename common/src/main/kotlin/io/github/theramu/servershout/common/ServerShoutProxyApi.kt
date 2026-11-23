package io.github.theramu.servershout.common

import io.github.theramu.servershout.common.model.ShoutChannel
import io.github.theramu.servershout.common.service.ShoutChannelService
import io.github.theramu.servershout.common.platform.player.PlatformPlayer
import io.github.theramu.servershout.common.platform.server.PlatformServer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.util.*

/**
 * @author TheRamU
 * @since 2024/8/28 17:26
 */
abstract class ServerShoutProxyApi protected constructor() : ServerShoutApi() {
    val shoutChannelService = ShoutChannelService()
    open val useShoutBroadcast = false

    override fun load() {
        super.load()
        loadShoutChannels()
    }

    override fun removeCache(uuid: UUID) {
        super.removeCache(uuid)
        shoutChannelService.removePlayer(uuid)
    }

    private fun loadShoutChannels() {
        logger.info("Loading shout channels...")
        shoutChannelService.reset()
        val shoutConfig = configLoader.shoutConfig
        shoutConfig.getKeys("channels").forEach { key ->
            val shoutChannel = shoutConfig.get("channels.$key", ShoutChannel::class.java)
            shoutChannel.name = key
            shoutChannelService.addChannel(shoutChannel)
        }
    }

    abstract override fun sendServerInvite(playerUUID: UUID, inviterUUID: UUID, serverName: String)

    fun sendShout(player: PlatformPlayer, channelName: String, component: Component) {
        try {
            val content = LegacyComponentSerializer.legacySection().serialize(component)
            if (channelName.length > 32766 || content.length > 32766) return
            val bytes = ByteArrayOutputStream()
            DataOutputStream(bytes).use { out ->
                out.writeByte(2)
                out.writeUTF(player.uuid.toString())
                out.writeUTF(channelName)
                out.writeUTF(content)
            }
            if (bytes.size() <= 32766) sendShoutData(player, bytes.toByteArray())
        } catch (e: Exception) {
            logger.warn("Unable to forward shout display: ${e.javaClass.simpleName}")
        }
    }

    fun sendShout(servers: List<PlatformServer>, channelName: String, component: Component) {
        try {
            val content = LegacyComponentSerializer.legacySection().serialize(component)
            if (channelName.length > 32766 || content.length > 32766) return
            val bytes = ByteArrayOutputStream()
            DataOutputStream(bytes).use { out ->
                out.writeByte(3)
                out.writeByte(1)
                out.writeUTF(channelName)
                out.writeUTF(content)
            }
            if (bytes.size() <= 32766) sendShoutData(servers, bytes.toByteArray())
        } catch (e: Exception) {
            logger.warn("Unable to forward shout broadcast: ${e.javaClass.simpleName}")
        }
    }

    protected abstract fun sendShoutData(player: PlatformPlayer, data: ByteArray)

    protected open fun sendShoutData(servers: List<PlatformServer>, data: ByteArray) {
    }

    companion object {
        const val SHOUT_MESSAGE_CHANNEL = "servershout:cast"
    }
}
