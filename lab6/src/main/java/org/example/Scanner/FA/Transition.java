package org.example.Scanner.FA;

public class Transition {
    private String fromState;
    private String toState;
    private String element;

    public Transition(String fromState, String toState, String element) {
        this.fromState = fromState;
        this.toState = toState;
        this.element = element;
    }

    public String getFromState() {
        return fromState;
    }

    public String getToState() {
        return toState;
    }

    public String getElement() {
        return element;
    }
}
