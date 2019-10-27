package com.nimbusframework.nimbuslocal.deployment.services.usesresources

import com.nimbusframework.nimbuscore.annotations.document.UsesDocumentStore
import com.nimbusframework.nimbuscore.permissions.PermissionType
import com.nimbusframework.nimbuslocal.deployment.function.FunctionEnvironment
import com.nimbusframework.nimbuslocal.deployment.function.permissions.StorePermission
import com.nimbusframework.nimbuslocal.deployment.services.LocalResourceHolder
import java.lang.reflect.Method

class LocalUsesDocumentStoreHandler(
        localResourceHolder: LocalResourceHolder,
        private val stage: String
): LocalUsesResourcesHandler(localResourceHolder) {
    override fun handleUsesResources(clazz: Class<out Any>, method: Method, functionEnvironment: FunctionEnvironment) {
        val usesDocumentStores = method.getAnnotationsByType(UsesDocumentStore::class.java)

        for (usesDocumentStore in usesDocumentStores) {
            if (usesDocumentStore.stages.contains(stage)) {
                functionEnvironment.addPermission(PermissionType.DOCUMENT_STORE, StorePermission(usesDocumentStore.dataModel.qualifiedName.toString()))
            }
        }
    }
}