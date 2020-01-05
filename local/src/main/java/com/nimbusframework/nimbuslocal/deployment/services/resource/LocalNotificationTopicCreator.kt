package com.nimbusframework.nimbuslocal.deployment.services.resource

import com.nimbusframework.nimbuscore.annotations.notification.NotificationTopicDefinition
import com.nimbusframework.nimbuscore.annotations.notification.UsesNotificationTopic
import com.nimbusframework.nimbuslocal.deployment.notification.LocalNotificationTopic
import com.nimbusframework.nimbuslocal.deployment.services.LocalResourceHolder

class LocalNotificationTopicCreator(
        private val localResourceHolder: LocalResourceHolder,
        private val stage: String
) : LocalCreateResourcesHandler {

    override fun createResource(clazz: Class<out Any>) {
        val notificationTopics = clazz.getAnnotationsByType(NotificationTopicDefinition::class.java)

        for (notificationTopic in notificationTopics) {
            if (notificationTopic.stages.contains(stage)) {
                localResourceHolder.notificationTopics[notificationTopic.topicName] = LocalNotificationTopic()
            }
        }

    }

}