package com.mlesniak.shortener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

class Form {
    public String url;
    public String shortLink;
    public String error;
}

@HtmxController
public class IndexController {
    private static final Logger log = LoggerFactory.getLogger(IndexController.class);

    private LinkService linkService;

    public IndexController(LinkService linkService) {
        this.linkService = linkService;
    }

    @GetMapping("/")
    public HtmxResponse index() {
        var form = new Form();
        return new HtmxBuilder()
                .add("index", form)
                .build();
    }

    @PostMapping(value = "/link")
    public HtmxResponse update(@RequestParam("url") String urlString) throws Exception {
        log.info("url={}", urlString);
        var builder = new HtmxBuilder();

        var form = new Form();
        form.url = urlString;

        try {
            form.shortLink = linkService.create(new Url(urlString)).url();
            log.info("shortLink={} for url={}", form.shortLink, urlString);

        } catch (Exception e) {
            form.error = e.getMessage();
            builder.status(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        builder.add("index", form);
        return builder.build();
    }
}