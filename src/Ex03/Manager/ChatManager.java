package Ex03.Manager;

import Ex03.Utils.SingleChatEntry;

import java.util.*;

// Manages the chat of the system
public class ChatManager {

    private final Map<String, List<SingleChatEntry>> m_GameNameToChatList;

    public ChatManager() {
        m_GameNameToChatList = new HashMap<>();
    }

    public void addChatString(String chatString, String username, String gameName) {
        List<SingleChatEntry> gameChatList = m_GameNameToChatList.get(gameName);

        if (gameChatList == null) {
            gameChatList = new LinkedList<>();
        }
        gameChatList.add(new SingleChatEntry(chatString, username));
        m_GameNameToChatList.put(gameName, gameChatList);
    }

    public List<SingleChatEntry> getChatEntries(int fromIndex, String gameName) {
        List<SingleChatEntry> chatList = m_GameNameToChatList.get(gameName);
        List<SingleChatEntry> chatSubList = null;

        if (chatList != null) {
            if (fromIndex < 0 || fromIndex >= chatList.size()) {
                fromIndex = 0;
            }
            chatSubList = chatList.subList(fromIndex, chatList.size());
        }

        return chatSubList;
    }

    public void ClearChatFromGame(String i_GameName) {
        m_GameNameToChatList.put(i_GameName, null);
    }
}