package com.auqkwatech.auqkwacore.commands.arguments

import com.auqkwatech.auqkwacore.commands.arguments.exceptions.CommandParseException
import kotlin.reflect.KClass

data class Argument(var index: Int, var value: String?) {

    @Suppress("TYPE_INFERENCE_ONLY_INPUT_TYPES_WARNING", "UNCHECKED_CAST")
    fun <T : Any> parse(tClass: KClass<T>): T? {
        if (value != null) {
            val set = ArgumentRegistry.instance.argumentMap[tClass]
            set?.forEach {
                val apply = it.parse().apply(value!!)
                if (apply != null) {
                    return apply as T
                }
            }
        }
        return null
    }

    @Throws(CommandParseException::class)
    fun <T : Any> forceParse(tClass: KClass<T>): T {
        val parse = parse(tClass)
        if (parse != null) {
            return parse
        }
        throw CommandParseException(tClass.simpleName!!, index)
    }
}