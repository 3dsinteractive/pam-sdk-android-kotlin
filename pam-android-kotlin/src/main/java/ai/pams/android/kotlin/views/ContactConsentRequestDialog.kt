package ai.pams.android.kotlin.views

import ai.pams.android.kotlin.R
import ai.pams.android.kotlin.databinding.ConsentFragmentBinding
import ai.pams.android.kotlin.models.consent.contact.ContactConsentModel
import ai.pams.android.kotlin.models.consent.tracking.message.ConsentOption
import ai.pams.android.kotlin.views.adapters.ConsentOptionListAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager


class ContactConsentRequestDialog(
    val consentMessage: ContactConsentModel?
) : DialogFragment() {

    private var _binding: ConsentFragmentBinding? = null
    private val binding get() = _binding!!

    private var currentLang = "th"
    private var consentOptions: MutableList<ConsentOption> = mutableListOf()

    private val listAdapter = ConsentOptionListAdapter()

    var onAccept: ((Map<String,Boolean>)->Unit)? = null

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

        val width = view.context.resources.displayMetrics.widthPixels * 0.98
        val height = view.context.resources.displayMetrics.heightPixels * 0.7

        dialog?.window?.setLayout(width.toInt(), height.toInt())

        val langs: Array<String> =
            consentMessage?.setting?.availableLanguages?.toTypedArray() ?: arrayOf()
        val adapter = ArrayAdapter(view.context, R.layout.spinner_item, langs)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.languageSpinner.adapter = adapter

        binding.languageSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    currentLang =
                        consentMessage?.setting?.availableLanguages?.get(position).toString()
                    renderPopup()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }

        binding.acceptAllBtn.setOnClickListener {
            acceptAll()
        }

        binding.saveSettingBtn.setOnClickListener {
            saveSetting()
        }

        currentLang = consentMessage?.setting?.defaultLanguage ?: ""

        val layoutManager = LinearLayoutManager(this.context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL

        binding.listview.layoutManager = layoutManager
        binding.listview.adapter = listAdapter
        renderPopup()
    }

    private fun createConsentOptionArray(){
        consentOptions = mutableListOf()

        consentMessage?.setting?.termsAndConditions?.let{
            if(it.is_enabled == true) {
                it.is_allow = true
                it.title = "Term & Conditions"
                it.require = true
                consentOptions.add(it)
            }
        }

        consentMessage?.setting?.privacyOverview?.let{
            if(it.is_enabled == true) {
                it.is_allow = true
                it.title = "Privacy OverView"
                it.require = true
                consentOptions.add(it)
            }
        }

        consentMessage?.setting?.email?.let{
            if(it.is_enabled == true) {
                it.is_allow = true
                it.title = "Email"
                it.require = true
                consentOptions.add(it)
            }
        }

        consentMessage?.setting?.sms?.let{
            if(it.is_enabled == true) {
                it.is_allow = true
                it.title = "SMS"
                it.require = true
                consentOptions.add(it)
            }
        }

        consentMessage?.setting?.line?.let{
            if(it.is_enabled == true) {
                it.is_allow = true
                it.title = "Line"
                it.require = true
                consentOptions.add(it)
            }
        }

        consentMessage?.setting?.line?.let{
            if(it.is_enabled == true) {
                it.is_allow = true
                it.title = "Line"
                it.require = true
                consentOptions.add(it)
            }
        }

        consentMessage?.setting?.facebookMessenger?.let{
            if(it.is_enabled == true) {
                it.is_allow = true
                it.title = "Facebook Messenger"
                it.require = true
                consentOptions.add(it)
            }
        }

    }

    private fun acceptAll(){
        val acceptList = mapOf(
            "_allow_terms_and_conditions" to true,
            "_allow_privacy_overview" to true,
            "_allow_email" to  true,
            "_allow_sms" to true,
            "_allow_line" to true,
            "_allow_facebook_messenger" to true
        )

        onAccept?.invoke(acceptList)
        this.dismiss()
    }

    private fun saveSetting() {
        val acceptList = mapOf(
            "_allow_terms_and_conditions" to (consentMessage?.setting?.termsAndConditions?.is_allow ?: true),
            "_allow_privacy_overview" to (consentMessage?.setting?.privacyOverview?.is_allow ?: true),
            "_allow_email" to  (consentMessage?.setting?.email?.is_allow ?: true),
            "_allow_sms" to (consentMessage?.setting?.sms?.is_allow ?: true),
            "_allow_line" to (consentMessage?.setting?.line?.is_allow ?: true),
            "_allow_facebook_messenger" to (consentMessage?.setting?.facebookMessenger?.is_allow ?: true),
        )
        onAccept?.invoke(acceptList)
        this.dismiss()
    }

    private fun renderPopup(){
        createConsentOptionArray()
        listAdapter.updateList(consentOptions, currentLang)
        binding.titleText.text = when(currentLang){
            "th"->consentMessage?.setting?.consentDetailTitle?.th
            else->consentMessage?.setting?.consentDetailTitle?.en
        }
    }

}