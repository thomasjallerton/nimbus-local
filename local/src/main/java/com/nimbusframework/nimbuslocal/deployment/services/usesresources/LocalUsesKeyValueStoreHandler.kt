package com.nimbusframework.nimbuslocal.deployment.services.usesresources

import com.nimbusframework.nimbuscore.annotations.keyvalue.UsesKeyValueStore
import com.nimbusframework.nimbuscore.permissions.PermissionType
import com.nimbusframework.nimbuslocal.deployment.function.FunctionEnvironment
import com.nimbusframework.nimbuslocal.deployment.function.permissions.StorePermission
import com.nimbusframework.nimbuslocal.deployment.services.LocalResourceHolder
import java.lang.reflect.Method

class LocalUsesKeyValueStoreHandler(
        localResourceHolder: LocalResourceHolder,
        private val stage: String
): LocalUsesResourcesHandler(localResourceHolder) {
    override fun handleUsesResources(clazz: Class<out Any>, method: Method, functionEnvironment: FunctionEnvironment) {
        val usesKeyValueStores = method.getAnnotationsByType(UsesKeyValueStore::class.java)

        for (usesKeyValueSore in usesKeyValueStores) {
            if (usesKeyValueSore.stages.contains(stage)) {
                functionEnvironment.addPermission(PermissionType.KEY_VALUE_STORE, StorePermission(usesKeyValueSore.dataModel.qualifiedName.toString()))
            }
        }
    }
}