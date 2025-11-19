package com.cc17.zenith // Make sure this package matches your project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatAdapter(
    // It starts with an empty list
    private var messageList: List<ChatMessage>
) : RecyclerView.Adapter<ChatAdapter.MessageViewHolder>() {

    // View type constants
    private val VIEW_TYPE_USER = 1
    private val VIEW_TYPE_BOT = 2

    // This class just holds the reference to the TextView in your bubble
    class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val messageText: TextView = view.findViewById(R.id.chat_message_text)
    }

    override fun getItemViewType(position: Int): Int {
        // This is the magic: it checks who the message is from...
        return if (messageList[position].isUser) {
            VIEW_TYPE_USER // ...and returns the "user" type
        } else {
            VIEW_TYPE_BOT // ...or the "bot" type
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        // This inflates the correct layout based on the view type
        val layoutId = if (viewType == VIEW_TYPE_USER) {
            R.layout.user_bubble_layout
        } else {
            R.layout.bot_bubble_layout
        }
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        // This gets the message for the current row and sets the text
        val message = messageList[position]
        holder.messageText.text = message.message
    }

    override fun getItemCount(): Int = messageList.size

    // This is the function your Chatbot.kt Activity calls
    // to update the list with new messages
    fun updateMessages(newMessages: List<ChatMessage>) {
        messageList = newMessages
        // This tells the adapter to refresh the whole list
        notifyDataSetChanged()
    }
}