package com.DS.DistributedSystems;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class Controller {

    private final Storage storage = new Storage();

    @PostMapping("/add")
    public String addKeyValue(@RequestBody Map<String, String> data) {
        String key = data.get("key");
        String value = data.get("value");
        storage.put(key, value);
        return "Запись добавлена: " + key + " -> " + value + "\n";
    }

    @GetMapping("/get/{key}")
    public String getValueByKey(@PathVariable String key) {
        String value = storage.get(key);
        if (value != null) {
            return "Значение для ключа " + key + ": " + value + "\n";
        } else {
            return "Ключ " + key + " не найден.\n";
        }
    }
}