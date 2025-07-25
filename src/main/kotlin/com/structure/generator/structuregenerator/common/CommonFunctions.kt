package com.structure.generator.structuregenerator.common

import java.io.File

fun createFile(dir: File, name: String, content: String) {
    File(dir, name).writeText(content)
}