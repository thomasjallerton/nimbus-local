package com.nimbusframework.nimbuslocal.exampleHandlers

import com.nimbusframework.nimbuscore.annotations.function.BasicServerlessFunction

class ExampleBasicFunctionHandler {

    @BasicServerlessFunction
    fun handle(input: String): Boolean {
        return true
    }
}