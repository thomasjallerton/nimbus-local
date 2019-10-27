package com.nimbusframework.nimbuslocal.exampleModels

import com.nimbusframework.nimbuscore.annotations.document.DocumentStore
import com.nimbusframework.nimbuscore.annotations.persistent.Attribute
import com.nimbusframework.nimbuscore.annotations.persistent.Key

@DocumentStore
data class Document(
        @Key
        val name: String = "",
        @Attribute
        val people: List<Person> = listOf()
)