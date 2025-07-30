package com.structure.generator.structuregenerator.ui

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.components.JBTextField
import java.awt.BorderLayout
import java.awt.Color
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

    private val classNameField = JBTextField().apply {
        emptyText.text = "ClassName"
    }

    private val jsonTextArea = JTextArea(8, 40).apply {
        lineWrap = true
        wrapStyleWord = true
    }

    private val statusLabel = JLabel("")

    private val addButton = JButton("Add JSON")

    private val validJsonObjects = mutableListOf<Pair<String, JsonObject>>()

    init {
        init()
        title = "Generate Feature Module"
        addButton.addActionListener {
            onAddJson()
        }
    }

    override fun createCenterPanel(): JComponent {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
        panel.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
        panel.preferredSize = Dimension(500, 600)

        panel.add(createLabeledField("Module Name", moduleNameField))
        panel.add(Box.createVerticalStrut(10))
        panel.add(createLabeledField("Base File Name (optional)", baseNameField))
        panel.add(Box.createVerticalStrut(10))
        panel.add(createLabeledField("Screens (comma-separated)", screensField))
        panel.add(Box.createVerticalStrut(10))
        panel.add(createLabeledField("Class Name", classNameField))
        panel.add(Box.createVerticalStrut(10))
        panel.add(createLabeledArea("JSON Input", jsonTextArea))
        panel.add(Box.createVerticalStrut(10))
        panel.add(addButton)
        panel.add(Box.createVerticalStrut(10))
        panel.add(statusLabel)

        return panel
    }

    override fun doValidate(): ValidationInfo? {
        return when {
            moduleNameField.text.isBlank() -> ValidationInfo("Module name is required", moduleNameField)
            screensField.text.isBlank() -> ValidationInfo("At least one screen must be provided", screensField)
            else -> null
        }
    }

    private fun onAddJson() {
        val className = classNameField.text.trim()
        val jsonText = jsonTextArea.text.trim()

        if (className.isBlank()) {
            statusLabel.text = "Class name cannot be empty."
            return
        }

        if (jsonText.isBlank()) {
            statusLabel.text = "JSON cannot be empty."
            return
        }

        try {
            val jsonElement = JsonParser.parseString(jsonText)
            if (!jsonElement.isJsonObject) {
                showStatusMessage("JSON format is not valid '$className'.", false)
                return
            }

            validJsonObjects.add(className to jsonElement.asJsonObject)
            jsonTextArea.text = ""
            classNameField.text = ""
            showStatusMessage("JSON added successfully for class '$className'.", true)
        } catch (e: Exception) {
            statusLabel.text = "Invalid JSON: ${e.message}"
        }
    }

    private fun showStatusMessage(message: String, isSuccess: Boolean) {
        statusLabel.text = message
        statusLabel.foreground = if (isSuccess) Color(0, 128, 0) else Color(200, 0, 0)

        Timer(2000) {
            statusLabel.text = ""
        }.apply {
            isRepeats = false
            start()
        }
    }

    private fun createLabeledField(labelText: String, textField: JBTextField): JPanel {
        val panel = JPanel(BorderLayout(5, 5))
        val label = JLabel(labelText)
        panel.add(label, BorderLayout.NORTH)
        panel.add(textField, BorderLayout.CENTER)
        return panel
    }

    private fun createLabeledArea(labelText: String, textArea: JTextArea): JPanel {
        val panel = JPanel(BorderLayout(5, 5))
        val label = JLabel(labelText)
        panel.add(label, BorderLayout.NORTH)
        panel.add(JScrollPane(textArea), BorderLayout.CENTER)
        return panel
    }

    fun getModuleName() = moduleNameField.text.trim()

    fun getBaseName() = baseNameField.text.trim()

    fun getScreens(): List<String> = screensField.text
        .split(",")
        .map { it.trim() }
        .filter { it.isNotEmpty() }

    fun getCollectedJson(): List<Pair<String, JsonObject>> = validJsonObjects
}
