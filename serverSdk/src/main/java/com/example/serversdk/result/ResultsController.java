package com.example.serversdk.result;

import com.example.serversdk.first.group.Group;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/results")
@RequiredArgsConstructor
public class ResultsController {
    private final ResultService resultService;

    @GetMapping()
    public ResponseEntity<?> getDisciplines(HttpServletRequest request) {
        return resultService.getDisciplines(request);
    }



    @GetMapping()
    public ResponseEntity<?> getGroups(HttpServletRequest request) {
        return resultService.getGroups(request);
    }
}
