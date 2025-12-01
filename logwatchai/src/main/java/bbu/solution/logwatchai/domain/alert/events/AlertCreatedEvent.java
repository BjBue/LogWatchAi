package bbu.solution.logwatchai.domain.alert.events;

import bbu.solution.logwatchai.domain.alert.Alert;

public record AlertCreatedEvent(Alert alert) {}
