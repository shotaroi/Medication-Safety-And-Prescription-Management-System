package com.shotaroi.medsafety.common;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.net.URI;
import java.time.Instant;
import java.util.Map;

/**
 * RFC 7807 Problem Details for HTTP APIs.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProblemDetail(
        URI type,
        String title,
        int status,
        String detail,
        String instance,
        String correlationId,
        Instant timestamp,
        Map<String, Object> extensions
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private URI type = URI.create("about:blank");
        private String title;
        private int status;
        private String detail;
        private String instance;
        private String correlationId;
        private Instant timestamp = Instant.now();
        private Map<String, Object> extensions;

        public Builder type(URI type) {
            this.type = type;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder status(int status) {
            this.status = status;
            return this;
        }

        public Builder detail(String detail) {
            this.detail = detail;
            return this;
        }

        public Builder instance(String instance) {
            this.instance = instance;
            return this;
        }

        public Builder correlationId(String correlationId) {
            this.correlationId = correlationId;
            return this;
        }

        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder extensions(Map<String, Object> extensions) {
            this.extensions = extensions;
            return this;
        }

        public ProblemDetail build() {
            return new ProblemDetail(type, title, status, detail, instance, correlationId, timestamp, extensions);
        }
    }
}
