package com.github.mangila.app.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * If requesting our API from a browser, the browser will try to load the favicon.ico file.
 * This is a pure REST API, so there is no need for a favicon.ico.
 * Let's just silently ignore the favicon request.
 * <br>
 * Without this it could bring alot of 404 noise in the logs.
 */
@Controller
public class FaviconConfig {

    @GetMapping("favicon.ico")
    @ResponseBody
    void doNothing() {
        // do nothing
    }

}
