package com.nimbusframework.nimbuslocal.deployment.services.resource

import com.nimbusframework.nimbuscore.annotations.queue.QueueDefinition
import com.nimbusframework.nimbuslocal.deployment.queue.LocalQueue
import com.nimbusframework.nimbuslocal.deployment.services.LocalResourceHolder

class LocalQueueCreator(
        private val localResourceHolder: LocalResourceHolder,
        private val stage: String
) : LocalCreateResourcesHandler {

    override fun createResource(clazz: Class<out Any>) {
        val queues = clazz.getAnnotationsByType(QueueDefinition::class.java)

        for (queue in queues) {
            if (queue.stages.contains(stage)) {
                localResourceHolder.queues[queue.queueId] = LocalQueue()
            }
        }

    }

}