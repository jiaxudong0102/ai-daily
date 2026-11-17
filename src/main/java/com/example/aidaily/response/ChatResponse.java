package com.example.aidaily.response;

import java.util.List;

public record ChatResponse(List<Choice> choices) {
    public record Choice(Message message) {}
    public record Message(String content) {}
}