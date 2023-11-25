pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://jitpack.io") }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        // library voice/video call
        maven { url = uri("https://storage.zego.im/maven") }   // <- Add this line.
        jcenter()

    }
}

rootProject.name = "PRO1121-GR!"
include(":app")
