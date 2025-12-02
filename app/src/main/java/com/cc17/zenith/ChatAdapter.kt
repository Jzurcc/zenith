package com.cc17.zenith

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.noties.markwon.Markwon

class ChatAdapter(
    private var messageList: List<ChatMessage>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_USER = 1
    private val VIEW_TYPE_BOT = 2
    private val VIEW_TYPE_LOADING = 3

    private lateinit var markwon: Markwon

    // --- ViewHolders ---

    class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val messageText: TextView = view.findViewById(R.id.chat_message_text)
    }

    class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val dot1: View = view.findViewById(R.id.dot1)
        private val dot2: View = view.findViewById(R.id.dot2)
        private val dot3: View = view.findViewById(R.id.dot3)

        init {
            // Start the wave animation immediately when view is created
            animateDot(dot1, 0)
            animateDot(dot2, 150)
            animateDot(dot3, 300)
        }

        private fun animateDot(dot: View, delay: Long) {
            // Move dot UP by 10 pixels, then back down
            val animator = ObjectAnimator.ofFloat(dot, "translationY", 0f, -15f)
            animator.duration = 500 // Speed of one jump
            animator.repeatMode = ValueAnimator.REVERSE
            animator.repeatCount = ValueAnimator.INFINITE
            animator.startDelay = delay
            animator.start()
        }
    }

    // --- Adapter Overrides ---

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        markwon = Markwon.create(recyclerView.context)
    }

    override fun getItemViewType(position: Int): Int {
        val message = messageList[position]
        return when {
            message.isLoading -> VIEW_TYPE_LOADING
            message.isUser -> VIEW_TYPE_USER
            else -> VIEW_TYPE_BOT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_USER -> {
                val view = inflater.inflate(R.layout.user_bubble_layout, parent, false)
                MessageViewHolder(view)
            }
            VIEW_TYPE_LOADING -> {
                val view = inflater.inflate(R.layout.loading_bubble, parent, false)
                LoadingViewHolder(view)
            }
            else -> { // BOT
                val view = inflater.inflate(R.layout.bot_bubble_layout, parent, false)
                MessageViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MessageViewHolder) {
            val message = messageList[position]
            if (!message.isUser) {
                markwon.setMarkdown(holder.messageText, message.message)
            } else {
                holder.messageText.text = message.message
            }
        }
        // No binding needed for LoadingViewHolder as animation starts in init block
    }

    override fun getItemCount(): Int = messageList.size

    fun updateMessages(newMessages: List<ChatMessage>) {
        messageList = newMessages
        notifyDataSetChanged()
    }
}