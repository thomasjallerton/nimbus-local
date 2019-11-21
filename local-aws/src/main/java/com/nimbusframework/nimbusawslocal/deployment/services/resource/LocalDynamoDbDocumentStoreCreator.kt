package com.nimbusframework.nimbusawslocal.deployment.services.resource

import com.nimbusframework.nimbusaws.annotation.annotations.document.DynamoDbDocumentStore
import com.nimbusframework.nimbusaws.clients.document.DynamoDbDocumentStoreAnnotationService
import com.nimbusframework.nimbuscore.clients.document.DocumentStoreAnnotationService
import com.nimbusframework.nimbuslocal.deployment.document.LocalDocumentStore
import com.nimbusframework.nimbuslocal.deployment.services.LocalResourceHolder
import com.nimbusframework.nimbuslocal.deployment.services.resource.LocalCreateResourcesHandler

class LocalDynamoDbDocumentStoreCreator(
        private val localResourceHolder: LocalResourceHolder,
        private val stage: String
): LocalCreateResourcesHandler {

    override fun createResource(clazz: Class<out Any>) {
        val documentStoreAnnotations = clazz.getAnnotationsByType(DynamoDbDocumentStore::class.java)

        for (documentStoreAnnotation in documentStoreAnnotations) {
            if (documentStoreAnnotation.stages.contains(stage)) {
                val tableName = DynamoDbDocumentStoreAnnotationService.getTableName(clazz, stage)
                localResourceHolder.documentStores[clazz] = LocalDocumentStore(clazz, tableName, stage)
                localResourceHolder.webDocumentStores[tableName] = LocalDocumentStore(clazz, tableName, stage)
            }
        }
    }

}