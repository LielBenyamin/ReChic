package com.example.rechic.model

data class Country(
    val flags: Flags,
    val idd: Idd,
) {
    fun getPrefix(): String {
        return "${idd.root}${idd.suffixes?.firstOrNull() ?: ""}"
    }

    fun getFirstSuffixAsNumber(): Int {
        val prefix = getPrefix()
        return prefix.replace('+', ' ').trim().toIntOrNull() ?: 0
    }
}



