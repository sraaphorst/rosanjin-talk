package com.vorpal.rosanjintalk.model;

// By Sebastian Raaphorst, 2023.

import java.io.Serializable;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * This is the model for storing an ad-lib aka Rosanjin-talk.
 * It contains the required prompts with their substitution number and the text
 * in which to make the substitutions.
 * I know getting rid of Java Serialization is a long-term goal, but for a project of this magnitude,
 * it is the easiest solution and should not be brittle.
 *
 * @param name   The name of the story.
 * @param inputs The list of inputs to the text for substitution. Key 1, for example, will prompt inputs[1] and then substitute any occurrences of "{1}" in the text. with the response.
 */
public record RosanjinTalkResource(String name, Map<Integer, String> inputs, String text) implements Serializable {
    public RosanjinTalkResource(final String name, final Map<Integer, String> inputs, final String text) {
        if (isInvalid(inputs.keySet(), text))
            throw new RuntimeException("There are substitutions in the story body that are not in the inputs.");
        this.name = name;
        this.inputs = Collections.unmodifiableMap(inputs);
        this.text = text;
    }

    /**
     * Determine if this represents a valid RosanjinTalk, i.e. all entries of the form {#}
     * in the text are covered by the inputs.
     *
     * @param inputKeys the set of keys that are in the inputs
     * @param text      the body of text into which the substitutions are made
     * @return true if for every {#} entry in the text, there is an entry in the inputKeys
     */
    public static boolean isInvalid(final Set<Integer> inputKeys, final String text) {
        final var requiredSubstitutions = storySubstitutions(text);
        return !inputKeys.containsAll(requiredSubstitutions);
    }

    /**
     * Static method to retrieve the substitutions from a body of text.
     * These are the numbers that are of the form {#}, i.e. between curly braces.
     *
     * @param text the text to scan for substitution entries
     * @return a set of the substitution entries found in the text
     */
    public static Set<Integer> storySubstitutions(final String text) {
        // Retrieve all the {#} entries from the text.
        final var substitutions = new HashSet<Integer>();

        // Traverse the story and get all the {#} entries.
        final var pattern = Pattern.compile("(?<=\\{)(\\d+)(?=})");
        final var matcher = pattern.matcher(text);
        while (matcher.find())
            substitutions.add(Integer.parseInt(matcher.group()));
        return Collections.unmodifiableSet(substitutions);
    }

    /**
     * This returns the set of unused keys in inputKeys and can be used to generate warnings
     * about keys that can be deleted.
     *
     * @param inputKeys the set of keys that are in the inputs
     * @param text      the body of text into which the substitutions are made
     * @return the set of keys which appear in the inputKeys but not in the text
     */
    public static Set<Integer> unusedKeys(final Set<Integer> inputKeys, final String text) {
        final var substitutionKeys = storySubstitutions(text);
        return inputKeys
                .stream()
                .filter(k -> !substitutionKeys.contains(k))
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Given a set of answers for the inputs to this RosanjinTalk, make the substitutions.
     * If there are any missing entries, a RuntimeException is issued.
     * If there are any empty entries, a
     *
     * @param answers the answers provided by the player
     * @return a string with the answers substituted into the text
     */
    public String substitute(final Map<Integer, String> answers) {
        // We shouldn't need to check for validation at this point, but we do just in case.
        if (isInvalid(answers.keySet(), text))
            throw new RuntimeException("The input set is not valid for the RosanjinTalk.");

        // trim all the Strings in the answers.
        final var trimmedAnswers = answers.entrySet()
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

