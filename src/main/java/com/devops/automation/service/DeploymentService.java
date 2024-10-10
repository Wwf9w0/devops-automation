package com.devops.automation.service;

import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Service
public class DeploymentService {


    private String cloneGitHubRepository(String githubUrl) {
        String projectName = githubUrl.substring(githubUrl.lastIndexOf("/") + 1, githubUrl.length() - 4);
        String cloneCommand = "git clone " + githubUrl + " /tmp" + projectName;

        try {
            Process process = Runtime.getRuntime().exec(cloneCommand);
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Repository cloned successfully: " + projectName);
        return projectName;
    }

    private Map<String, Object> readConfigurationFile(String projectName) {
        String configurationFilePath = "/tmp" + projectName + "/config.yml";
        File configFile = new File(configurationFilePath);

        if (!configFile.exists()) {
            throw new RuntimeException("config.yml file not found in the project.");
        }

        try (InputStream inputStream = new FileInputStream(configFile)) {
            Yaml yaml = new Yaml();
            Map<String, Object> config = yaml.load(inputStream);

            Map<String, Object> defaultConfig = getDefaultConfig();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private Map<String, Object> getDefaultConfig() {
        Map<String, Object> defaultConfig = new HashMap<>();
        defaultConfig.put("docker.imageName", "myrepo/spring-boot-app");
        defaultConfig.put("docker.buildContext", ".");
        defaultConfig.put("kubernetes.replicas", 1);
        defaultConfig.put("kubernetes.namespace", "default");
        defaultConfig.put("kubernetes.deploymentName", "spring-boot-app");
        defaultConfig.put("kubernetes.serviceName", "spring-boot-service");
        return defaultConfig;
    }
}
