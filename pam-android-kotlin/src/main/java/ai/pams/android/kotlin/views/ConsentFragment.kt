package ai.pams.android.kotlin.views

import ai.pams.android.kotlin.databinding.ConsentFragmentBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class ConsentFragment: Fragment() {

    private var _binding: ConsentFragmentBinding? = null
    private val binding get() = _binding!!

    private var currentLang = "th"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ConsentFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.switchBtn.setOnClickListener{
//            when(currentLang){
//                "th"->switchLang("en")
//                "en"->switchLang("th")
//            }
//        }

    }

//    private fun switchLang(lang: String){
//        currentLang = lang
//        when(lang){
//            "th"->{
//                binding.btnTh.setBackgroundResource(R.mipmap.switch_bg_right)
//                binding.btnEn.setBackgroundResource(R.mipmap.switch_bg_left_disable)
//            }
//            "en"->{
//                binding.btnTh.setBackgroundResource(R.mipmap.switch_bg_right_disable)
//                binding.btnEn.setBackgroundResource(R.mipmap.switch_bg_left)
//            }
//        }
//    }

}