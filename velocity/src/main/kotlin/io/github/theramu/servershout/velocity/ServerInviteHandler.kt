package io.github.theramu.servershout.velocity

import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream

class ServerInviteHandler(
    private val proxy: ProxyServer
) {

    companion object {
        val BUNGEECORD_CHANNEL = MinecraftChannelIdentifier.from("BungeeCord")
    }

    fun sendServerInvite(playerName: String, inviterName: String, serverName: String) {
        val server = proxy.getServer(serverName).orElse(null) ?: return
        ByteArrayOutputStream().use { bytes ->
            DataOutputStream(bytes).use { out ->
                out.writeUTF("serverinvite")
                out.writeUTF("$playerName,$inviterName")
            }
            server.sendPluginMessage(BUNGEECORD_CHANNEL, bytes.toByteArray())
        }
    }
}
