package com.devops.automation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/deployment")
public class DeploymentController {

    private final DeploymentService deploymentService;

    @PostMapping
    public ResponseEntity<String> deployment(@RequestParam String githubUrl){
        return ResponseEntity.ok(deploymentService.deployFromGithub(githubUrl));
    }
}
