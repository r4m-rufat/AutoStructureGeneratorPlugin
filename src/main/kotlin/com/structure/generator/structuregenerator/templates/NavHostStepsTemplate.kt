package com.structure.generator.structuregenerator.templates

import com.structure.generator.structuregenerator.common.PackageName
import com.structure.generator.structuregenerator.common.getApplicationPackage

internal fun navHostStepsTemplate(packageName: PackageName, baseName: String, screens: List<String>): String {

    val className = "${baseName}NavHostSteps"

    val sb = StringBuilder()
    sb.appendLine("package $packageName")
    sb.appendLine()
    sb.appendLine("import androidx.annotation.StringRes")
    sb.appendLine("import ${packageName.getApplicationPackage()}.R")
    sb.appendLine()
    sb.appendLine("sealed class $className(val route: String, @StringRes val titleRes: Int? = null) {")

    sb.appendLine()

    screens.forEach {
        sb.appendLine("    data object $it: $className(\"$it\")")
    }

    sb.appendLine()
    sb.appendLine("    companion object {")
    sb.appendLine()
    sb.appendLine("        val allScreens = listOf(${screens.joinToString(", ")})")
    sb.appendLine()
    sb.appendLine("        fun findRoute(route: String?): $className? = allScreens.find { it.route == route }")
    sb.appendLine()
    sb.appendLine("    }")
    sb.appendLine("}")

    return sb.toString()

}