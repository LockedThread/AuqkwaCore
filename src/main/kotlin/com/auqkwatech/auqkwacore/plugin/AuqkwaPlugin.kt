package com.auqkwatech.auqkwacore.plugin

import com.auqkwatech.auqkwacore.mods.Mod
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.kotlin.konan.file.File
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngine

val auqkwaPlugins = hashSetOf<AuqkwaPlugin>()

abstract class AuqkwaPlugin : JavaPlugin() {

    val modsDirectory: File by lazy {
        File(dataFolder.toPath(), "/mods")
    }

    val myMods = hashMapOf<Mod, File>()

    init {
        Thread.currentThread().contextClassLoader = classLoader
    }

    override fun onEnable() {
        super.onEnable()
        auqkwaPlugins.add(this)
    }

    abstract fun load(exists: Boolean)

    fun loadMod(mod: Mod, file: File) {
        mod.start()
        myMods[mod] = file
    }

    fun loadKotlinScript(
            scriptEngine: KotlinJsr223JvmLocalScriptEngine,
            scriptName: String,
            new: Boolean
    ): Pair<Mod, File> {
        val fileContents: ByteArray
        val file = File(modsDirectory, "${scriptName}.kts")
        if (new) {
            val resourceAsStream = classLoader.getResourceAsStream("mods/${scriptName}.kts")
            fileContents = ByteArray(resourceAsStream!!.available())
            resourceAsStream.read(fileContents)
            file.writeBytes(fileContents)
        } else {
            fileContents = file.readBytes()
        }
        val eval = scriptEngine.eval(String(fileContents), scriptEngine.context)
        if (eval !is Mod) {
            throw RuntimeException("Mod is null. File: ${file.path}")
        }
        return eval to file
    }
}