# Guis

Only available for Paper 1.19.4.

Create beautiful GUI quickly without editing 1 billion yml files.
Use annotations to set the settings of your own data, without config files.


# Dev

## Gradle 

```groovy

repositories{
    // for GUI
	maven { url 'https://jitpack.io' }
    // For Paper API
    maven {
        url = uri('https://repo.papermc.io/repository/maven-public/')
    }
    // For ProtocolLib
    maven {
        url = uri('https://repo.dmulloy2.net/repository/public/')
    }
}

dependencies {
    implementation 'com.github.Otomny:guis:main-SNAPSHOT'
}

```