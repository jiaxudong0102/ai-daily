package com.example.aidaily.model;

public record Article(
       Long id,
       String title,
       String url,
       Long time,
       String source

) {
}
