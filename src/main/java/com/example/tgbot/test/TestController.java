package com.example.tgbot.test;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tests")
public class TestController {
    private final TestService testService;

    @GetMapping("/{disciplineName}")
    public List<Test> getTests(@PathVariable String disciplineName) {
        return testService.getTests(disciplineName);
    }

    @PostMapping("/{disciplineName}")
    public void postTest(@PathVariable String disciplineName, @RequestBody Test test) {
        testService.postTest(disciplineName, test);
    }
}
