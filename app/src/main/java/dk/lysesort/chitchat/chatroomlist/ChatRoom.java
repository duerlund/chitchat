package dk.lysesort.chitchat.chatroomlist;

import com.google.firebase.Timestamp;

public class ChatRoom {
    private String id;
    private Timestamp timestamp = new Timestamp(0, 0);
    private String name;
    private String description;

    public ChatRoom() {

    }

    public ChatRoom(String id, Timestamp timestamp, String name, String description) {
        this.id = id;
        this.timestamp = timestamp;
        this.name = name;
        this.description = description;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "ChatRoom{" +
               "id='" + id + '\'' +
               ", timestamp=" + timestamp +
               ", name='" + name + '\'' +
               ", description='" + description + '\'' +
               '}';
    }
}
