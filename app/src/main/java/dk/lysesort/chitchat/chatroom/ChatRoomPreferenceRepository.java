package dk.lysesort.chitchat.chatroom;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

import dk.lysesort.chitchat.R;

/**
 * Handles chat room preferences. Preferences are handled per device, so you could have two device
 * with different chat room notification preferences.
 */
public class ChatRoomPreferenceRepository {

    private ChatRoomPreferenceRepository() {

    }

    public static boolean hasAnswered(Context context, String chatRoomId) {
        return getSharedPreferences(context).contains(chatRoomId);
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String name = "subscriptions_" + uid;
        return context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    /**
     * Toggle subscription state and return the new value.
     *
     * @param context    context for loading shared preferences
     * @param chatRoomId chat room id
     *
     * @return Toggled value
     */
    public static boolean toggleSubscription(Context context, String chatRoomId) {
        boolean subscribed = isSubscribedToPush(context, chatRoomId);

        if (subscribed) {
            unsubscribe(context, chatRoomId);
        } else {
            subscribe(context, chatRoomId);
        }

        return !subscribed;
    }

    public static boolean isSubscribedToPush(Context context, String chatRoomId) {
        return getSharedPreferences(context).getBoolean(chatRoomId, false);
    }

    public static void subscribe(Context context, String chatRoomId) {
        FirebaseMessaging.getInstance().subscribeToTopic(getTopic(chatRoomId))
            .addOnSuccessListener(aVoid -> setSubscription(context, chatRoomId, true));
    }

    private static String getTopic(String chatRoomId) {
        return "chatroom.topic." + chatRoomId;
    }

    private static void setSubscription(Context context, String chatRoomId, boolean subscribe) {
        getSharedPreferences(context)
            .edit()
            .putBoolean(chatRoomId, subscribe)
            .apply();

        Toast.makeText(context,
                       subscribe ? R.string.notification_toast_subscribed
                                 : R.string.notification_toast_unsubscribed,
                       Toast.LENGTH_SHORT).show();
    }

    public static void unsubscribe(Context context, String chatRoomId) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(getTopic(chatRoomId))
            .addOnSuccessListener(aVoid -> setSubscription(context, chatRoomId, false));
    }
}
