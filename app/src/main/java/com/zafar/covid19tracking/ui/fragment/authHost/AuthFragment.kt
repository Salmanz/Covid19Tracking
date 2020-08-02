package com.zafar.covid19tracking.ui.fragment.authHost

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.zafar.covid19tracking.databinding.FragmentAuthBinding

class AuthFragment : Fragment() {
    private lateinit var binding: FragmentAuthBinding
    private lateinit var viewModel: AuthViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        binding = FragmentAuthBinding.inflate(inflater, container, false)
        binding.authViewModel = viewModel
        binding.lifecycleOwner = this

//        viewModel.navigateToLogin.observe(viewLifecycleOwner, Observer {
//            if (it) {
//                this.findNavController()
//                    .navigate(AuthFragmentDirections.actionAuthFragmentToLoginFragment())
//                viewModel.doneNavigationToLogin()
//            }
//        })
//        viewModel.navigateToSignUp.observe(viewLifecycleOwner, Observer {
//            if (it) {
//                this.findNavController()
//                    .navigate(AuthFragmentDirections.actionAuthFragmentToSignUpFragment())
//                viewModel.doneNavigationToSignUp()
//            }
//        })

        return binding.root
    }
}