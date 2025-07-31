package com.structure.generator.structuregenerator.generator

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.structure.generator.structuregenerator.common.toPascalCase

class JsonToKotlinClassGenerator {

    private val classMap = mutableMapOf<String, KotlinClass>()

    fun generate(rootClassName: String, json: JsonObject): String {
        classMap.clear()
        parseJsonObject(json, rootClassName)
        return buildAllClasses()
    }

    private fun parseJsonObject(jsonObject: JsonObject, className: String) {
        if (classMap.containsKey(className)) return

        val kotlinClass = KotlinClass(className)
        for ((jsonKey, jsonElement) in jsonObject.entrySet()) {
            val fieldName = jsonKey.toCamelCase()
            val typeInfo = detectType(jsonKey, jsonElement)
            kotlinClass.fields.add(
                KotlinField(
                    jsonKey = jsonKey,
                    name = fieldName,
                    type = typeInfo.first,
                    defaultValue = typeInfo.second,
                    isNullable = true
                )
            )
        }

        classMap[className] = kotlinClass
    }

    private fun detectType(jsonKey: String, element: JsonElement): Pair<String, String?> {
        return when {
            element.isJsonPrimitive -> {
                val prim = element.asJsonPrimitive
                when {
                    prim.isBoolean -> "Boolean?" to "null"
                    prim.isNumber -> if (prim.asString.contains('.')) "Double?" to "null" else "Int?" to "null"
                    else -> "String?" to "null"
                }
            }
            element.isJsonObject -> {
                val nestedClassName = jsonKey.toPascalCase()
                parseJsonObject(element.asJsonObject, nestedClassName)
                "$nestedClassName?" to "null"
            }
            element.isJsonArray -> {
                val arr = element.asJsonArray
                if (arr.isEmpty) {
                    "ArrayList<Any>?" to "null"
                } else {
                    val first = arr.first()
                    if (first.isJsonObject) {
                        val itemClassName = jsonKey.toSingularPascalCase()
                        parseJsonObject(first.asJsonObject, itemClassName)
                        "ArrayList<$itemClassName>?" to "null"
                    } else {
                        val itemType = detectType(jsonKey, first).first.removeSuffix("?")
                        "ArrayList<$itemType>?" to "null"
                    }
                }
            }
            else -> "Any?" to "null"
        }
    }

    private fun buildAllClasses(): String {
        return classMap.values.joinToString("\n\n") { buildClassCode(it) }
    }

    private fun buildClassCode(kClass: KotlinClass): String {
        val sb = StringBuilder()
        sb.append("data class ${kClass.className}(\n")
        sb.append(
            kClass.fields.joinToString(",\n") { field ->
                val default = field.defaultValue?.let { " = $it" } ?: ""
                """    @field:SerializedName("${field.jsonKey}")
    var ${field.name}: ${field.type}$default"""
            }
        )
        sb.append("\n)")
        return sb.toString()
    }

    // Naming helpers
    private fun String.toCamelCase(): String = split('_').joinToString("") { it.capitalize() }.replaceFirstChar { it.lowercase() }
    private fun String.toSingularPascalCase(): String {
        val base = this.removeSuffix("s")
        return base.toPascalCase()
    }
}
