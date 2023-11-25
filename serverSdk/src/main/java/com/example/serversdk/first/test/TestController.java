package com.example.serversdk.first.test;

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

    @GetMapping("/{disciplineName}/{testId}")
    public TestDto getTestClaims(@PathVariable Long testId, @PathVariable String disciplineName) {
        return testService.getTestClaims(testId);
    }

    @PostMapping("/{disciplineName}")
    public void postTest(@PathVariable String disciplineName, @RequestBody TestRequest testRequest) {
        testService.createNewTest(testRequest, disciplineName);
    }

    @PatchMapping("{id}")
    public void updateTest(@PathVariable long id, Test test) {
        testService.updateTest(id, test);
    }
}
