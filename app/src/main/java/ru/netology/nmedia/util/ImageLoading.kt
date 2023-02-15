package utils

import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import ru.netology.nmedia.R

fun ImageView.load(
    url: String,
    @DrawableRes placeholder: Int = R.drawable.ic_loading_100dp,
    @DrawableRes fallback: Int = R.drawable.ic_error_100dp,
    timeOutMs: Int = 10000
) {
    Glide.with(this)
        .load(url)
        .timeout(timeOutMs)
        .circleCrop()
        .placeholder(placeholder)
        .error(fallback)
        .into(this)
}

fun ImageView.loadImage(
    url: String,
    timeOutMs: Int = 10000
) {
    Glide.with(this)
        .load(url)
        .timeout(timeOutMs)
        .into(this)
}