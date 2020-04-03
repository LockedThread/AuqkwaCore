package com.auqkwatech.auqkwacore.commands.arguments

import java.util.function.Function

interface ArgumentParser<T> {

    fun parse(): Function<String, T?>
}
