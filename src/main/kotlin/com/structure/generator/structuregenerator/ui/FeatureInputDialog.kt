package com.structure.generator.structuregenerator.ui

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.components.JBTextField
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.*

class FeatureInputDialog : DialogWrapper(true) {

    private val moduleNameField = JBTextField().apply {
        emptyText.text = "my_feature"
    }
    private val baseNameField = JBTextField().apply {
        emptyText.text = "MyNewFeature"
    }
    private val screensField = JBTextField().apply {
        emptyText.text = "FirstScreen, SecondScreen, etc"
    }

    init {
        init()
        title = "Generate Feature Module"
    }

    override fun createCenterPanel(): JComponent {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
        panel.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
        panel.preferredSize = Dimension(400, preferredSize.height)

        panel.add(createLabeledField("Module Name", moduleNameField))
        panel.add(Box.createVerticalStrut(15))
        panel.add(createLabeledField("Base File Name (optional)", baseNameField))
        panel.add(Box.createVerticalStrut(15))
        panel.add(createLabeledField("Screens (comma-separated)", screensField))

        return panel
    }

    override fun doValidate(): ValidationInfo? {
        return when {
            moduleNameField.text.isBlank() -> {
                ValidationInfo("Module name is required", moduleNameField)
            }
            screensField.text.isBlank() -> {
                ValidationInfo("At least one screen must be provided", screensField)
            }
            else -> null
        }
    }

    private fun createLabeledField(labelText: String, textField: JBTextField): JPanel {
        val panel = JPanel()
        panel.layout = BorderLayout(5, 5)
        val label = JLabel(labelText)
        panel.add(label, BorderLayout.NORTH)
        panel.add(textField, BorderLayout.CENTER)
        return panel
    }

    fun getModuleName() = moduleNameField.text.trim()
    fun getBaseName() = baseNameField.text.trim()
    fun getScreens() = screensField.text
        .split(",")
        .map { it.trim() }
        .filter { it.isNotEmpty() }
}