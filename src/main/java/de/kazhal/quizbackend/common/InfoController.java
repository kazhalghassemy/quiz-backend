package de.kazhal.quizbackend.common;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class InfoController {

    public record AppInfo(String name, String description) {}

    @GetMapping("/info")
    public AppInfo info() {
        return new AppInfo("Quiz-Backend",
                "Selbsttest zu Java-Grundlagen, Datenbanken und mehr");
    }
}