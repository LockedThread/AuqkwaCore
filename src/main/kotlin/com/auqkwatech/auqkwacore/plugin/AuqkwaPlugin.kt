package com.auqkwatech.auqkwacore.plugin

import com.auqkwatech.auqkwacore.mods.Mod
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.kotlin.konan.file.File
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngine

val auqkwaPlugins = hashSetOf<AuqkwaPlugin>()

abstract class AuqkwaPlugin : JavaPlugin() {

    val modsDirectory: java.io.File by lazy {
        java.io.File(dataFolder, "/mods")
    }

    val myMods = hashMapOf<Mod, File>()

    override fun onEnable() {
        super.onEnable()
        Thread.currentThread().contextClassLoader = classLoader
        auqkwaPlugins.add(this)
    }

    abstract fun load(exists: Boolean)

    fun loadMod(pair: Pair<Mod, File>) {
        loadMod(pair.first, pair.second)
    }

    fun loadMod(mod: Mod, file: File) {
        mod.start()
        myMods[mod] = file
    }

    fun loadKotlinScript(
            scriptEngine: KotlinJsr223JvmLocalScriptEngine,
            scriptName: String
    ): Pair<Mod, File> {
        val fileContents: ByteArray
        val file = File(modsDirectory.toPath(), "${scriptName}.kts")
        if (file.exists) {
            fileContents = file.readBytes()
        } else {
            val resourceAsStream = classLoader.getResourceAsStream("mods/${scriptName}.kts")
            fileContents = ByteArray(resourceAsStream!!.available())
            resourceAsStream.read(fileContents)
            file.writeBytes(fileContents)
        }
        val eval = scriptEngine.eval(String(fileContents), scriptEngine.context)
        if (eval !is Mod) {
            throw RuntimeException("Mod is null. File: ${file.path}")
        }
        return eval to file
    }
}