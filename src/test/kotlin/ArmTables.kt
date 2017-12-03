fun mask(size: Int) = (1 shl size) - 1

data class TableFormat(val format: String) {
	val mask: Int
	val value: Int

	init {
		var rmask = 0
		var rvalue = 0
		val parts = format.split(":")
		for (part in parts) {
			when {
				part.startsWith("0") || part.startsWith("1") || part.startsWith("-") -> {
					val size = part.length
					rmask = rmask shl size
					rvalue = rvalue shl size
					if (!part.startsWith("-")) {
						rmask = rmask or mask(size)
						rvalue = rvalue or part.toInt(2)
					}
				}
				else -> {
					val size = part.length
					rmask = rmask shl size
					rvalue = rvalue shl size
				}
			}
		}
		mask = rmask
		value = rvalue
	}

	fun match(value: Int) = (value and mask) == this.value
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

//ARM® Architecture Reference Manual  ARMv7-A and ARMv7-R edition
object ArmTables {
	data class Format(
		val mode: Int,
		val encoding: Int,
		val name: String,
		val format: String
	)

	val THUMB = 0
	val ARM = 1

	val tables = listOf(
		// A8.8.133 PUSH
		Format(THUMB, 1, "PUSH", "1011:0:10:M:RRRRRRRR"),
		Format(THUMB, 2, "PUSH", "11101:00:100:1:0:1101:-:M:-:RRRRRRRRRRRRR"),
		Format(THUMB, 3, "PUSH", "11111:00:0:0:10:0:1101:TTTT:1:101:00000100"),
		Format(ARM, 1, "PUSH", "CCCC:100100:1:0:1101:RRRRRRRRRRRRRRRR"),
		Format(ARM, 2, "PUSH", "CCCC:010:1:0:0:1:0:1101:TTTT:000000000100")
	)
}

fun main(args: Array<String>) {
	// Create tables

	// 0xB590        - 1011_0_10_1_10010000:--------------- - PUSH {R4,R7,LR}
	// 0xE92D:0x41F0 - 1110_1_00_1_00101101:100_000111110000 - PUSH.W {R4-R8,LR}
}