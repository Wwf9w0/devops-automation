package com.devops.automation.service;

import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Service
public class DeploymentService {


    public String deployFromGithub(String githubUrl) {
        try {
            String projectName = cloneGitHubRepository(githubUrl);
            Map<String, Object> config = readConfigurationFile(projectName);
            deployToKubernetes(projectName, config);
            return "Deployment successful for project: " + projectName;
        } catch (Exception e) {
            e.printStackTrace();
            return "Deployment failed: " + e.getMessage();
        }
    }


    private String cloneGitHubRepository(String githubUrl) {
        String projectName = githubUrl.substring(githubUrl.lastIndexOf("/") + 1, githubUrl.length() - 4);
        String cloneCommand = "git clone " + githubUrl + " /tmp" + projectName;
        executeCommand(cloneCommand, "Failed to clone repository.");
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
            config = mergeConfigs(defaultConfig, config);
            System.out.println("Configuration loaded: " + config);
            return config;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    private Map<String, Object> mergeConfigs(Map<String, Object> defaultConfig, Map<String, Object> userConfig) {
        for (String key : defaultConfig.keySet()) {
            userConfig.putIfAbsent(key, defaultConfig.get(key));
        }
        return userConfig;
    }


    private void deployToKubernetes(String projectName, Map<String, Object> config) {
        String imageName = (String) config.get("docker.imageName");
        String buildContext = (String) config.get("docker.buildContext");
        String buildCommand = "docker build -t " + imageName + " " + buildContext;
        executeCommand(buildCommand, "Failed to build Docker image.");

        String pushCommand = "docker push " + imageName;
        executeCommand(pushCommand, "Failed to push Docker image.");

        String deploymentName = (String) config.get("kubernetes.deploymentName");
        String namespace = (String) config.get("kubernetes.namespace");
        String applyCommand = "kubectl apply -f /tmp/" + projectName + "/k8s/deployment.yml -n " + namespace;
        executeCommand(applyCommand, "Failed to apply Kubernetes deployment.");

        System.out.println("Project " + deploymentName + "successfully deploy to Kubernetes in namespace " + namespace);
    }

    private void executeCommand(String command, String errorMessage) {
        try {
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            if (process.exitValue() != 0) {
                throw new RuntimeException(errorMessage);
            }
            System.out.println("Command executed successfully: " + command);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
