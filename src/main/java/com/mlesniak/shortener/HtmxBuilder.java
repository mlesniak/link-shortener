package com.mlesniak.shortener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.samskivert.mustache.Mustache;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Map;

public class HtmxBuilder {
    private final Mustache.Compiler compiler;
    private final StringBuilder body = new StringBuilder();
    private final HttpHeaders headers = new HttpHeaders();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private HttpStatus httpStatus = HttpStatus.OK;

    public HtmxBuilder() {
        this.compiler = Mustache
                .compiler()
                .withLoader(this::getTemplateReader);
    }

    public HtmxResponse build() {
        return new HtmxResponse(body.toString(), headers, httpStatus);
    }

    public HtmxBuilder add(String templateName) {
        return add(templateName, Map.of());
    }

    // This is a bit hacky, but ok for playground examples.
    @SuppressWarnings("unchecked")
    public HtmxBuilder add(String templateName, Object object) {
        try {
            var json = objectMapper.writeValueAsString(object);
            var map = objectMapper.readValue(json, Map.class);
            return add(templateName, map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public HtmxBuilder add(String templateName, Map<String, ?> data) {
        try(Reader templateReader = getTemplateReader(templateName)) {
            var templateSource = IOUtils.toString(templateReader);
            var fragment = compiler.compile(templateSource).execute(data);
            body.append(fragment);
            return this;
        } catch (IOException e) {
            throw new IllegalArgumentException("Error compiling template: " + templateName, e);
        }
    }

    public HtmxBuilder status(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        return this;
    }

    public HtmxBuilder header(String headerName, String value) {
        headers.add(headerName, value);
        return this;
    }

    private Reader getTemplateReader(String templateName) {
        String templateLocation = "/templates/%s.mustache";
        var istream = getClass().getResourceAsStream(templateLocation.formatted(templateName));
        if (istream == null) {
            throw new IllegalArgumentException("Template not found: " + templateName);
        }
        return new InputStreamReader(istream, Charset.defaultCharset());
    }
}
