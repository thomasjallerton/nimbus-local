package com.nimbusframework.nimbuslocal.deployment.services.function

import com.nimbusframework.nimbuscore.annotations.deployment.AfterDeployment
import com.nimbusframework.nimbuslocal.deployment.afterdeployment.AfterDeploymentMethod
import com.nimbusframework.nimbuslocal.deployment.services.LocalResourceHolder
import java.lang.reflect.Method

class LocalAfterDeploymentHandler(
        private val localResourceHolder: LocalResourceHolder,
        private val stage: String
) : LocalFunctionHandler(localResourceHolder) {

    override fun handleMethod(clazz: Class<out Any>, method: Method): Boolean {
        val afterDeployments = method.getAnnotationsByType(AfterDeployment::class.java)
        if (afterDeployments.isEmpty()) return false

        for (afterDeployment in afterDeployments) {
            if (afterDeployment.stages.contains(stage)) {
                val invokeOn = clazz.getConstructor().newInstance()

                if (afterDeployment.isTest) {
                    localResourceHolder.afterDeployments.addLast(AfterDeploymentMethod(method, invokeOn))
                } else {
                    localResourceHolder.afterDeployments.addFirst(AfterDeploymentMethod(method, invokeOn))
                }
            }
        }
        return true
    }

}