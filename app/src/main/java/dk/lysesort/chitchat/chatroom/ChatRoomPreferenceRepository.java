package dk.lysesort.chitchat.chatroom;

import android.content.Context;
import android.content.SharedPreferences;

public class ChatRoomPreferenceRepository {

    private ChatRoomPreferenceRepository() {

    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences("subscriptions", Context.MODE_PRIVATE);
    }

    public static boolean hasAnswered(Context context, String chatRoomId) {
        return getSharedPreferences(context).contains(chatRoomId);
    }

    public static boolean isSubscribedToPush(Context context, String chatRoomId) {
        return getSharedPreferences(context).getBoolean(chatRoomId, false);
    }

    public static void subscribe(Context context, String chatRoomId) {
        getSharedPreferences(context)
            .edit()
            .putBoolean(chatRoomId, true)
            .apply();
    }

    public static void unsubscribe(Context context, String chatRoomId) {
        getSharedPreferences(context)
            .edit()
            .putBoolean(chatRoomId, false)
            .apply();
    }
}
