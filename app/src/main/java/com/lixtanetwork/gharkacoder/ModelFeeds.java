package com.lixtanetwork.gharkacoder;

public class ModelFeeds {
    private String feedId;
    private String feedContent;
    private String userName;
    private String name;
    private String profilePic;
    private String feedImage;
    private long feedDate;

    public ModelFeeds() {
    }

    public ModelFeeds(String feedId, String feedContent, String userName, String name, String profilePic, String feedImage, long feedDate) {
        this.feedId = feedId;
        this.feedContent = feedContent;
        this.userName = userName;
        this.name = name;
        this.profilePic = profilePic;
        this.feedImage = feedImage;
        this.feedDate = feedDate;
    }

    public String getFeedId() {
        return feedId;
    }

    public void setFeedId(String feedId) {
        this.feedId = feedId;
    }

    public String getFeedContent() {
        return feedContent;
    }

    public void setFeedContent(String feedContent) {
        this.feedContent = feedContent;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getFeedImage() {
        return feedImage;
    }

    public void setFeedImage(String feedImage) {
        this.feedImage = feedImage;
    }

    public long getFeedDate() {
        return feedDate;
    }

    public void setFeedDate(long feedDate) {
        this.feedDate = feedDate;
    }
}
