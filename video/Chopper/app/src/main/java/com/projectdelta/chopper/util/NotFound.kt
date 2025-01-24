package com.projectdelta.chopper.util

@Suppress("unused")
object NotFound {

	private val TextOnlyEmoticons = listOf(
		"(='X'=)", "^o^", "(·_·)", "(>_<)",
		"(≥o≤)", "(ㆆ _ ㆆ)", "(╥﹏╥)", "<(^_^)>",
		"=^_^=", "(-_-;)", "(*^_^*)", "(◠﹏◠)",
	)

	fun surpriseMe() = TextOnlyEmoticons.random()

	class TheFuckHappened(why: String = "") : IllegalStateException(why)

	class ItsYourFaultIdiotException(why: String = "") : IllegalAccessException(why)

}
