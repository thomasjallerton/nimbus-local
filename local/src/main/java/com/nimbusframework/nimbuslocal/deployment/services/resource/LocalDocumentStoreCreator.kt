package com.nimbusframework.nimbuslocal.deployment.services.resource

import com.nimbusframework.nimbuscore.annotations.document.DocumentStoreDefinition
import com.nimbusframework.nimbuscore.clients.document.DocumentStoreAnnotationService
import com.nimbusframework.nimbuslocal.deployment.document.LocalDocumentStore
import com.nimbusframework.nimbuslocal.deployment.services.LocalResourceHolder

class LocalDocumentStoreCreator(
        private val localResourceHolder: LocalResourceHolder,
        private val stage: String
): LocalCreateResourcesHandler {

    override fun createResource(clazz: Class<out Any>) {
        val documentStoreAnnotations = clazz.getAnnotationsByType(DocumentStoreDefinition::class.java)

        for (documentStoreAnnotation in documentStoreAnnotations) {
            if (documentStoreAnnotation.stages.contains(stage)) {
                val tableName = DocumentStoreAnnotationService.getTableName(clazz, stage)
                val localStore = LocalDocumentStore(clazz, tableName, stage)

                localResourceHolder.webDocumentStores[tableName] = localStore
                localResourceHolder.documentStores[clazz] = localStore
            }
        }
    }

}