package com.nimbusframework.nimbuslocal.clients.mock

import com.nimbusframework.nimbuscore.clients.queue.QueueClient
import com.nimbusframework.nimbuslocal.exampleModels.Queue
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import io.mockk.mockk

class MockClientBuilderTest : StringSpec({

    "can inject queue client" {
        val queueClient = mockk<QueueClient>()
        val underTest = MockClientBuilder(queueClients = mapOf(Pair("QueueId", queueClient)))
        underTest.getQueueClient(Queue::class.java, "") shouldBe queueClient
    }

})
