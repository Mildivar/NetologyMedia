package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.di.DependencyContainer
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel
import ru.netology.nmedia.viewmodel.ViewModelFactory

class NewPostFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }
    private val dependencyContainer = DependencyContainer.getInstance()

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment,
        factoryProducer = {
            ViewModelFactory(dependencyContainer.repository,dependencyContainer.appAuth)
        })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewPostBinding.inflate(
            inflater,
            container,
            false
        )
        val photoLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                when (it.resultCode) {
                    ImagePicker.RESULT_ERROR -> {
                        Snackbar.make(binding.root,getString(R.string.error),Snackbar.LENGTH_LONG).show()
                    }
                    else -> {
                        val uri = it.data?.data ?: return@registerForActivityResult
                        viewModel.changePhoto(uri.toFile(),uri)
                    }
                }
            }

        arguments?.textArg
            ?.let(binding.edit::setText)

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.new_post_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                when (menuItem.itemId) {
                    R.id.new_post -> {
                        viewModel.changeContent(binding.edit.text.toString())
                        viewModel.save()
                        AndroidUtils.hideKeyboard(requireView())
                        true
                    }
                    else -> false
                }


        }, viewLifecycleOwner)


        binding.clear.setOnClickListener {
            viewModel.clearPhoto()
        }

        binding.photo.setOnClickListener{
            ImagePicker.with(this)
                .cameraOnly()
                .crop()
                .compress(2048)
                .createIntent(photoLauncher::launch)
        }

        binding.gallery.setOnClickListener{
            ImagePicker.with(this)
                .galleryOnly()
                .crop()
                .compress(2048)
                .createIntent(photoLauncher::launch)
        }

        viewModel.media.observe(viewLifecycleOwner){media ->
            if (media == null){
                binding.previewContainer.isVisible = false
                return@observe
            }
            binding.previewContainer.isVisible = true
            binding.preview.setImageURI(media.uri)
        }

//        binding.ok.setOnClickListener {
//            viewModel.changeContent(binding.edit.text.toString())
//            viewModel.save()
//            AndroidUtils.hideKeyboard(requireView())
//        }

        viewModel.postCreated.observe(viewLifecycleOwner) {
            viewModel.refreshPosts()
            findNavController().navigateUp()
        }
        return binding.root
    }
}