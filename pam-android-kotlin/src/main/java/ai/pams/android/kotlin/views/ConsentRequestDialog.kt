package ai.pams.android.kotlin.views

import ai.pams.android.kotlin.R
import ai.pams.android.kotlin.databinding.ConsentFragmentBinding
import ai.pams.android.kotlin.models.consent.tracking.allow.TrackingConsentAllowModel
import ai.pams.android.kotlin.models.consent.tracking.message.ConsentOption
import ai.pams.android.kotlin.models.consent.tracking.message.TrackingConsentModel
import ai.pams.android.kotlin.views.adapters.ConsentOptionListAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager


class ConsentRequestDialog(
    val consentMessage: TrackingConsentModel?,
    private val consentAllowModel: TrackingConsentAllowModel?
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
            consentMessage?.setting?.available_languages?.toTypedArray() ?: arrayOf()
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
                        consentMessage?.setting?.available_languages?.get(position).toString()
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

        currentLang = consentMessage?.setting?.default_language ?: ""

        val layoutManager = LinearLayoutManager(this.context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL

        binding.listview.layoutManager = layoutManager
        binding.listview.adapter = listAdapter
        renderPopup()
    }

    private fun createConsentOptionArray(){
        consentOptions = mutableListOf()

        consentMessage?.setting?.terms_and_conditions?.let{
            if(it.is_enabled == true) {
                if(consentAllowModel == null){
                    it.is_allow = true
                }
                consentOptions.add(it)
            }
        }

        consentMessage?.setting?.privacy_overview?.let{
            if(it.is_enabled == true) {
                if(consentAllowModel == null){
                    it.is_allow = true
                }
                consentOptions.add(it)
            }
        }

        consentMessage?.setting?.necessary_cookies?.let{
            if(it.is_enabled == true) {
                if(consentAllowModel == null){
                    it.is_allow = true
                }
                consentOptions.add(it)
            }
        }

        consentMessage?.setting?.preferences_cookies?.let{
            if(it.is_enabled == true) {
                if(consentAllowModel == null){
                    it.is_allow = true
                }
                consentOptions.add(it)
            }
        }

        consentMessage?.setting?.analytics_cookies?.let{
            if(it.is_enabled == true) {
                if(consentAllowModel == null){
                    it.is_allow = true
                }
                consentOptions.add(it)
            }
        }

        consentMessage?.setting?.marketing_cookies?.let{
            if(it.is_enabled == true) {
                if(consentAllowModel == null){
                    it.is_allow = true
                }
                consentOptions.add(it)
            }
        }

        consentMessage?.setting?.social_media_cookies?.let{
            if(it.is_enabled == true) {
                if(consentAllowModel == null){
                    it.is_allow = true
                }
                consentOptions.add(it)
            }
        }

        consentMessage?.setting?.sms?.let{
            if(it.is_enabled == true){
                if(consentAllowModel == null){
                    it.is_allow = true
                }
                consentOptions.add(it)
            }
        }

        consentMessage?.setting?.line?.let{
            if(it.is_enabled == true) {
                if(consentAllowModel == null){
                    it.is_allow = true
                }
                consentOptions.add(it)
            }
        }

        consentMessage?.setting?.facebook_messenger?.let{
            if(it.is_enabled == true) {
                if(consentAllowModel == null){
                    it.is_allow = true
                }
                consentOptions.add(it)
            }
        }

    }

    private fun acceptAll(){
        val acceptList = mapOf(
            "_allow_terms_and_conditions" to true,
            "_allow_privacy_overview" to true,
            "_allow_necessary_cookies" to  true,
            "_allow_preferences_cookies" to true,
            "_allow_analytics_cookies" to true,
            "_allow_marketing_cookies" to true,
            "_allow_social_media_cookies" to true
        )

        onAccept?.invoke(acceptList)
        this.dismiss()
    }

    private fun saveSetting() {
        val acceptList = mapOf(
            "_allow_terms_and_conditions" to (consentMessage?.setting?.terms_and_conditions?.is_allow ?: true),
            "_allow_privacy_overview" to (consentMessage?.setting?.privacy_overview?.is_allow ?: true),
            "_allow_necessary_cookies" to  (consentMessage?.setting?.necessary_cookies?.is_allow ?: true),
            "_allow_preferences_cookies" to (consentMessage?.setting?.preferences_cookies?.is_allow ?: true),
            "_allow_analytics_cookies" to (consentMessage?.setting?.analytics_cookies?.is_allow ?: true),
            "_allow_marketing_cookies" to (consentMessage?.setting?.marketing_cookies?.is_allow ?: true),
            "_allow_social_media_cookies" to (consentMessage?.setting?.social_media_cookies?.is_allow ?: true)
        )

        onAccept?.invoke(acceptList)
        this.dismiss()
    }

    private fun renderPopup(){
        createConsentOptionArray()
        listAdapter.updateList(consentOptions, currentLang)
        binding.titleText.text = when(currentLang){
            "th"->consentMessage?.setting?.consent_detail_title?.th
            else->consentMessage?.setting?.consent_detail_title?.en
        }
    }

}