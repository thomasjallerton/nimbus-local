package com.nimbusframework.nimbuslocal.deployment.services.function

import com.nimbusframework.nimbuscore.annotations.function.QueueServerlessFunction
import com.nimbusframework.nimbuscore.clients.queue.QueueIdAnnotationService
import com.nimbusframework.nimbuslocal.deployment.function.FunctionIdentifier
import com.nimbusframework.nimbuslocal.deployment.function.ServerlessFunction
import com.nimbusframework.nimbuslocal.deployment.function.information.QueueFunctionInformation
import com.nimbusframework.nimbuslocal.deployment.queue.LocalQueue
import com.nimbusframework.nimbuslocal.deployment.queue.QueueMethod
import com.nimbusframework.nimbuslocal.deployment.services.LocalResourceHolder
import java.lang.reflect.Method

class LocalQueueFunctionHandler(
        private val localResourceHolder: LocalResourceHolder,
        private val stage: String
) : LocalFunctionHandler(localResourceHolder) {

    override fun handleMethod(clazz: Class<out Any>, method: Method): Boolean {
        val queueServerlessFunctions = method.getAnnotationsByType(QueueServerlessFunction::class.java)
        if (queueServerlessFunctions.isEmpty()) return false

        val functionIdentifier = FunctionIdentifier(clazz.canonicalName, method.name)

        for (queueFunction in queueServerlessFunctions) {
            if (queueFunction.stages.contains(stage)) {
                val invokeOn = clazz.getConstructor().newInstance()

                val queueMethod = QueueMethod(method, invokeOn, queueFunction.batchSize)
                val queueId = QueueIdAnnotationService.getQueueId(queueFunction.queue.java, stage)

                val functionInformation = QueueFunctionInformation(queueId, queueFunction.batchSize)
                val queue = localResourceHolder.queues[queueId]!!

                queue.addConsumer(queueMethod)
                localResourceHolder.functions[functionIdentifier] = ServerlessFunction(queueMethod, functionInformation)
            }
        }
        return true
    }

}