package ru.netology.nmedia.adapter

import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.AttachmentTypes
import ru.netology.nmedia.dto.Post
import utils.load
import utils.loadImage

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {


    fun bind(post: Post) {
        val avatarUrl = "http://10.0.2.2:9999/avatars/${post.authorAvatar}"
        val attachmentUrl = "http://10.0.2.2:9999/media/${post.attachment?.url}"
        binding.apply {
            author.text = post.author
            avatar.load(avatarUrl)
            published.text = post.published
            content.text = post.content
            // в адаптере
            like.isChecked = post.likedByMe
            like.text = "${post.likes}"
            attachmentImage.let {
                if ((post.attachment != null) && post.attachment.type == AttachmentTypes.IMAGE) {
                    attachmentImage.isVisible = true
                    it.loadImage(attachmentUrl)
                    attachmentImage.setOnClickListener {
                        onInteractionListener.onImage(post)
                    }
                }
                else attachmentImage.isVisible = false
            }
            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(post)
                                true
                            }
                            R.id.edit -> {
                                onInteractionListener.onEdit(post)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }

            like.setOnClickListener {
                onInteractionListener.onLike(post)
            }

            share.setOnClickListener {
                onInteractionListener.onShare(post)
            }
        }
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}

