package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.netology.nmedia.databinding.FragmentMediaBinding
import ru.netology.nmedia.util.StringArg
import utils.loadImage

class MediaFragment : Fragment() {

    companion object {
        var Bundle.textArg by StringArg
    }

    val imageUrl = "http://10.0.2.2:9999/media/${arguments?.textArg}"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMediaBinding.inflate(
            inflater,
            container,
            false
        )

        binding.image.loadImage(imageUrl)

        return binding.root
    }
}
