import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import dev.s7a.gradle.minecraft.server.tasks.LaunchMinecraftServerTask
import dev.s7a.gradle.minecraft.server.tasks.LaunchMinecraftServerTask.JarUrl
import groovy.lang.Closure
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.serialization") version "1.5.31"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.1"
    id("com.github.ben-manes.versions") version "0.41.0"
    id("com.palantir.git-version") version "0.12.3"
    id("dev.s7a.gradle.minecraft.server") version "1.2.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("org.jmailen.kotlinter") version "3.8.0"
    id("org.hidetake.ssh") version "2.10.0"
}

val gitVersion: Closure<String> by extra

val pluginVersion: String by project.ext

repositories {
    mavenCentral()
    maven(url = "https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven(url = "https://oss.sonatype.org/content/groups/public/")
    maven(url = "https://jitpack.io")
    maven(url ="https://maven.enginehub.org/repo/")
}

val shadowImplementation: Configuration by configurations.creating
configurations["implementation"].extendsFrom(shadowImplementation)

dependencies {
    shadowImplementation(kotlin("stdlib"))
    compileOnly("org.spigotmc:spigot-api:$pluginVersion-R0.1-SNAPSHOT")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.0")
    implementation("com.sk89q.worldguard:worldguard-bukkit:7.0.1")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:VERSION")
    compileOnly("net.luckperms:api:5.4")
}

configure<BukkitPluginDescription> {
    main = "com.github.Ringoame196.Main"
    version = gitVersion()
    apiVersion = "1." + pluginVersion.split(".")[1]
    commands {
        register("money") {
            description = "お金を管理するコマンド"
            usage = "/money [メニュー] [プレイヤー] [値段]"
        }
        register("aoringoop") {
            description = "OP用コマンド"
            permission = "aoringo.server.OP"
        }
        register("write") {
            description = "書き込み用のコマンド"
            usage = "/write [入力1] [入力2]..."
        }
        register("fshop") {
            description = "ショップ管理のコマンド"
            usage = "/fshop set [lore,price] [入力]"
        }
    }
        permissions{
            register("aoringo.server.OP") {
                description = "青リンゴサーバーのオーナー権限"
                default = BukkitPluginDescription.Permission.Default.OP // TRUE, FALSE, OP or NOT_OP
        }
    }

    tasks.withType<ShadowJar> {
        configurations = listOf(shadowImplementation)
        archiveClassifier.set("")
        relocate("kotlin", "com.github.Ringoame196.libs.kotlin")
        relocate("org.intellij.lang.annotations", "com.github.Ringoame196.libs.org.intellij.lang.annotations")
        relocate("org.jetbrains.annotations", "com.github.Ringoame196.libs.org.jetbrains.annotations")
    }

    tasks.named("build") {
        dependsOn("shadowJar")
        doFirst {
            exec {
                workingDir("D:/plugin/aoringoServer") // バッチファイルの作業ディレクトリを設定
                commandLine("cmd", "/c", "transfer.bat")
            }
        }
    }

    task<LaunchMinecraftServerTask>("buildAndLaunchServer") {
        dependsOn("build")
        doFirst {
            copy {
                from(buildDir.resolve("libs/${project.name}.jar"))
                into(buildDir.resolve("MinecraftServer/plugins"))
            }
        }

        jarUrl.set(JarUrl.Paper(pluginVersion))
        jarName.set("server.jar")
        serverDirectory.set(buildDir.resolve("MinecraftServer"))
        nogui.set(true)
        agreeEula.set(true)
    }
    task<SetupTask>("setup")
}
