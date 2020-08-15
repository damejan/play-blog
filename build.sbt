libraryDependencies += "dev.morphia.morphia" % "morphia-core" % "2.0.0-RC1"
libraryDependencies += "at.favre.lib" % "bcrypt" % "0.9.0"
libraryDependencies += "com.atlassian.commonmark" % "commonmark" % "0.15.2"
libraryDependencies += "com.atlassian.commonmark" % "commonmark-ext-gfm-tables" % "0.15.2"
name := """play-blog"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.13.3"

libraryDependencies += guice
