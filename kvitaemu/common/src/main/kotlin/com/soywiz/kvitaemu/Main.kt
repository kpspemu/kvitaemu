package com.soywiz.kvitaemu

import com.soywiz.korio.error.invalidOp
import com.soywiz.korio.util.mask


fun main(args: Array<String>) {
	// Create tables

	// 0xB590        - 1011_0_10_1_10010000:--------------- - PUSH {R4,R7,LR}
	// 0xE92D:0x41F0 - 1110_1_00_1_00101101:100_000111110000 - PUSH.W {R4-R8,LR}


}

data class MaskValue(val mask: Int, val value: Int) {
	companion object {
		operator fun invoke(format: String): MaskValue {
			var rmask = 0
			var rvalue = 0
			var rsize = 0
			val parts = format.split(":")
			for (part in parts) {
				when {
					part.startsWith("0") || part.startsWith("1") || part.startsWith("-") -> {
						val size = part.length
						rmask = rmask shl size
						rvalue = rvalue shl size
						rsize += size
						if (!part.startsWith("-")) {
							rmask = rmask or size.mask()
							rvalue = rvalue or part.toInt(2)
						}
					}
					else -> {
						val size = part.length
						rmask = rmask shl size
						rvalue = rvalue shl size
						rsize += size
					}
				}
			}

			when (rsize) {
				16, 32 -> Unit
				else -> invalidOp("Invalid size $rsize!")
			}

			return MaskValue(rmask, rvalue)
		}
	}

	fun match(value: Int): Boolean = (value and mask) == this.value
}

data class TableFormat(
	val mode: ArmTables.Mode,
	val encoding: Int,
	val name: String,
	val format: String,
	val format2: String? = null
) {
	val mv = MaskValue(format)
	val mv2 = format2?.let { MaskValue(it) }

	fun match(value: Int) = mv.match(value)

	fun matchFirst(value: Int): Boolean = mv.match(value)
	fun matchSecond(value: Int): Boolean = mv2?.match(value) ?: false

	val nchunks = if (mv2 != null) 2 else 1
}

//object ArmTables {
//	val tables = listOf(
//		TableFormat("CCCC:001:OOOO:S:NNNN:DDDD:oooooooooooo"),  // Data processing and FSR transfer
//		TableFormat("CCCC:000000:A:S:DDDD:NNNN:SSSS:1001:MMMM"),  // Multiply
//		TableFormat("CCCC:00001:U:A:S:HHHH:LLLL:NNNN:1001:MMMM"), // Multiply long
//		TableFormat("CCCC:00010:B:00:NNNN:DDDD:0000:1001:MMMM"), // Single data swap
//		TableFormat("CCCC:0001:0010:111111111111:0001:NNNN"), // Branch and exchange
//		TableFormat("CCCC:000:P:U:0:W:L:NNNN:DDDD:00001:S:H:1:MMMM"), // Halfword data transfer, register offset
//		TableFormat("CCCC:000:P:U:1:W:L:NNNN:DDDD:OOOO:1:S:H:1:oooo"), // Halfword data transfer, immediate offset
//		TableFormat("CCCC:011:P:U:B:W:L:NNNN:DDDD:OOOOOOOOOOOO"), // Single data transfer
//		TableFormat("CCCC:011:--------------------:1:----"), // Undefined
//		TableFormat("CCCC:100:P:U:S:W:L:NNNN:LLLLLLLLLLLLLLLL"), // Block data transfer
//		TableFormat("CCCC:101:L:OOOOOOOOOOOOOOOOOOOOOOOO"), // Branch
//		TableFormat("CCCC:110:P:U:N:W:L:NNNN:DDDD:PPPP:OOOOOOOO"), // Coprocesor data transfer
//		TableFormat("CCCC:1110:oooo:NNNN:DDDD:PPPP:ppp:0:mmm"), // Coprocesor data operation
//		TableFormat("CCCC:1110:ooo:L:NNNN:DDDD:PPPP:ppp:1:mmm"), // Coprocesor register transfer
//		TableFormat("CCCC:1111:------------------------") // Software interrupt
//	)
//
//	fun decodeSlow(value: Int): TableFormat? {
//		for (table in tables) if (table.match(value)) return table
//
//		return null
//	}
//}

enum class ArmConditions(val id: Int, val desc: String) {
	EQ(0b0000, "Equal"),
	NE(0b0001, "Not equal"),
	HS_CS(0b0010, "Unsigned higher or same, carry set"),
	LO_CC(0b0011, "Unsigned lower, carry clear"),
	MI(0b0100, "Negative, minus"),
	PL(0b0101, "Positive or zero, plus"),
	VS(0b0110, "Overflow"),
	VC(0b0111, "No overflow"),
	HI(0b1000, "Unsigned higher"),
	LS(0b1001, "Unsigned lower or same"),
	GE(0b1010, "Signed greater or equal"),
	LT(0b1011, "Signed less than"),
	GT(0b1100, "Signed greater than"),
	LE(0b1101, "Signed less than or equal"),
	AL(0b1110, "Always");
}

interface InstructionReader {
	fun read16(): Int
	fun read32(): Int
}

object SlowInstructionDecoder {
	fun decode(thumb: Boolean, r: InstructionReader): TableFormat? {
		if (thumb) {
			val low = r.read16()
			for (f in ArmTables.tables) {
				if (f.nchunks == 1) {
					if (f.match(low)) return f
				} else if (f.nchunks == 2) {
					if (f.matchFirst(low)) {
						val high = r.read16()
						return f
					}
				} else {
					TODO()
				}
			}
		} else {
			TODO()
		}
		return null
	}
}
