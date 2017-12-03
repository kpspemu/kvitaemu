package com.soywiz.kvitaemu

//ARM® Architecture Reference Manual  ARMv7-A and ARMv7-R edition
object ArmTables {
	enum class Mode { ARM, THUMB }

	val THU = Mode.THUMB
	val ARM = Mode.ARM

	val tables = listOf(
		// A8.8.133 PUSH
		TableFormat(THU, 1, "PUSH", "1011:0:10:M:RRRRRRRR"),
		TableFormat(THU, 2, "PUSH", "11101:00:100:1:0:1101", "-:M:-:RRRRRRRRRRRRR"),
		TableFormat(THU, 3, "PUSH", "11111:00:0:0:10:0:1101", "TTTT:1:101:00000100"),
		TableFormat(ARM, 1, "PUSH", "CCCC:100100:1:0:1101:RRRRRRRRRRRRRRRR"),
		TableFormat(ARM, 2, "PUSH", "CCCC:010:1:0:0:1:0:1101:TTTT:000000000100")
	)
}
