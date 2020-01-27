# Chit Chat
A simple chat app backed by various firebase technologies. 

### Database structure
The data in the app is stored in a firestore database collection using this structure.

```
chatrooms: Collection
    id: autoID
    name: String
    description: String
    timestamp: Timestamp
    messages: Collection
        id: autoID
        user: String
        profileImageUrl: String
        timestamp: TimeStamp
        imageUrl: String
        imageReference: String
```

The app assumes that there is a collection called chatrooms and with at least one document with the following fields set: 

- name
- description
- timestamp

If any of these field are omitted, the chat room will not appear on the chat room list.

Users can upload images to chat aswell as text, images are stored on Firebase Storage in the folder `/images`

### Push notifications

If an app user would to receive push notifications, the app subscribes the user to a topic on FCM. A cloud function monitoring the chat rooms for new messages must be installed for this to work. There is one included in `/cloudfunctions`. 

Check out the official docs for installation: https://firebase.google.com/docs/functions


