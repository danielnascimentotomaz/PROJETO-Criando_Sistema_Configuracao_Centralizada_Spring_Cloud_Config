package com.dio.me.config_client.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/client")
public class ClientController {

    @Value("${example.property}")
    private String exampleProperty;

    @GetMapping(value = "/config")
    public String getConfig() {
        return "A propriedade configurada é: " + exampleProperty;
    }

}
