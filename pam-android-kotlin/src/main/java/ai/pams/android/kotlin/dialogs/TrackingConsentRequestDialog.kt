package ai.pams.android.kotlin.dialogs

import ai.pams.android.kotlin.R
import ai.pams.android.kotlin.databinding.ConsentFragmentBinding
import ai.pams.android.kotlin.models.consent.tracking.allow.TrackingConsentUserPermissions
import ai.pams.android.kotlin.models.consent.tracking.message.ConsentOption
import ai.pams.android.kotlin.models.consent.tracking.message.TrackingConsentMessageConfigurations
import ai.pams.android.kotlin.dialogs.adapters.ConsentOptionListAdapter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.os.Build

import android.text.SpannableString

import android.text.Spanned





class TrackingConsentRequestDialog(
    val consentMessage: TrackingConsentMessageConfigurations?,
    private val trackingConsentAllowUserPermissions: TrackingConsentUserPermissions?
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
        val height = view.context.resources.displayMetrics.heightPixels * 0.75

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

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        initFullVersionReader()
        renderPopup()
    }

    private fun initFullVersionReader(){
        listAdapter.onShowFullDescription = {
            showFullVersion(it)
        }

        binding.scrollView.visibility = View.GONE
        binding.closeFullVersionBtn.visibility = View.GONE
        binding.closeFullVersionBtn.setOnClickListener{
            it.visibility = View.GONE
            binding.scrollView.visibility = View.GONE
            binding.languageSpinner.visibility = View.VISIBLE
            binding.closeFullVersionBtn.visibility = View.GONE
            binding.acceptAllBtn.visibility = View.VISIBLE
            binding.saveSettingBtn.visibility = View.VISIBLE
        }
    }

    private fun showFullVersion(text:String){
        binding.languageSpinner.visibility = View.GONE
        binding.scrollView.visibility = View.VISIBLE
        val html = fromHtml(text)
        binding.fullVersionText.text = html
        binding.scrollView.visibility = View.VISIBLE
        binding.closeFullVersionBtn.visibility = View.VISIBLE
        binding.acceptAllBtn.visibility = View.GONE
        binding.saveSettingBtn.visibility = View.GONE
    }

    private fun fromHtml(html: String?): Spanned? {
        return when {
            html == null -> {
                // return an empty spannable if the html is null
                SpannableString("")
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
                // FROM_HTML_MODE_LEGACY is the behaviour that was used for versions below android N
                // we are using this flag to give a consistent behaviour
                Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
            }
            else -> {
                Html.fromHtml(html)
            }
        }
    }

    private fun createConsentOptionArray(){
        consentOptions = mutableListOf()

        consentMessage?.setting?.terms_and_conditions?.let{
            if(it.is_enabled == true) {
                if(trackingConsentAllowUserPermissions == null){
                    it.is_allow = true
                }
                it.title = "Term & Conditions"
                it.require = true
                consentOptions.add(it)
            }
        }

        consentMessage?.setting?.privacy_overview?.let{
            if(it.is_enabled == true) {
                if(trackingConsentAllowUserPermissions == null){
                    it.is_allow = true
                }
                it.title = "Privacy OverView"
                it.require = true
                consentOptions.add(it)
            }
        }

        consentMessage?.setting?.necessary_cookies?.let{
            if(it.is_enabled == true) {
                if(trackingConsentAllowUserPermissions == null){
                    it.is_allow = true
                }
                it.title = "Necessary Cookies"
                it.require = true
                consentOptions.add(it)
            }
        }

        consentMessage?.setting?.preferences_cookies?.let{
            if(it.is_enabled == true) {
                if(trackingConsentAllowUserPermissions == null){
                    it.is_allow = true
                }
                it.title = "Preferences Cookies"
                it.require = false
                consentOptions.add(it)
            }
        }

        consentMessage?.setting?.analytics_cookies?.let{
            if(it.is_enabled == true) {
                if(trackingConsentAllowUserPermissions == null){
                    it.is_allow = true
                }
                it.title = "Analytics Cookies"
                it.require = false
                consentOptions.add(it)
            }
        }

        consentMessage?.setting?.marketing_cookies?.let{
            if(it.is_enabled == true) {
                if(trackingConsentAllowUserPermissions == null){
                    it.is_allow = true
                }
                it.title = "Marketing Cookies"
                it.require = false
                consentOptions.add(it)
            }
        }

        consentMessage?.setting?.social_media_cookies?.let{
            if(it.is_enabled == true) {
                if(trackingConsentAllowUserPermissions == null){
                    it.is_allow = true
                }
                it.title = "Social Media Cookies"
                it.require = false
                consentOptions.add(it)
            }
        }

        consentMessage?.setting?.sms?.let{
            if(it.is_enabled == true){
                if(trackingConsentAllowUserPermissions == null){
                    it.is_allow = true
                }
                it.require = false
                consentOptions.add(it)
            }
        }

        consentMessage?.setting?.line?.let{
            if(it.is_enabled == true) {
                if(trackingConsentAllowUserPermissions == null){
                    it.is_allow = true
                }
                it.require = false
                consentOptions.add(it)
            }
        }

        consentMessage?.setting?.facebook_messenger?.let{
            if(it.is_enabled == true) {
                if(trackingConsentAllowUserPermissions == null){
                    it.is_allow = true
                }
                it.require = false
                consentOptions.add(it)
            }
        }

    }

    private fun acceptAll(){
        val acceptList = mutableMapOf<String, Boolean>()

        consentMessage?.setting?.terms_and_conditions?.is_enabled?.let{
            if(it) acceptList["_allow_terms_and_conditions"] = true
        }

        consentMessage?.setting?.privacy_overview?.is_enabled?.let{
            if(it) acceptList["_allow_privacy_overview"] = true
        }

        consentMessage?.setting?.necessary_cookies?.is_enabled?.let{
            if(it) acceptList["_allow_necessary_cookies"] = true
        }

        consentMessage?.setting?.preferences_cookies?.is_enabled?.let{
            if(it) acceptList["_allow_preferences_cookies"] = true
        }

        consentMessage?.setting?.analytics_cookies?.is_enabled?.let{
            if(it) acceptList["_allow_analytics_cookies"] = true
        }

        consentMessage?.setting?.marketing_cookies?.is_enabled?.let{
            if(it) acceptList["_allow_marketing_cookies"] = true
        }

        consentMessage?.setting?.social_media_cookies?.is_enabled?.let{
            if(it) acceptList["_allow_social_media_cookies"] = true
        }

        onAccept?.invoke(acceptList)
        this.dismiss()
    }

    private fun saveSetting() {

        val acceptList = mutableMapOf<String, Boolean>()

        consentMessage?.setting?.terms_and_conditions?.let{
            if(it.is_enabled == true) acceptList["_allow_terms_and_conditions"] = it.is_allow
        }

        consentMessage?.setting?.privacy_overview?.let{
            if(it.is_enabled == true) acceptList["_allow_privacy_overview"] = it.is_allow
        }

        consentMessage?.setting?.necessary_cookies?.let{
            if(it.is_enabled == true) acceptList["_allow_necessary_cookies"] = it.is_allow
        }

        consentMessage?.setting?.preferences_cookies?.let{
            if(it.is_enabled == true) acceptList["_allow_preferences_cookies"] = it.is_allow
        }

        consentMessage?.setting?.analytics_cookies?.let{
            if(it.is_enabled == true) acceptList["_allow_analytics_cookies"] = it.is_allow
        }

        consentMessage?.setting?.marketing_cookies?.let{
            if(it.is_enabled == true) acceptList["_allow_marketing_cookies"] = it.is_allow
        }

        consentMessage?.setting?.social_media_cookies?.let{
            if(it.is_enabled == true) acceptList["_allow_social_media_cookies"] = it.is_allow
        }

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