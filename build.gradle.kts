import xyz.jpenilla.resourcefactory.bukkit.BukkitPluginYaml

plugins {
    id("java-library")
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.21"
    id("com.gradleup.shadow") version "9.4.1"
    id("xyz.jpenilla.run-paper") version "3.0.2"
    id("xyz.jpenilla.resource-factory-paper-convention") version "1.3.1"
}

repositories {
    mavenCentral()
}

version = "1.0.0"

dependencies {
    paperweight.paperDevBundle("26.1.1.build.+")
    implementation("com.electronwill.night-config:core:3.8.4")
    implementation("com.electronwill.night-config:toml:3.8.4")
    implementation("io.github.revxrsal:lamp.common:4.0.0-rc.16")
    implementation("io.github.revxrsal:lamp.bukkit:4.0.0-rc.16")
    implementation("io.github.revxrsal:lamp.brigadier:4.0.0-rc.16")
    implementation("org.bstats:bstats-bukkit:3.2.1")
}

paperPluginYaml {
    main = "me.bradenk.customItems.CustomItems"
    apiVersion = "26.1.1"

    load = BukkitPluginYaml.PluginLoadOrder.STARTUP
    authors.addAll("bradenk04", "Stryff")
    website = "https://bradenk.me/"
    prefix = "CustomItems"
    name = "CustomItems"
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

tasks {
    build {
        dependsOn(shadowJar)
    }

    runServer {
        // Configure the Minecraft version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin's jar (or shadowJar if present) will be used automatically.
        minecraftVersion("26.1.1")
        jvmArgs("-Xms2G", "-Xmx2G", "-Dcom.mojang.eula.agree=true")
    }
    withType<JavaCompile> {
        options.compilerArgs.add("-parameters")
    }

    shadowJar {
        archiveClassifier.set("")
        relocate("org.bstats", project.group.toString() + "bstats")
        relocate("com.electronwill", project.group.toString() + "nightconfig")
        relocate("io.github.revxrsal", project.group.toString() + "lamp")
    }
}
