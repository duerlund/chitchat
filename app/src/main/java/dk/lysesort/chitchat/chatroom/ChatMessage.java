package dk.lysesort.chitchat.chatroom;

import com.google.firebase.Timestamp;

public class ChatMessage {
    private String id;
    private Timestamp timestamp;
    private String user;
    private String text;
    private String profileImageUrl;
    private String imageUrl;

    public ChatMessage() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
               "id='" + id + '\'' +
               ", timestamp=" + timestamp +
               ", user='" + user + '\'' +
               ", text='" + text + '\'' +
               ", profileImageUrl='" + profileImageUrl + '\'' +
               ", imageUrl='" + imageUrl + '\'' +
               '}';
    }
}
