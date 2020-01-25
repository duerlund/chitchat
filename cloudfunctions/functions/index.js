const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

// Trigger function for new documents in chat
exports.notifyOnNewMessage = functions.firestore.document('rooms/{roomId}/messages/{messageId}')
  .onCreate((snapshot, context) => {
    var roomId = context.params.roomId;
    var messageId = context.params.messageId;
    console.log('roomId:' + roomId + ' messageId:' + messageId);
    sendPushNotification(roomId, messageId);
    return true;
});

function sendPushNotification(roomId, messageId) {
  var topic = 'chatroom.' + roomId;

  var message = {
    'topic': topic,
    'data': {
      'chatRoomId': roomId,
      'messageId': messageId
    },
    'notification': {
      'title': 'New messages for you',
      'body': 'New messages in roomId ' + roomId
    }
  }

  admin.messaging().send(message)
    .then((response) => {
      // Response is a message ID string.
      console.log('Successfully sent message:', response);
      return true;
    })
    .catch((error) => {
      console.log('Error sending message:', error);
    });

}
