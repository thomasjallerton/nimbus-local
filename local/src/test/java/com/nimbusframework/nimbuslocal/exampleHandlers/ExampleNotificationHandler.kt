package com.nimbusframework.nimbuslocal.exampleHandlers

import com.nimbusframework.nimbuscore.annotations.function.NotificationServerlessFunction
import com.nimbusframework.nimbuscore.annotations.notification.UsesNotificationTopic
import com.nimbusframework.nimbuslocal.exampleModels.Person

class ExampleNotificationHandler {

    @NotificationServerlessFunction(topic="test-topic")
    @UsesNotificationTopic(topic = "test-client-topic")
    fun receiveNotification(person: Person): Person {
        return person
    }
}