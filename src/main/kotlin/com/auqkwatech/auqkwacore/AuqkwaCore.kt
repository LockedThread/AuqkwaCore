package com.auqkwatech.auqkwacore

import com.auqkwatech.auqkwacore.commands.executor.AuqkwaCommandExecutor
import com.auqkwatech.auqkwacore.plugin.AuqkwaPlugin
import com.auqkwatech.auqkwacore.serialization.ItemStackSerializer
import com.auqkwatech.auqkwacore.utils.info
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.bukkit.Bukkit
import org.bukkit.command.CommandExecutor
import org.bukkit.inventory.ItemStack
import org.jetbrains.kotlin.cli.common.environment.setIdeaIoUseFallback
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngine
import javax.script.ScriptEngineManager

class AuqkwaCore : AuqkwaPlugin() {

    companion object {
        var instance: AuqkwaCore? = null

        init {
            setIdeaIoUseFallback()
        }
    }

    lateinit var gson: Gson
    lateinit var commandExecutor: CommandExecutor
    private val scriptEngineManager: ScriptEngineManager = ScriptEngineManager()
    lateinit var scriptEngine: KotlinJsr223JvmLocalScriptEngine

    override fun onEnable() {
        instance = this
        saveDefaultConfig()

        this.gson = GsonBuilder()
                .setPrettyPrinting()
                .enableComplexMapKeySerialization()
                .disableHtmlEscaping()
                .registerTypeAdapter(ItemStack::class.java, ItemStackSerializer)
                .create()
        this.commandExecutor = AuqkwaCommandExecutor()
        val exists = modsDirectory.exists
        if (!exists) {
            modsDirectory.mkdirs()
        }

        val pluginsFolder = dataFolder.parentFile
        val plugins = pluginsFolder.listFiles { pathname -> pathname.extension == "jar" } ?: emptyArray()
        info(plugins.contentToString())

        if (plugins.isNotEmpty()) {
            info("adding plugins to classpath.")
            val classpath = buildString {
                for (plugin in plugins) {
                    info(" - ${plugin.name}")
                    append("${plugin.absolutePath};")
                }
                append("${Bukkit::class.java.protectionDomain.codeSource.location.path};")
            }
            info(classpath)
            System.setProperty("kotlin.script.classpath", classpath)
        }
        scriptEngine = scriptEngineManager.getEngineByExtension("kts")!! as KotlinJsr223JvmLocalScriptEngine
        load(exists)
    }

    override fun load(exists: Boolean) {
        val pair = loadKotlinScript(scriptEngine, "Menus", !exists)
        loadMod(pair.first, pair.second)
    }

    /*private fun loadKotlinScript(
            directory: File,
            scriptName: String,
            new: Boolean
    ): Mod {
        val fileContents: ByteArray
        val file = File(directory, "${scriptName}.kts")
        if (new) {
            val resourceAsStream = classLoader.getResourceAsStream("mods/${scriptName}.kts")
            fileContents = ByteArray(resourceAsStream!!.available())
            resourceAsStream.read(fileContents)
            file.writeBytes(fileContents)
        } else {
            fileContents = file.readBytes()
        }
        val eval = receiver.eval(String(fileContents), receiver.context)
        if (eval !is Mod) {
            throw RuntimeException("Mod is null. File: ${file.path}")
        }
        return eval
    }*/

    override fun onDisable() {
        instance = null
    }
}
