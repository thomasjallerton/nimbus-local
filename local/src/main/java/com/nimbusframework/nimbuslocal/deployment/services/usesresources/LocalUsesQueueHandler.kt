package com.nimbusframework.nimbuslocal.deployment.services.usesresources

import com.nimbusframework.nimbuscore.annotations.queue.UsesQueue
import com.nimbusframework.nimbuscore.clients.queue.QueueIdAnnotationService
import com.nimbusframework.nimbuscore.permissions.PermissionType
import com.nimbusframework.nimbuslocal.deployment.function.FunctionEnvironment
import com.nimbusframework.nimbuslocal.deployment.function.permissions.QueuePermission
import com.nimbusframework.nimbuslocal.deployment.services.LocalResourceHolder
import java.lang.reflect.Method

class LocalUsesQueueHandler(
        localResourceHolder: LocalResourceHolder,
        private val stage: String
) : LocalUsesResourcesHandler(localResourceHolder) {

    override fun handleUsesResources(clazz: Class<out Any>, method: Method, functionEnvironment: FunctionEnvironment) {
        val usesQueues = method.getAnnotationsByType(UsesQueue::class.java)

        for (usesQueue in usesQueues) {
            if (usesQueue.stages.contains(stage)) {
                val queueId = QueueIdAnnotationService.getQueueId(usesQueue.queue.java, stage)
                functionEnvironment.addPermission(PermissionType.QUEUE, QueuePermission(queueId))
            }
        }
    }

}