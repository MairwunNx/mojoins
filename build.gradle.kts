plugins {
  kotlin("jvm") version "2.4.21"
  kotlin("plugin.serialization") version "2.4.21"
  id("com.gradleup.shadow") version "9.6.1"
  id("com.modrinth.minotaur") version "2.10.0"
}

group = "ru.mairwunnx"
// The release workflow passes the version; every other build is a dev build.
version = (findProperty("releaseVersion") as String?)?.takeIf { it.isNotBlank() } ?: "dev"

repositories {
  mavenCentral()
  maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
  compileOnly("io.papermc.paper:paper-api:26.3.build.180-stable")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.11.0")
  implementation("com.charleskorn.kaml:kaml:0.104.0")
  implementation("com.maxmind.db:maxmind-db:4.2.0")

  testImplementation("io.papermc.paper:paper-api:26.3.build.180-stable")
  testImplementation(kotlin("test"))
  testImplementation(platform("org.junit:junit-bom:6.1.3"))
  testImplementation("org.junit.jupiter:junit-jupiter")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
  jvmToolchain(25)
}

tasks.test {
  useJUnitPlatform()
}

tasks.processResources {
  val version = project.version.toString()
  inputs.property("version", version)
  filesMatching("paper-plugin.yml") {
    expand("version" to version)
  }
}

tasks.shadowJar {
  archiveClassifier = ""
  mergeServiceFiles()
  // Relocated so other plugins that ship their own Kotlin or kaml can't clash with ours.
  listOf("kotlin", "kotlinx", "com.charleskorn.kaml", "it.krzeminski.snakeyaml", "okio", "net.thauvin", "com.maxmind")
    .forEach { relocate(it, "ru.mairwunnx.mojoins.libs.$it") }
  exclude("META-INF/*.kotlin_module", "META-INF/versions/*/module-info.class", "module-info.class")
  dependencies { exclude(dependency("org.jetbrains:annotations")) }
}

tasks.jar { enabled = false }
tasks.assemble { dependsOn(tasks.shadowJar) }

// `./gradlew modrinth` with MODRINTH_TOKEN set; the release workflow runs it when asked to.
modrinth {
  token = providers.environmentVariable("MODRINTH_TOKEN")
  projectId = "mojoins"
  versionNumber = project.version.toString()
  versionName = "Mo’Joins ${project.version}"
  versionType = "release"
  uploadFile.set(tasks.shadowJar)
  gameVersions.addAll("26.3")
  loaders.addAll("paper", "purpur")
  changelog = "https://github.com/MairwunNx/mojoins/releases/tag/v${project.version}"
  syncBodyFrom = providers.fileContents(layout.projectDirectory.file("README.md")).asText
}

tasks.modrinth {
  dependsOn(tasks.modrinthSyncBody)
  notCompatibleWithConfigurationCache("Minotaur reads the project at execution time")
}
