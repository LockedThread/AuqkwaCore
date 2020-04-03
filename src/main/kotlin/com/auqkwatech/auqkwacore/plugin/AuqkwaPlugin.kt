package com.auqkwatech.auqkwacore.plugin

import com.auqkwatech.auqkwacore.AuqkwaCore
import com.auqkwatech.auqkwacore.mods.Mod
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.kotlin.konan.file.File

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

    fun loadMod(pair: Pair<Mod, File>) {
        loadMod(pair.first, pair.second)
    }

    fun loadMod(mod: Mod, file: File) {
        mod.start()
        myMods[mod] = file
    }

    fun loadKotlinScript(scriptName: String): Pair<Mod, File> {
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
        val eval = AuqkwaCore.instance!!.scriptEngine.eval(String(fileContents))
        if (eval !is Mod) {
            throw RuntimeException("Mod is null. File: ${file.path}")
        }
        return eval to file
    }
}