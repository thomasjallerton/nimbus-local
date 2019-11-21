package com.nimbusframework.nimbuslocal.deployment.services.function

import com.nimbusframework.nimbuscore.annotations.function.KeyValueStoreServerlessFunction
import com.nimbusframework.nimbuscore.clients.keyvalue.AbstractKeyValueStoreClient
import com.nimbusframework.nimbuslocal.LocalNimbusDeployment
import com.nimbusframework.nimbuslocal.deployment.function.FunctionIdentifier
import com.nimbusframework.nimbuslocal.deployment.function.ServerlessFunction
import com.nimbusframework.nimbuslocal.deployment.function.information.KeyValueStoreFunctionInformation
import com.nimbusframework.nimbuslocal.deployment.keyvalue.KeyValueMethod
import com.nimbusframework.nimbuslocal.deployment.services.LocalResourceHolder
import java.lang.reflect.Method

class LocalKeyValueStoreFunctionHandler(
        private val localResourceHolder: LocalResourceHolder,
        private val stage: String
) : LocalFunctionHandler(localResourceHolder) {

    override fun handleMethod(clazz: Class<out Any>, method: Method): Boolean {
        val keyValueFunctions = method.getAnnotationsByType(KeyValueStoreServerlessFunction::class.java)
        if (keyValueFunctions.isEmpty()) return false

        val functionIdentifier = FunctionIdentifier(clazz.canonicalName, method.name)

        for (keyValueFunction in keyValueFunctions) {
            if (keyValueFunction.stages.contains(stage)) {
                val invokeOn = clazz.getConstructor().newInstance()

                val keyValueMethod = KeyValueMethod(method, invokeOn, keyValueFunction.method)
                val functionInformation = KeyValueStoreFunctionInformation(
                        keyValueFunction.dataModel.simpleName ?: "",
                        keyValueFunction.method
                )
                localResourceHolder.functions[functionIdentifier] = ServerlessFunction(keyValueMethod, functionInformation)
                val keyValueStore = localResourceHolder.keyValueStores[keyValueFunction.dataModel.java]
                keyValueStore?.addMethod(keyValueMethod)
            }
        }
        return true
    }
}