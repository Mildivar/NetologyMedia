package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.FragmentAuthorizationBinding
import ru.netology.nmedia.databinding.FragmentMediaBinding
import ru.netology.nmedia.model.AuthModel
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.viewmodel.AuthViewModel

class AuthorizationFragment : Fragment() {
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

        val authorizationViewModel by viewModels<AuthViewModel>()

        binding.confirmButton.setOnClickListener {
            val login = binding.login.text.toString()
            val password = binding.password.text.toString()
            authorizationViewModel.authorization(login, password)
//            AppAuth.init(it.context)
            AndroidUtils.hideKeyboard(requireView())
            findNavController().navigateUp()
        }





        return binding.root
    }
}