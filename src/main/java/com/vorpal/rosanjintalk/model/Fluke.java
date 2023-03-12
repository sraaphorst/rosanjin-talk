package com.vorpal.rosanjintalk.model;

// By Sebastian Raaphorst, 2023.

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.vorpal.rosanjintalk.ui.Shared;

/**
 * This is an immutable model for storing a RosanjinTalk file, aka a Fluke.
 * It contains the required prompts with their substitution number and the story
 * in which to make the substitutions.
 *
 * @param title   The title of the story.
 * @param inputs The list of inputs to the story for substitution. Key 1, for example, will prompt inputs[1] and then
 *               substitute any occurrences of "{1}" in the story. with the response.
 */
public record Fluke(String filename, String title, Map<Integer, String> inputs, String story) {
    public Fluke {
        Objects.requireNonNull(filename);
        Objects.requireNonNull(title);
        Objects.requireNonNull(inputs);
        Objects.requireNonNull(story);

        if (isInvalid(inputs.keySet(), story))
            throw new RuntimeException("There are substitutions in the story body that are not in the inputs.");
    }

    public static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(SerializationFeature.INDENT_OUTPUT, true);

    public static Fluke fromJson(final String json) throws IOException {
        return MAPPER.readValue(json, Fluke.class);
    }

    public String toJson() throws JsonProcessingException {
        return MAPPER.writeValueAsString(this);
    }

    /**
     * Save the contents as a zipped file of JSON as a .fluke file.
     */
    public void save() {
        final var flukeFilename = filename.endsWith(".fluke") ? filename : filename + ".fluke";
        final var path = Objects.requireNonNull(Shared.getFlukePath());
        final var flukePath = path.resolve(flukeFilename);

        try {
            Files.write(flukePath, toJson().getBytes());
        } catch (final JsonProcessingException e) {
            Shared.unrecoverableError("Could not convert into output format.");
        } catch (final IOException e) {
            Shared.unrecoverableError("Could not write file:\n\n" + flukeFilename +
                    "\n\nto path:\n\n" + path);
        }
    }

    /**
     * Open a file from the fluke directory. Should end with .fluke.
     * @param flukeFilename The file to open from the fluke directory.
     * @return A Fluke object representing the file.
     */
    public static Fluke load(final String flukeFilename) {
        final var path = Shared.getFlukePath();
        Objects.requireNonNull(path);
        final var flukePath = path.resolve(flukeFilename);
        try {
            final var json = Files.readString(flukePath);
            return fromJson(json);
        } catch (final IOException e) {
            Shared.unrecoverableError("Could not read file:\n\n" + flukeFilename);
            // We will never reach this point.
            return null;
        }
    }

    /**
     * Determine if this represents a valid RosanjinTalk, i.e. all entries of the form {#}
     * in the story are covered by the inputs.
     *
     * @param inputKeys the set of keys that are in the inputs
     * @param story      the body of story into which the substitutions are made
     * @return true if for every {#} entry in the story, there is an entry in the inputKeys
     */
    public static boolean isInvalid(final Set<Integer> inputKeys, final String story) {
        final var requiredSubstitutions = substitutions(story);
        return !inputKeys.containsAll(requiredSubstitutions);
    }

    /**
     * Get all substitutions in a title and a story.
     * @param title the title string
     * @param story the story string
     * @return a set of the indices of the substitutions
     */
    public static Set<Integer> allSubstituations(final String title, final String story) {
        final var substitutions = substitutions(title);
        final var storySubstitutions = substitutions(story);
        substitutions.addAll(storySubstitutions);
        return substitutions;
    }
    /**
     * Static method to retrieve the substitutions from a body of story.
     * These are the numbers that are of the form {#}, i.e. between curly braces.
     *
     * @param string the string to scan for substitution entries
     * @return a set of the substitution entries found in the string
     */
    private static Set<Integer> substitutions(final String string) {
        // Retrieve all the {#} entries from the story.
        final var substitutions = new HashSet<Integer>();

        // Traverse the story and get all the {#} entries.
        final var pattern = Pattern.compile("(?<=\\{)(\\d+)(?=})");
        final var matcher = pattern.matcher(string);
        while (matcher.find())
            substitutions.add(Integer.parseInt(matcher.group()));
        return substitutions;
    }

    /**
     * This returns the set of unused keys in inputKeys and can be used to generate warnings
     * about keys that can be deleted.
     *
     * @param inputKeys the set of keys that are in the inputs
     * @param title     the title string
     * @param story     the story string
     * @return the set of keys which appear in the inputKeys but not in the story
     */
    public static Set<Integer> unusedKeys(final Set<Integer> inputKeys,
                                          final String title,
                                          final String story) {
        final var substitutionKeys = allSubstituations(title, story);

        return inputKeys
                .stream()
                .filter(k -> !substitutionKeys.contains(k))
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Given a set of answers for the inputs to this RosanjinTalk, make the substitutions.
     * If there are any missing entries, a RuntimeException is thrown.
     * If there are any empty entries, an InputEmptyException is thrown.
     *
     * @param inputs the answers provided by the player
     * @param text   the text into which to make the substitutions
     * @return a string with the answers substituted into the story
     */
    public static String substitute(final Map<Integer, String> inputs, final String text) {
        // We shouldn't need to check for validation at this point, but we do just in case.
        if (isInvalid(inputs.keySet(), text))
            throw new RuntimeException("The input set is not valid for the Fluke.");

        // trim all the Strings in the answers.
        final var trimmedAnswers = inputs.entrySet()
                .stream()
                .map(entry -> new AbstractMap.SimpleImmutableEntry<>(entry.getKey(), entry.getValue().trim()))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));

        // If there is an empty entry, then we throw an exception indicating the first empty key.
        trimmedAnswers.entrySet()
                .stream()
                .sorted()
                .filter(entry -> entry.getValue().isEmpty())
                .findFirst()
                .map(Map.Entry::getKey)
                .ifPresent(key -> {
                    throw new InputEmptyException(key);
                });

        // Otherwise, substitute the strings. I forgot how terrible Java programming is after Kotlin.
        // No decent fold expression, so we just have to do this the hard way.
        var substitutedText = text;
        for (final var entry : trimmedAnswers.entrySet())
            substitutedText = substitutedText.replace("{" + entry.getKey() + "}", entry.getValue());
        return substitutedText;
    }
}

