package com.structure.generator.structuregenerator.templates

import com.structure.generator.structuregenerator.common.PackageName
import com.structure.generator.structuregenerator.common.getApplicationPackage

internal fun viewModelTemplate(packageName: PackageName, baseName: String): String {

    val viewModelName = "${baseName}ViewModel"
    val repositoryName = "${baseName}Repository"
    val applicationPackageName = packageName.getApplicationPackage()

    return """
         package $packageName
         
         import androidx.lifecycle.ViewModel
         import androidx.lifecycle.viewModelScope
         import ${applicationPackageName}.core.models.LCE
         import ${applicationPackageName}.core.tools.errorCodeAndMessage
         import ${packageName}.repository.${baseName}Repository
         import dagger.hilt.android.lifecycle.HiltViewModel
         import kotlinx.coroutines.Dispatchers.IO
         import kotlinx.coroutines.flow.MutableStateFlow
         import kotlinx.coroutines.flow.asStateFlow
         import kotlinx.coroutines.launch
         import okhttp3.ResponseBody
         import javax.inject.Inject

         @HiltViewModel
         class $viewModelName @Inject constructor(
             private val repository: $repositoryName
         ) : ViewModel() {

             lateinit var serviceId: String

             private val _state = MutableStateFlow<LCE<ResponseBody>?>(null)
             val state = _state.asStateFlow()
             
             /**
             fun getData() = viewModelScope.launch(IO) {

                 _state.value = LCE.loading()

                 try {
                     repository.getData().also {
                         if (it.isSuccessful && it.body() != null) {
                             _state.value = LCE.content(it.body())
                         } else {
                             val error = it.errorBody()?.string().errorCodeAndMessage()
                             _state.value =
                                 LCE.error(code = error?.first, errorMessage = error?.second)
                         }
                     }
                 } catch (e: Exception) {
                     e.printStackTrace()
                     _state.value = LCE.error()
                 }

             }
             */

         }
         
    """.trimIndent()


}