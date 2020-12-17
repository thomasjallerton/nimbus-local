package com.nimbusframework.nimbuslocal.clients.mock

import com.nimbusframework.nimbuscore.clients.queue.QueueClient
import com.nimbusframework.nimbuslocal.exampleModels.Queue
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk

class MockClientBuilderTest : StringSpec({

    "can inject queue client" {
        val queueClient = mockk<QueueClient>()
        val underTest = MockClientBuilder(queueClients = mapOf(Pair("QueueId", queueClient)))
        underTest.getQueueClient(Queue::class.java, "") shouldBe queueClient
    }

})
