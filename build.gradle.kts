// Top-level build file using traditional buildscript to ensure Chaquopy resolves correctly
buildscript {
    repositories {
        google()
        mavenCentral()
        maven("https://chaquo.com/maven")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.2.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.10")
        classpath("com.chaquo.python:gradle:15.0.1")
    }
}

// Clean up old plugins block if it exists
plugins {
}