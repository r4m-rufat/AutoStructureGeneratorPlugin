package com.structure.generator.structuregenerator.generator

data class KotlinField(
    val jsonKey: String,
    val name: String,
    val type: String,
    val defaultValue: String?,
    val isNullable: Boolean
)