package com.nimbusframework.nimbusawslocal.deployment.services.resource

import com.nimbusframework.nimbuslocal.LocalNimbusDeployment
import exampleresources.DynamoDbDocument
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.AnnotationSpec

internal class LocalDynamoDbDocumentStoreCreatorTest: AnnotationSpec() {

    @Test
    fun canCorrectlyDetectAnnotation() {
        val localNimbusDeployment = LocalNimbusDeployment.getNewInstance("exampleresources")
        val document = localNimbusDeployment.getDocumentStore(DynamoDbDocument::class.java);
        document shouldNotBe null
    }
}