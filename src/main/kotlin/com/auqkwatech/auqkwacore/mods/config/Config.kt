package com.auqkwatech.auqkwacore.mods.config

import com.auqkwatech.auqkwacore.AuqkwaCore
import com.auqkwatech.auqkwacore.plugin.AuqkwaPlugin
import com.google.common.reflect.TypeToken
import org.jetbrains.kotlin.konan.file.File

interface Config {

    fun fileName(): String

    fun loadConfig(auqkwaPlugin: AuqkwaPlugin): Any? {
        val configFile = java.io.File(auqkwaPlugin.modsDirectory, fileName())
        // Was created, didn't exist
        if (!configFile.exists()) {
            configFile.createNewFile()
            saveConfig()
        } else {
            val fromJson = AuqkwaCore.instance!!.gson.fromJson<HashMap<String, Any>>(
                    String(configFile.readBytes()),
                    object : TypeToken<HashMap<String, Any>>() {}.type
            )
            this::class.java.declaredFields.forEach { declaredField ->
                declaredField.isAccessible = true
                declaredField.set(this, fromJson[declaredField.name])
            }
            auqkwaPlugin.logger.info("Loaded config values for ${configFile.name}")
        }
        return configFile
    }

    fun saveConfig() {
        val configFile = File(AuqkwaCore.instance!!.modsDirectory.toPath(), fileName())

        val map = HashMap<String, Any>()
        this::class.java.declaredFields.forEach {
            it.isAccessible = true
            map[it.name] = it.get(this)
        }
        configFile.writeBytes(AuqkwaCore.instance!!.gson.toJson(map).toByteArray())
    }
}