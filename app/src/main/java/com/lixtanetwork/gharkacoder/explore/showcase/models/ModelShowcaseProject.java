package com.lixtanetwork.gharkacoder.explore.showcase.models;

import java.util.List;

public class ModelShowcaseProject {
    private String projectId;
    private String projectLogo;
    private String projectTitle;
    private String projectTagline;
    private String projectDescription;
    private String projectType;
    private String projectVideoUrl;
    private List<String> projectOptionalBanners;
    private int projectVotes;

    public ModelShowcaseProject() {
    }

    public ModelShowcaseProject(String projectId, String projectLogo, String projectTitle, String projectTagline, String projectDescription, String projectType, String projectVideoUrl, List<String> projectOptionalBanners, int projectVotes) {
        this.projectId = projectId;
        this.projectLogo = projectLogo;
        this.projectTitle = projectTitle;
        this.projectTagline = projectTagline;
        this.projectDescription = projectDescription;
        this.projectType = projectType;
        this.projectVideoUrl = projectVideoUrl;
        this.projectOptionalBanners = projectOptionalBanners;
        this.projectVotes = projectVotes;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectLogo() {
        return projectLogo;
    }

    public void setProjectLogo(String projectLogo) {
        this.projectLogo = projectLogo;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public String getProjectTagline() {
        return projectTagline;
    }

    public void setProjectTagline(String projectTagline) {
        this.projectTagline = projectTagline;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public String getProjectType() {
        return projectType;
    }

    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }

    public String getProjectVideoUrl() {
        return projectVideoUrl;
    }

    public void setProjectVideoUrl(String projectVideoUrl) {
        this.projectVideoUrl = projectVideoUrl;
    }

    public List<String> getProjectOptionalBanners() {
        return projectOptionalBanners;
    }

    public void setProjectOptionalBanners(List<String> projectOptionalBanners) {
        this.projectOptionalBanners = projectOptionalBanners;
    }

    public int getProjectVotes() {
        return projectVotes;
    }

    public void setProjectVotes(int projectVotes) {
        this.projectVotes = projectVotes;
    }

}
