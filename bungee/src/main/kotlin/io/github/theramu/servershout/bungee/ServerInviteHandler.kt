package io.github.theramu.servershout.bungee

import net.md_5.bungee.api.ProxyServer
import java.io.ByteArrayOutputStream
import java.util.UUID
import java.io.DataOutputStream

class ServerInviteHandler {

    fun sendServerInvite(playerUUID: UUID, inviterUUID: UUID, serverName: String) {
        val serverInfo = ProxyServer.getInstance().getServerInfo(serverName) ?: return
        val player = serverInfo.players.firstOrNull() ?: return
        ByteArrayOutputStream().use { bytes ->
            DataOutputStream(bytes).use { out ->
                out.writeUTF("serverinvite")
                out.writeUTF("$playerUUID,$inviterUUID")
            }
            player.server.sendData("BungeeCord", bytes.toByteArray())
        }
    }
}
