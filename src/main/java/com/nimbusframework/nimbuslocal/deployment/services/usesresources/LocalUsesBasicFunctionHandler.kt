package com.nimbusframework.nimbuslocal.deployment.services.usesresources

import com.nimbusframework.nimbuscore.annotations.function.UsesBasicServerlessFunction
import com.nimbusframework.nimbuscore.permissions.PermissionType
import com.nimbusframework.nimbuslocal.deployment.function.FunctionEnvironment
import com.nimbusframework.nimbuslocal.deployment.function.permissions.BasicFunctionPermission
import com.nimbusframework.nimbuslocal.deployment.services.LocalResourceHolder
import java.lang.reflect.Method

class LocalUsesBasicFunctionHandler(
        localResourceHolder: LocalResourceHolder,
        private val stage: String
): LocalUsesResourcesHandler(localResourceHolder) {

    override fun handleUsesResources(clazz: Class<out Any>, method: Method, functionEnvironment: FunctionEnvironment) {
        val usesBasicFunctionClients = method.getAnnotationsByType(UsesBasicServerlessFunction::class.java)

        for (usesBasicFunctionClient in usesBasicFunctionClients) {
            if (usesBasicFunctionClient.stages.contains(stage)) {
                functionEnvironment.addPermission(PermissionType.BASIC_FUNCTION, BasicFunctionPermission(
                        usesBasicFunctionClient.targetClass.java,
                        usesBasicFunctionClient.methodName
                ))
            }
        }
    }

}