package io.github.theramu.servershout.common.service

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ShoutRecipientsTest {
    private data class Player(val name: String, val mutedSender: Boolean = false, val mutedChannel: Boolean = false)
    private data class Server(val name: String, val players: List<Player>)
    private val sender = Player("sender")
    private val local = Player("local")
    private val allowed = Player("allowed")
    private val mutedSender = Player("muted sender", mutedSender = true)
    private val mutedChannel = Player("muted channel", mutedChannel = true)
    private val servers = listOf(Server("home", listOf(sender, local)),
        Server("other", listOf(allowed, mutedSender, mutedChannel)))

    private fun select(localBroadcast: Boolean, senderOnline: Boolean = true) = ShoutRecipients.select(
        servers, "home", localBroadcast, senderOnline, { it.name }, { it.players },
        { it.mutedSender || it.mutedChannel }
    )

    @Test fun excludesBothKindsOfMute() {
        val result = select(localBroadcast = true)
        assertEquals(listOf(sender, local, allowed), result.players)
        assertFalse(result.echoSender)
    }

    @Test fun excludesHomeButEchoesOnlineSenderWhenLocalBroadcastIsOff() {
        val result = select(localBroadcast = false)
        assertEquals(listOf(allowed), result.players)
        assertTrue(result.echoSender)
        assertFalse(select(localBroadcast = false, senderOnline = false).echoSender)
    }
}
