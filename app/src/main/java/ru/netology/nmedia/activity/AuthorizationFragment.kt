package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.FragmentAuthorizationBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.viewmodel.AuthViewModel
import javax.inject.Inject

@AndroidEntryPoint
class AuthorizationFragment : Fragment() {

    @Inject
    lateinit var appAuth: AppAuth
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAuthorizationBinding.inflate(
            inflater,
            container,
            false
        )

        val authorizationViewModel: AuthViewModel by activityViewModels()

        binding.confirmButton.setOnClickListener {
            val login = binding.login.text.toString()
            val password = binding.password.text.toString()
            authorizationViewModel.authorization(login, password)
//            AppAuth.init(it.context)
            AndroidUtils.hideKeyboard(requireView())
        }

        authorizationViewModel.signInApp.observe(viewLifecycleOwner) {
            appAuth.setAuth(it.id,it.token)
            findNavController().navigateUp()
        }

        authorizationViewModel.state.observe(viewLifecycleOwner) {
            if (it.wrongAuth) {
                Snackbar.make(binding.root, "Wrong login or password", Snackbar.LENGTH_LONG).show()
            }
        }
        return binding.root
    }
}