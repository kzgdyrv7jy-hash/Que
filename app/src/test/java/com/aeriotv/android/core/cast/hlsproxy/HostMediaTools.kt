package com.aeriotv.android.core.cast.hlsproxy

import java.io.File

/** Find host tools on Linux/macOS instead of assuming a Homebrew installation. */
internal fun hostMediaTool(name: String): File {
    val override = System.getenv("${name.uppercase()}_BIN")?.takeIf { it.isNotBlank() }
    val paths = System.getenv("PATH").orEmpty().split(File.pathSeparatorChar)
        .filter { it.isNotBlank() }.map { File(it, name) }
    return (listOfNotNull(override?.let(::File)) + paths + File("/opt/homebrew/bin", name))
        .firstOrNull { it.isFile && it.canExecute() } ?: File(name)
}
