package com.structure.generator.structuregenerator.templates

internal fun navHostTemplate(screens: List<String>, packageName: String, baseName: String): String {

    val viewModel = "${baseName}ViewModel"
    val sb = StringBuilder()

    sb.appendLine("package $packageName")
    sb.appendLine()
    sb.appendLine("import androidx.compose.runtime.Composable")
    sb.appendLine("import androidx.navigation.NavHostController")
    sb.appendLine("import androidx.navigation.compose.NavHost")
    sb.appendLine("import androidx.navigation.compose.composable")
    screens.forEach {
        sb.appendLine("import $packageName.${baseName}NavHostSteps.$it")
    }
    screens.forEach {
        sb.appendLine("import $packageName.screens.$it")
    }
    sb.appendLine()
    sb.appendLine("@Composable")
    sb.appendLine("fun ${baseName}NavHost(navHostController: NavHostController, viewModel: $viewModel) {")
    sb.appendLine()
    sb.appendLine("    NavHost(navController = navHostController, startDestination = ${screens.first()}.route) {")
    sb.appendLine()

    screens.dropLast(1).forEach {
        sb.appendLine("        composable(route = $it.route) {")
        sb.appendLine("            $it(navHostController, viewModel)")
        sb.appendLine("        }")
        sb.appendLine()
    }

    screens.lastOrNull()?.let {
        sb.appendLine("        composable(route = $it.route) {")
        sb.appendLine("            $it(viewModel)")
        sb.appendLine("        }")
        sb.appendLine()
    }

    sb.appendLine("    }")
    sb.appendLine("}")

    return sb.toString()

}