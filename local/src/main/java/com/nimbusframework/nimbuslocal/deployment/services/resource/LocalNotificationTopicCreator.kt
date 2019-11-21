package com.nimbusframework.nimbuslocal.deployment.services.resource

import com.nimbusframework.nimbuscore.annotations.notification.UsesNotificationTopic
import com.nimbusframework.nimbuslocal.deployment.notification.LocalNotificationTopic
import com.nimbusframework.nimbuslocal.deployment.services.LocalResourceHolder

class LocalNotificationTopicCreator(
        private val localResourceHolder: LocalResourceHolder,
        private val stage: String
): LocalCreateResourcesHandler {

    override fun createResource(clazz: Class<out Any>) {
        for (method in clazz.methods) {
            val usesNotificationTopics = method.getAnnotationsByType(UsesNotificationTopic::class.java)

            for (usesNotificationTopic in usesNotificationTopics) {
                if (usesNotificationTopic.stages.contains(stage)) {
                    localResourceHolder.notificationTopics.putIfAbsent(usesNotificationTopic.topic, LocalNotificationTopic())
                }
            }
        }
    }

}