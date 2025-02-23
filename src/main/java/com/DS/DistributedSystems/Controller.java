package com.DS.DistributedSystems;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class Controller {

    private final Storage storage = new Storage();

    @PostMapping("/put")
    public String put(@RequestParam String key, @RequestParam String value) {
        storage.put(key, value);
        return "Запись добавлена: " + key + " -> " + value + "\n";
    }

    @GetMapping("/get")
    public String get(@RequestParam String key) {
        String value = storage.get(key);
        if (value != null) {
            return "Значение для ключа " + key + ": " + value + "\n";
        } else {
            return "Ключ " + key + " не найден.\n";
        }
    }
}