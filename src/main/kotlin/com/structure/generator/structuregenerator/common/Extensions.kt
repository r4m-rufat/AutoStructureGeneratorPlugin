package com.structure.generator.structuregenerator.common

import java.io.File

typealias PackageName = String

fun PackageName.getApplicationPackage(): String = split(".").take(3).joinToString(".")

fun String.toPascalCase() = split("_")
    .joinToString("") { word ->
        word.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase() else it.toString()
        }
    }

fun File.extractPackage(baseSrcPath: String): String {
    val srcIndex = path.indexOf(baseSrcPath)
    if (srcIndex == -1) return ""
    val relativePath = path.substring(srcIndex + baseSrcPath.length)
    return relativePath.trimStart(File.separatorChar)
        .replace(File.separatorChar, '.')
        .trim('.')
}