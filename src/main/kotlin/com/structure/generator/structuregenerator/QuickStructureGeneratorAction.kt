package com.structure.generator.structuregenerator

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.ui.Messages
import com.intellij.psi.JavaDirectoryService
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.structure.generator.structuregenerator.common.PackageName
import com.structure.generator.structuregenerator.common.createFile
import com.structure.generator.structuregenerator.common.extractPackage
import com.structure.generator.structuregenerator.common.toPascalCase
import com.structure.generator.structuregenerator.templates.activityTemplate
import com.structure.generator.structuregenerator.templates.defaultScreenTemplate
import com.structure.generator.structuregenerator.templates.lastScreenTemplate
import com.structure.generator.structuregenerator.templates.navHostStepsTemplate
import com.structure.generator.structuregenerator.templates.navHostTemplate
import com.structure.generator.structuregenerator.templates.repositoryTemplate
import com.structure.generator.structuregenerator.templates.viewModelTemplate
import com.structure.generator.structuregenerator.ui.FeatureInputDialog
import java.io.File
import kotlin.String

class QuickStructureGeneratorAction : AnAction("Falcon Structure Generator") {

    override fun actionPerformed(e: AnActionEvent) {
        val dialog = FeatureInputDialog()
        if (!dialog.showAndGet()) return

        val createdPackageName = dialog.getModuleName()
        val baseName = dialog.getBaseName().ifBlank { createdPackageName.toPascalCase() }
        val screens = dialog.getScreens()

        if (createdPackageName.isBlank() || screens.isEmpty()) {
            return
        }

        val psiElement = e.getData(LangDataKeys.PSI_ELEMENT)
        val psiDirectory = when (psiElement) {
            is PsiDirectory -> psiElement
            is PsiFile -> psiElement.containingDirectory
            else -> null
        } ?: run {
            Messages.showErrorDialog("Please right-click on a folder or package.", "Invalid Location")
            return
        }
        val clickedDir = psiDirectory.virtualFile.path
        val baseDir = File(clickedDir, createdPackageName)
        val clickedPackageName = JavaDirectoryService.getInstance()
            .getPackage(psiDirectory)?.qualifiedName ?: File(clickedDir).extractPackage("src/")


        listOf("models", "components", "screens", "repository").forEach {
            File(baseDir, it).mkdirs()
        }

        generateCoreFiles(
            baseDir = baseDir,
            baseName = baseName,
            screens = screens,
            fullPackageName = "$clickedPackageName.$createdPackageName"
        )

        val localFileSystem = com.intellij.openapi.vfs.LocalFileSystem.getInstance()
        localFileSystem.refreshAndFindFileByIoFile(baseDir)?.let { vFile ->
            com.intellij.openapi.vfs.VfsUtil.markDirtyAndRefresh(
                true,
                true,
                true,
                vFile
            )
        }

        Messages.showInfoMessage("Feature module '$baseName' created!", "Success")
    }

    private fun generateCoreFiles(
        baseDir: File,
        baseName: String,
        screens: List<String>,
        fullPackageName: PackageName
    ) {

        createFile(
            baseDir,
            "${baseName}Activity.kt",
            activityTemplate(packageName = fullPackageName, baseName = baseName)
        )

        createFile(
            baseDir,
            "${baseName}ViewModel.kt",
            viewModelTemplate(packageName = fullPackageName, baseName = baseName)
        )

        createFile(
            baseDir,
            "${baseName}NavHost.kt",
            navHostTemplate(
                screens = screens,
                packageName = fullPackageName,
                baseName = baseName
            )
        )

        createFile(
            baseDir,
            "${baseName}NavHostSteps.kt",
            navHostStepsTemplate(
                screens = screens,
                packageName = fullPackageName,
                baseName = baseName
            )
        )

        createFile(
            File(baseDir, "repository"),
            "${baseName}Repository.kt",
            repositoryTemplate(packageName = fullPackageName, baseName = baseName)
        )

        screens.dropLast(1).forEach {
            createFile(
                File(baseDir, "screens"),
                "$it.kt",
                defaultScreenTemplate(packageName = fullPackageName, screenName = it, baseName = baseName)
            )
        }

        createFile(
            File(baseDir, "screens"),
            "${screens.last()}.kt",
            lastScreenTemplate(packageName = fullPackageName, screenName = screens.last(), baseName = baseName)
        )

    }

}