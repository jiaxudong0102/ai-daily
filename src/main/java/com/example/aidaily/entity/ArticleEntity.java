package com.example.aidaily.entity;

import com.example.aidaily.model.Article;
import jakarta.persistence.*;

@Entity
@Table(name="articles")
public class ArticleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long sourceId;
    private String title;
    @Column(length = 2048, nullable = false)
    private String url;
    private Long publishedAt;
    private String source;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Long publishedat) {
        this.publishedAt = publishedat;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
