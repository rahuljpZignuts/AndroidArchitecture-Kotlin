package com.rahul.kotlin.architecture.extension

inline fun <reified T : Enum<T>> enumValueByName(
    name: String?, ignoreCase: Boolean = true,
): T? = runSafe {
    return@runSafe when {
        name.isNullOrEmpty() -> null
        ignoreCase -> enumValues<T>().find { it.name.equals(name, ignoreCase = true) }
        else -> enumValueOf<T>(name)
    }
}

inline fun <reified T : Enum<T>> enumValueByName(
    name: String?,
    fallback: T,
    ignoreCase: Boolean = true,
): T = enumValueByName<T>(name, ignoreCase) ?: fallback

inline fun <reified T : Enum<T>> enumValueByOrdinal(
    ordinal: Int,
    fallback: T,
): T = enumValues<T>().getOrNull(ordinal) ?: fallback
