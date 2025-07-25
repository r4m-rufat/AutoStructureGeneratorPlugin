package com.structure.generator.structuregenerator.templates

import com.structure.generator.structuregenerator.common.PackageName
import com.structure.generator.structuregenerator.common.getApplicationPackage

internal fun lastScreenTemplate(packageName: PackageName, screenName: String, baseName: String): String {

    val applicationPackageName = packageName.getApplicationPackage()


    return """
        package $packageName.screens

        import androidx.compose.foundation.layout.Box
        import androidx.compose.foundation.layout.fillMaxSize
        import androidx.compose.foundation.layout.padding
        import androidx.compose.material3.CircularProgressIndicator
        import androidx.compose.runtime.Composable
        import androidx.compose.runtime.collectAsState
        import androidx.compose.runtime.getValue
        import androidx.compose.ui.Alignment
        import androidx.compose.ui.Modifier
        import androidx.compose.ui.unit.dp
        import $applicationPackageName.core.models.Status
        import $packageName.${baseName}ViewModel
        import $applicationPackageName.ui.components.ErrorView
        import $applicationPackageName.ui.components.NoDataView

        @Composable
        fun $screenName(
            viewModel: ${baseName}ViewModel
        ) {

            val state by viewModel.state.collectAsState()

            /**
            LaunchedEffect(key1 = Unit) {
            viewModel.getData()
            }
             */

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {

                when (state?.status) {

                    null, Status.LOADING -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }

                    Status.CONTENT -> {

                    }

                    Status.ERROR -> {
                        if (state?.code == 404) {
                            NoDataView(modifier = Modifier.align(Alignment.Center))
                        } else {
                            ErrorView(
                                modifier = Modifier.align(Alignment.Center),
                                message = state?.errorMessage
                            )
                        }
                    }

                }

            }
        }
    """.trimIndent()

}