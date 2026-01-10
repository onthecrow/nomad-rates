package com.onthecrow.nomadrates.ui.util

import nomadrates.core.ui.generated.resources.Res

fun String?.toFlagResourceUri(): String {
    return this?.let {
        try {
            Res.getUri("files/flags/${this.lowercase()}.svg")
        } catch (error: Throwable) {
            println("error: $error")
            null
        }
        // TODO need to implement a placeholder or smth
    } ?: Res.getUri("files/flags/ae.svg")
}
