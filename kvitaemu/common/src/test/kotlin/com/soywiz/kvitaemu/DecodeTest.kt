package com.soywiz.kvitaemu

import com.soywiz.korio.stream.SyncStream
import com.soywiz.korio.stream.openSync
import com.soywiz.korio.stream.readS32_le
import com.soywiz.korio.stream.readU16_le
import com.soywiz.korio.util.fromHexString
import org.junit.Test
import kotlin.test.assertEquals

class DecodeTest {
	@Test
	fun name() {
		assertEquals("PUSH", SlowInstructionDecoder.decode(true, "90B5".hexReader)?.name)
		assertEquals("PUSH", SlowInstructionDecoder.decode(true, "2DE9F041".hexReader)?.name)
		assertEquals("SUB", SlowInstructionDecoder.decode(true, "94B0".hexReader)?.name)
	}

	val String.hexStream: SyncStream get() = this.fromHexString().openSync()
	val String.hexReader: InstructionReader
		get() {
			val s = hexStream
			return object : InstructionReader {
				override fun read16(): Int = s.readU16_le()
				override fun read32(): Int = s.readS32_le()
			}
		}

}
