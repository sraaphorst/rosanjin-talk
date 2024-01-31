# RosanjinTalk

An small game that consists of an ad-lib / Mad-Lib style substitution story game where you:
1. Write a story.
2. Define substitution questions.

Then the answers to the questions (e.g. noun, adjective, city) are then substituted into a story.

The application is written in Java. This is my first time using JavaFX in any serious capacity, and programming it
by hand instead of using FXML. This is also an exercise to keep up-to-date with changes to Java UI offerings.

## Why is it called RosanjinTalk?

Why wouldn't it be?

This is a bit of an inside joke that came about from:
* John Mulaney and the Sack Lunch Bunch's _Girl Talk_ segment.
* The original Iron Chef's Rosanjin Scholar, Masaaki Hirano (平野雅章).
* Put these things together and you get RosanjinTalk.

The scripts created and used by this game are called Flukes, which have the extension `.fluke`.

## Intended audience

This was specifically written as a gift for my partner.

Note that there are a number of inside jokes between us included in the game, although it is completely usable by
anyone.

## How does it work?

It is written in Java to leverage JavaFX and has two modes:

### `RosanjinTalk` creation mode

The creator mode to set up a list of substitution questions and to write a `RosanjinTalk` that allows for the
answers to the prompts to be substituted into the story.

These are saved as JSON files, with the extension `.fluke`.

### `RosanjinTalk` play mode 

A `fluke` file is loaded and the player is prompted with the substitution prompts.

The answers to said prompts are then substituted into the story and the resultant story is displayed and can
be saved as a simple text file or copied to the clipboard.

# Configuration

It is recommended that you install the latest (19+) version of:

[bellsoft Liberica Java](https://bell-sw.com/pages/downloads/)

Go to your platform and install either the:
* **Full** JRE (if you just plan on running Java applications)
* **Full** JDK (if you plan on using it to develop Java applications)

You may be able to use other Java distributions as well: I recommend **bellsoft Liberica Java** because
unlike other Java distributions, it comes packaged with JavaFX, making it easier to configure.

This project requires gradle 7.6.

You can configure this (at least in IntelliJ IDEA) in the `gradle/wrapper/gradle-wrapper.properties` with the
following line:

```
distributionUrl=https\://services.gradle.org/distributions/gradle-7.6-bin.zip
```

To package it, execute:
```
$ ./gradlew distZip
```

**Status**: Version 1.0.0 **complete.**

<!-- https://stackoverflow.com/questions/5258159/how-to-make-an-executable-jar-file -->
