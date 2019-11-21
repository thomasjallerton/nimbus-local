package com.nimbusframework.nimbuslocal.deployment.services.resource

import com.nimbusframework.nimbuscore.annotations.keyvalue.KeyValueStore
import com.nimbusframework.nimbuscore.clients.keyvalue.AbstractKeyValueStoreClient
import com.nimbusframework.nimbuscore.clients.keyvalue.KeyValueStoreAnnotationService
import com.nimbusframework.nimbuslocal.LocalNimbusDeployment
import com.nimbusframework.nimbuslocal.deployment.keyvalue.LocalKeyValueStore
import com.nimbusframework.nimbuslocal.deployment.services.LocalResourceHolder

class LocalKeyValueStoreCreator(
        private val localResourceHolder: LocalResourceHolder,
        private val stage: String
): LocalCreateResourcesHandler {

    override fun createResource(clazz: Class<out Any>) {
        val keyValueStoreAnnotations = clazz.getAnnotationsByType(KeyValueStore::class.java)

        for (keyValueStoreAnnotation in keyValueStoreAnnotations) {
            if (keyValueStoreAnnotation.stages.contains(stage)) {
                val tableName = KeyValueStoreAnnotationService.getTableName(clazz, LocalNimbusDeployment.stage)
                val keyTypeAndName = KeyValueStoreAnnotationService.getKeyNameAndType(clazz, LocalNimbusDeployment.stage)
                val annotation = clazz.getDeclaredAnnotation(KeyValueStore::class.java)

                val localStore = LocalKeyValueStore(
                        annotation.keyType.java,
                        clazz,
                        keyTypeAndName.second,
                        keyTypeAndName.first,
                        tableName,
                        LocalNimbusDeployment.stage)

                localResourceHolder.keyValueStores[clazz] = localStore
                localResourceHolder.webKeyValueStores[tableName] = localStore
            }
        }
    }
}