package com.nimbusframework.nimbuslocal.deployment.services.usesresources

import com.nimbusframework.nimbuscore.annotations.notification.UsesNotificationTopic
import com.nimbusframework.nimbuscore.permissions.PermissionType
import com.nimbusframework.nimbuslocal.deployment.function.FunctionEnvironment
import com.nimbusframework.nimbuslocal.deployment.function.permissions.NotificationTopicPermission
import com.nimbusframework.nimbuslocal.deployment.services.LocalResourceHolder
import java.lang.reflect.Method

class LocalUsesNotificationTopicHandler(
        localResourceHolder: LocalResourceHolder,
        private val stage: String
): LocalUsesResourcesHandler(localResourceHolder) {

    override fun handleUsesResources(clazz: Class<out Any>, method: Method, functionEnvironment: FunctionEnvironment) {
        val usesNotificationTopics = method.getAnnotationsByType(UsesNotificationTopic::class.java)

        for (usesNotificationTopic in usesNotificationTopics) {
            if (usesNotificationTopic.stages.contains(stage)) {
                functionEnvironment.addPermission(PermissionType.NOTIFICATION_TOPIC, NotificationTopicPermission(usesNotificationTopic.topic))
            }
        }
    }
}