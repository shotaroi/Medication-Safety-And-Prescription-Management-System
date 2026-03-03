package com.shotaroi.medsafety.domain.exception;

import com.shotaroi.medsafety.domain.enums.InteractionSeverity;

import java.util.List;

public class HighSeverityInteractionException extends DomainException {

    private final List<String> messages;

    public HighSeverityInteractionException(List<String> messages) {
        super("High severity drug interaction(s) detected: " + String.join("; ", messages));
        this.messages = messages;
    }

    public List<String> getMessages() {
        return messages;
    }
}
