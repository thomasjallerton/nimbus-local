package com.nimbusframework.nimbusawslocal.deployment.services.resource

import com.nimbusframework.nimbuslocal.LocalNimbusDeployment
import exampleresources.DynamoDbDocument
import exampleresources.DynamoDbKeyValue
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.AnnotationSpec

internal class LocalDynamoDbKeyValueStoreCreatorTest: AnnotationSpec() {

    @Test
    fun canCorrectlyDetectAnnotation() {
        val localNimbusDeployment = LocalNimbusDeployment.getNewInstance("exampleresources")
        val keyValueStore = localNimbusDeployment.getKeyValueStore<Int, DynamoDbKeyValue>(DynamoDbKeyValue::class.java);
        keyValueStore shouldNotBe null
    }
}