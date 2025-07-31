package com.structure.generator.structuregenerator.generator

data class KotlinClass(
    val className: String,
    val fields: MutableList<KotlinField> = mutableListOf()
)