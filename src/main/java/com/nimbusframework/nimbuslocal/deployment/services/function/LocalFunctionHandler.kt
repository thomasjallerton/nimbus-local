package com.nimbusframework.nimbuslocal.deployment.services.function

import com.nimbusframework.nimbuslocal.deployment.function.FunctionEnvironment
import com.nimbusframework.nimbuslocal.deployment.function.FunctionIdentifier
import com.nimbusframework.nimbuslocal.deployment.services.LocalResourceHolder
import java.lang.reflect.Method

abstract class LocalFunctionHandler(
        private val localResourceHolder: LocalResourceHolder
) {

    protected abstract fun handleMethod(clazz: Class<out Any>, method: Method): Boolean

    fun createLocalFunctions(clazz: Class<out Any>, method: Method) {
        val functionIdentifier = FunctionIdentifier(clazz.name, method.name)
        if (handleMethod(clazz, method)) {
            if (!localResourceHolder.functionEnvironments.containsKey(functionIdentifier)) {
                localResourceHolder.functionEnvironments[functionIdentifier] = FunctionEnvironment()
            }
        }
    }

}