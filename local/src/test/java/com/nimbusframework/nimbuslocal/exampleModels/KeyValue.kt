package com.nimbusframework.nimbuslocal.exampleModels

import com.nimbusframework.nimbuscore.annotations.keyvalue.KeyValueStore
import com.nimbusframework.nimbuscore.annotations.persistent.Attribute

@KeyValueStore(keyType = Int::class)
data class KeyValue (
        @Attribute
        val name: String = "",
        @Attribute
        val people: List<Person> = listOf()
)