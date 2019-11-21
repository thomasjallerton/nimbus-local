package com.nimbusframework.nimbusawslocal.deployment.services.resource

import com.nimbusframework.nimbusaws.annotation.annotations.keyvalue.DynamoDbKeyValueStore
import com.nimbusframework.nimbusaws.clients.keyvalue.DynamoDbKeyValueStoreAnnotationService
import com.nimbusframework.nimbuslocal.deployment.keyvalue.LocalKeyValueStore
import com.nimbusframework.nimbuslocal.deployment.services.LocalResourceHolder
import com.nimbusframework.nimbuslocal.deployment.services.resource.LocalCreateResourcesHandler

class LocalDynamoDbKeyValueStoreCreator(
        private val localResourceHolder: LocalResourceHolder,
        private val stage: String
): LocalCreateResourcesHandler {

    override fun createResource(clazz: Class<out Any>) {
        val keyValueStoreAnnotations = clazz.getAnnotationsByType(DynamoDbKeyValueStore::class.java)

        for (keyValueStoreAnnotation in keyValueStoreAnnotations) {
            if (keyValueStoreAnnotation.stages.contains(stage)) {
                val tableName = DynamoDbKeyValueStoreAnnotationService.getTableName(clazz, stage)
                val keyTypeAndName = DynamoDbKeyValueStoreAnnotationService.getKeyNameAndType(clazz, stage)

                localResourceHolder.keyValueStores[clazz] = LocalKeyValueStore(
                        keyValueStoreAnnotation.keyType.java,
                        clazz,
                        keyTypeAndName.second,
                        keyTypeAndName.first,
                        tableName,
                        stage)
            }
        }
    }
}