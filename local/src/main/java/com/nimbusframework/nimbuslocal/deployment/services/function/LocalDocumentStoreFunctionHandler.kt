package com.nimbusframework.nimbuslocal.deployment.services.function

import com.nimbusframework.nimbuscore.annotations.function.DocumentStoreServerlessFunction
import com.nimbusframework.nimbuscore.clients.document.AbstractDocumentStoreClient
import com.nimbusframework.nimbuslocal.LocalNimbusDeployment
import com.nimbusframework.nimbuslocal.deployment.document.DocumentMethod
import com.nimbusframework.nimbuslocal.deployment.function.FunctionIdentifier
import com.nimbusframework.nimbuslocal.deployment.function.ServerlessFunction
import com.nimbusframework.nimbuslocal.deployment.function.information.DocumentStoreFunctionInformation
import com.nimbusframework.nimbuslocal.deployment.services.LocalResourceHolder
import java.lang.reflect.Method

class LocalDocumentStoreFunctionHandler(
        private val localResourceHolder: LocalResourceHolder,
        private val stage: String
) : LocalFunctionHandler(localResourceHolder) {

    override fun handleMethod(clazz: Class<out Any>, method: Method): Boolean {
        val documentFunctions = method.getAnnotationsByType(DocumentStoreServerlessFunction::class.java)
        if (documentFunctions.isEmpty()) return false

        val functionIdentifier = FunctionIdentifier(clazz.canonicalName, method.name)

        for (documentFunction in documentFunctions) {
            if (documentFunction.stages.contains(stage)) {
                val invokeOn = clazz.getConstructor().newInstance()

                val documentMethod = DocumentMethod(method, invokeOn, documentFunction.method)
                val functionInformation = DocumentStoreFunctionInformation(
                        documentFunction.dataModel.simpleName ?: "",
                        documentFunction.method
                )
                localResourceHolder.functions[functionIdentifier] = ServerlessFunction(documentMethod, functionInformation)
                val documentStore = localResourceHolder.documentStores[documentFunction.dataModel.java]
                documentStore?.addMethod(documentMethod)
            }
        }
        return true
    }
}