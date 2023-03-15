package com.vorpal.rosanjintalk.model;

// By Sebastian Raaphorst, 2023.

/**
 * An exception that is thrown when a request is made for an input set to be substituted into a story
 * and there is an empty entry in the input set. The first empty entry will be reported.
 */
public final class InputEmptyException extends IllegalArgumentException {
    public final int entry;

    public InputEmptyException(final int entry) {
        super("Empty input for substitution " + entry);
        this.entry = entry;
    }
}
