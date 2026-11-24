package io.github.theramu.servershout.common.service

/** Selects exactly the recipients of the chat message and its matching display. */
internal object ShoutRecipients {
    data class Selection<P>(val players: List<P>, val echoSender: Boolean)

    fun <S, P> select(
        servers: List<S>, senderServer: String, localBroadcast: Boolean, senderOnline: Boolean,
        serverName: (S) -> String, players: (S) -> List<P>, isMuted: (P) -> Boolean
    ): Selection<P> {
        val selected = servers.asSequence()
            .filter { localBroadcast || serverName(it) != senderServer }
            .flatMap { players(it).asSequence() }
            .filterNot(isMuted)
            .toList()
        return Selection(selected, senderOnline && !localBroadcast)
    }
}
