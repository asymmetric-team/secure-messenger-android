package com.safechat.conversation.select

class ConversationsListControllerImpl(
        val repository: ConversationsListRepository,
        val view: ConversationsListView) : ConversationsListController {

    override fun onCreate() {
        val conversations = repository.getConversationsMessages()
        view.showConversations(conversations)
    }
}