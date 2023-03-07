# RosanjinTalk (for smol boi)

An small game that consists of an ad-lib / Mad-Lib style substitution story game where you:
1. Write a story.
2. Define substitution questions.

And then the answers to the questions (e.g. noun, adjective, city) are then substituted into a story.

The application is written in Java. This is my first time using JavaFX in any serious capacity, and programming it
by hand instead of using FXML.

## Why is it called RosanjinTalk?

Why wouldn't it be?

This is a bit of an inside joke that came about from:
* John Mulaney and the Sack Lunch Bunch's _Girl Talk_ segment.
* The original Iron Chef's Rosanjin Scholar, Masaaki Hirano (平野雅章).
* Put these things together and you get RosanjinTalk.

So the scripts created and used by this game are called `RosanjinTalkResource`s, which have the extension `.fluke`.

These are (in the future) planned to be zipped JSON files.

## Why is it for smol boi?

Because it was specifically written for my partner, who is both small and a boy
(inasmuch as a 30 something year old can be called a boy).

## How does it work?

It is written in Java to leverage JavaFX (although I suppose I could have written it in Kotlin or Scala)
and has two modes:

### `RosanjinTalk` creation mode

The creator mode to set up a list of substitution questions and to write a `RosanjinTalk` that allows for the
responses to the questions to be substituted into the story.

These are saved as simple serialized objects for convenience, despite Java serialization being discouraged in lieu
of JSON or XML.

(In this case, it seems easy enough to not be problematic.)

### `RosanjinTalk` play mode 

A `RosanjinTalk` serialized file is loaded and the player is prompted with the substitution questions.

The answers to said questions are then substituted into the story and the resultant story is displayed and can
be saved as a simple text file.

# Configuration

It is recommended that you install the latest (19+) version of:

[bellsoft Liberica Java](https://bell-sw.com/pages/downloads/)

Go to your platform and install either the:
* **Full** JRE (if you just plan on running Java applications)
* **Full** JDK (if you plan on using it to develop Java applications)

You may be able to use other Java distributions as well: I recommend **bellsoft Liberica Java** because
unlike other Java distributions, it comes packaged with JavaFX, making it easier to configure.

(Whether this will be the case is TBD.)

**Status**: Work in progress.

<!-- https://stackoverflow.com/questions/5258159/how-to-make-an-executable-jar-file -->
