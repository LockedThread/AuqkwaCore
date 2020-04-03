package com.auqkwatech.auqkwacore.commands.arguments

import com.auqkwatech.auqkwacore.utils.server
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.util.function.Function
import kotlin.reflect.KClass

class ArgumentRegistry {

    companion object {
        val instance: ArgumentRegistry by lazy {
            ArgumentRegistry()
        }
    }

    val argumentMap: HashMap<KClass<*>, HashSet<ArgumentParser<*>>> = HashMap()

    init {
        addArgumentParser(Int::class, object : ArgumentParser<Int> {
            override fun parse(): Function<String, Int?> {
                return Function<String, Int?> {
                    it.toIntOrNull()
                }
            }
        })

        addArgumentParser(Long::class, object : ArgumentParser<Long> {
            override fun parse(): Function<String, Long?> {
                return Function<String, Long?> {
                    it.toLongOrNull()
                }
            }
        })

        addArgumentParser(Double::class, object : ArgumentParser<Double> {
            override fun parse(): Function<String, Double?> {
                return Function<String, Double?> {
                    it.toDoubleOrNull()
                }
            }
        })

        addArgumentParser(Float::class, object : ArgumentParser<Float> {
            override fun parse(): Function<String, Float?> {
                return Function<String, Float?> {
                    it.toFloatOrNull()
                }
            }
        })

        addArgumentParser(OfflinePlayer::class, object : ArgumentParser<OfflinePlayer> {
            override fun parse(): Function<String, OfflinePlayer?> {
                return Function<String, OfflinePlayer?> {
                    @Suppress("DEPRECATION") val offlinePlayer = server.getOfflinePlayer(it)
                    if (offlinePlayer.hasPlayedBefore()) {
                        offlinePlayer
                    } else {
                        null
                    }
                }
            }
        })

        addArgumentParser(Player::class, object : ArgumentParser<Player> {
            override fun parse(): Function<String, Player?> {
                return Function<String, Player?> {
                    server.getPlayer(it)
                }
            }
        })
    }

    fun <T : Any> addArgumentParser(clazz: KClass<T>, argumentParser: ArgumentParser<T>) {
        argumentMap.computeIfPresent(clazz) { _, shit ->
            shit.add(argumentParser)
            shit
        }
        argumentMap.putIfAbsent(clazz, hashSetOf(argumentParser))
    }
}