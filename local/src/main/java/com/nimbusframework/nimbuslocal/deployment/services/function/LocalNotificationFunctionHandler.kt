package com.nimbusframework.nimbuslocal.deployment.services.function

import com.nimbusframework.nimbuscore.annotations.function.NotificationServerlessFunction
import com.nimbusframework.nimbuscore.annotations.notification.NotificationTopicDefinition
import com.nimbusframework.nimbuscore.clients.notification.NotificationTopic
import com.nimbusframework.nimbuscore.clients.notification.NotificationTopicAnnotationService
import com.nimbusframework.nimbuscore.wrappers.annotations.datamodel.DataModelAnnotation
import com.nimbusframework.nimbuscore.wrappers.annotations.datamodel.NotificationTopicServerlessFunctionAnnotation
import com.nimbusframework.nimbuslocal.deployment.function.FunctionIdentifier
import com.nimbusframework.nimbuslocal.deployment.function.ServerlessFunction
import com.nimbusframework.nimbuslocal.deployment.function.information.NotificationFunctionInformation
import com.nimbusframework.nimbuslocal.deployment.notification.LocalNotificationTopic
import com.nimbusframework.nimbuslocal.deployment.notification.NotificationMethod
import com.nimbusframework.nimbuslocal.deployment.services.LocalResourceHolder
import java.lang.reflect.Method

class LocalNotificationFunctionHandler(
        private val localResourceHolder: LocalResourceHolder,
        private val stage: String
) : LocalFunctionHandler(localResourceHolder) {

    override fun handleMethod(clazz: Class<out Any>, method: Method): Boolean {
        val notificationServerlessFunctions = method.getAnnotationsByType(NotificationServerlessFunction::class.java)
        if (notificationServerlessFunctions.isEmpty()) return false

        val functionIdentifier = FunctionIdentifier(clazz.canonicalName, method.name)

        for (notificationFunction in notificationServerlessFunctions) {
            if (notificationFunction.stages.contains(stage)) {
                val invokeOn = clazz.getConstructor().newInstance()

                val notificationMethod = NotificationMethod(method, invokeOn)
                val topicName = NotificationTopicAnnotationService.getTopicName(notificationFunction.notificationTopic.java, stage)
                val functionInformation = NotificationFunctionInformation(topicName)
                val notificationTopic = localResourceHolder.notificationTopics[topicName]!!

                notificationTopic.addSubscriber(notificationMethod)
                localResourceHolder.functions[functionIdentifier] = ServerlessFunction(notificationMethod, functionInformation)
            }
        }
        return true
    }

}