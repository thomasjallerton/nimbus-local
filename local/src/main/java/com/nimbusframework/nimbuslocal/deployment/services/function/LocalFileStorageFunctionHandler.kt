package com.nimbusframework.nimbuslocal.deployment.services.function

import com.nimbusframework.nimbuscore.annotations.function.FileStorageServerlessFunction
import com.nimbusframework.nimbuslocal.deployment.file.FileStorageMethod
import com.nimbusframework.nimbuslocal.deployment.file.LocalFileStorage
import com.nimbusframework.nimbuslocal.deployment.function.FunctionIdentifier
import com.nimbusframework.nimbuslocal.deployment.function.ServerlessFunction
import com.nimbusframework.nimbuslocal.deployment.function.information.FileStorageFunctionInformation
import com.nimbusframework.nimbuslocal.deployment.services.LocalResourceHolder
import java.lang.reflect.Method

class LocalFileStorageFunctionHandler(
        private val localResourceHolder: LocalResourceHolder,
        private val stage: String
) : LocalFunctionHandler(localResourceHolder) {

    override fun handleMethod(clazz: Class<out Any>, method: Method): Boolean {

        val fileStorageFunctions = method.getAnnotationsByType(FileStorageServerlessFunction::class.java)
        if (fileStorageFunctions.isEmpty()) return false

        val functionIdentifier = FunctionIdentifier(clazz.canonicalName, method.name)

        val fileStorage = localResourceHolder.fileStorage
        val methods = localResourceHolder.functions

        for (fileStorageFunction in fileStorageFunctions) {
            if (fileStorageFunction.stages.contains(stage)) {
                val invokeOn = clazz.getConstructor().newInstance()

                if (!fileStorage.containsKey(fileStorageFunction.bucketName)) {
                    fileStorage[fileStorageFunction.bucketName] = LocalFileStorage(fileStorageFunction.bucketName, listOf(""))
                }
                val localFileStorage = fileStorage[fileStorageFunction.bucketName]
                val fileStorageMethod = FileStorageMethod(method, invokeOn, fileStorageFunction.eventType)
                val functionInformation = FileStorageFunctionInformation(
                        fileStorageFunction.bucketName,
                        fileStorageFunction.eventType
                )
                localFileStorage!!.addMethod(fileStorageMethod)
                methods[functionIdentifier] = ServerlessFunction(fileStorageMethod, functionInformation)
            }
        }
        return true
    }

}