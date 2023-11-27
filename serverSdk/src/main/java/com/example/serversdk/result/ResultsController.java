package com.example.serversdk.result;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/results")
@RequiredArgsConstructor
public class ResultsController {
    private final ResultService resultService;

    @GetMapping("/disciplines")
    public ResponseEntity<?> getDisciplines(HttpServletRequest request) {
        return resultService.getDisciplines(request);
    }


    @GetMapping("/{disciplineName}/groups")
    public ResponseEntity<?> getGroups(@PathVariable String disciplineName, HttpServletRequest request) {
        return resultService.getGroups(request, disciplineName);
    }

    @GetMapping("/{disciplineName}/groups/{groupName}")
    public ResponseEntity<?> getStats(@PathVariable String groupName, @PathVariable String disciplineName, HttpServletRequest request) {
        return resultService.getStats(groupName, disciplineName, request);
    }


}
