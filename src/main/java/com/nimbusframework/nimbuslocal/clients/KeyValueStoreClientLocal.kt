package com.nimbusframework.nimbuslocal.clients

import com.nimbusframework.nimbuscore.clients.keyvalue.KeyValueStoreClient
import com.nimbusframework.nimbuscore.permissions.PermissionType
import com.nimbusframework.nimbuslocal.deployment.keyvalue.LocalKeyValueStore

internal class KeyValueStoreClientLocal<K, V>(
        private val valueClass: Class<V>
): KeyValueStoreClient<K, V>, LocalClient() {

    private val table: LocalKeyValueStore<K, V> = localNimbusDeployment.getKeyValueStore(valueClass)

    override fun canUse(): Boolean {
        return checkPermissions(PermissionType.KEY_VALUE_STORE, valueClass.canonicalName)
    }
    override val clientName: String = KeyValueStoreClientLocal::class.java.simpleName

    override fun put(key: K, value: V) {
        checkClientUse()
        table.put(key, value)
    }

    override fun delete(keyObj: K) {
        checkClientUse()
        table.delete(keyObj)
    }

    override fun getAll(): Map<K, V> {
        checkClientUse()
        return table.getAll()
    }

    override fun get(keyObj: K): V? {
        checkClientUse()
        return table.get(keyObj)
    }
}