pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()

        // HMS Coreのリポジトリ
        maven { setUrl("https://developer.huawei.com/repo/") }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

        // HMS Coreのリポジトリ
        maven { setUrl("https://developer.huawei.com/repo/") }
    }
}

rootProject.name = "WearEngineDemo"
include(":app")
