package io.github.theramu.servershout.common

import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ShoutDisplayAuthTest {
    @Test fun matchesCrossImplementationVector() {
        val key = ShoutDisplayAuth.parseKey("000102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f")
        val data = ShoutDisplayAuth.encode(key, 1, null, "SHOUT_CHANNEL", "hello", 1700000000000L,
            UUID(0x0102030405060708L, 0x1112131415161718L))
        val actual = data.joinToString("") { "%02x".format(it.toInt() and 0xff) }
        assertEquals("04010000018bcfe5680001020304050607081112131415161718000d53484f55545f4348414e4e454c000568656c6c6f" +
            "2a023dca2629c5020722dc77a588a7d42e30bf123bcb825b7848c3c4c0f0c215", actual)
    }

    @Test fun rejectsInvalidKey() {
        assertFailsWith<IllegalArgumentException> { ShoutDisplayAuth.parseKey("bad") }
    }
}
