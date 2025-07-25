package com.structure.generator.structuregenerator.templates

import com.structure.generator.structuregenerator.common.PackageName
import com.structure.generator.structuregenerator.common.getApplicationPackage

internal fun activityTemplate(packageName: PackageName, baseName: String): String {

    val applicationPackageName = packageName.getApplicationPackage()

    return """
        package $packageName

        import android.os.Bundle
        import androidx.activity.compose.setContent
        import androidx.activity.viewModels
        import androidx.compose.foundation.layout.Box
        import androidx.compose.foundation.layout.fillMaxSize
        import androidx.compose.foundation.layout.padding
        import androidx.compose.material3.MaterialTheme
        import androidx.compose.material3.Scaffold
        import androidx.compose.material3.Surface
        import androidx.compose.ui.Modifier
        import androidx.navigation.NavHostController
        import androidx.navigation.compose.rememberNavController
        import androidx.compose.runtime.getValue
        import androidx.navigation.compose.currentBackStackEntryAsState
        import $applicationPackageName.R
        import $applicationPackageName.core.constants.SERVICE_ID
        import $applicationPackageName.core.constants.TITLE
        import $applicationPackageName.core.constants.LABEL
        import $applicationPackageName.ui.activities.BaseActivity
        import $applicationPackageName.ui.components.TopAppBar
        import $applicationPackageName.ui.theme.MyGovTheme


        class ${baseName}Activity : BaseActivity() {

            private val viewModel: ${baseName}ViewModel by viewModels()

            private lateinit var title: String
            private lateinit var label: String

            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)

                if (intent.hasExtra(SERVICE_ID)) {
                    title = intent.getStringExtra(TITLE) ?: getString(R.string.no_data_found)
                    label = intent.getStringExtra(LABEL) ?: getString(R.string.no_data_found)
                    viewModel.serviceId = intent.getStringExtra(SERVICE_ID) ?: getString(R.string.no_data_found)
                }

                setContent {

                    MyGovTheme(themeViewModel = settingsViewModel) {
                    
                        val navHostController: NavHostController = rememberNavController()
                        val navBackStackEntry by navHostController.currentBackStackEntryAsState()
                        val currentRoute = navBackStackEntry?.destination?.route
                        val currentScreen = ${baseName}NavHostSteps.findRoute(currentRoute)
                        val screenTitle = currentScreen?.titleRes?.let { getString(it) }

                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            Scaffold(
                                topBar = {
                                    TopAppBar(
                                        appBarTitle = screenTitle ?: title,
                                        onClick = {
                                            if (currentRoute == ${baseName}NavHostSteps.allScreens.first().route) {
                                                finish()
                                            } else {
                                                navHostController.popBackStack()
                                            }
                                        },
                                        label = label,
                                        context = this,
                                        serviceId = viewModel.serviceId,
                                        serviceName = serviceName,
                                        entityId = entityId,
                                        entityName = entityName
                                    )

                                },
                            ) {

                                Box(modifier = Modifier.padding(it)) {

                                    ${baseName}NavHost(
                                        navHostController = navHostController,
                                        viewModel = viewModel
                                    )

                                }

                            }
                        }

                    }

                }

            }
        }
    """.trimIndent()

}