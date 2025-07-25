package com.structure.generator.structuregenerator.templates

import com.structure.generator.structuregenerator.common.getApplicationPackage

internal fun repositoryTemplate(packageName: String, baseName: String): String {

    val applicationPackage = packageName.getApplicationPackage()
    val repositoryName = "${baseName}Repository"

    return """
        package $packageName.repository

        import $applicationPackage.core.network.services.DGMobileService
        import javax.inject.Inject

        class  $repositoryName @Inject constructor(val dgMobileService: DGMobileService) {
            
            // suspend fun getData() = dgMobileService.getData()

        }
    """.trimIndent()

}