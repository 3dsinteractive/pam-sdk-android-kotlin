package ai.pams.android.kotlin.dialogs

import ai.pams.android.kotlin.Pam
import ai.pams.android.kotlin.PamResponse
import ai.pams.android.kotlin.R
import ai.pams.android.kotlin.databinding.ConsentFragmentBinding
import ai.pams.android.kotlin.models.consent.contact.ContactConsentModel
import ai.pams.android.kotlin.models.consent.tracking.message.ConsentOption
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
        val height = view.context.resources.displayMetrics.heightPixels * 0.75

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

    fun fromHtml(html: String?): Spanned? {
        return if (html == null) {
            // return an empty spannable if the html is null
            SpannableString("")
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // FROM_HTML_MODE_LEGACY is the behaviour that was used for versions below android N
            // we are using this flag to give a consistent behaviour
            Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(html)
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

    private fun createConsentOptionArray(){
        consentOptions = mutableListOf()

        consentMessage?.setting?.termsAndConditions?.let{
            if(it.is_enabled == true) {
                it.title = "Term & Conditions"
                it.require = true
                consentOptions.add(it)
            }
        }

        consentMessage?.setting?.privacyOverview?.let{
            if(it.is_enabled == true) {
                it.is_allow = it.is_allow
                it.title = "Privacy OverView"
                it.require = true
                consentOptions.add(it)
            }
        }

        consentMessage?.setting?.pushNotification?.let{
            if(it.is_enabled == true) {
                it.title = "Push Notification"
                it.require = false
                consentOptions.add(it)
            }
        }

        consentMessage?.setting?.email?.let{
            if(it.is_enabled == true) {
                it.title = "Email"
                it.require = false
                consentOptions.add(it)
            }
        }

        consentMessage?.setting?.sms?.let{
            if(it.is_enabled == true) {
                it.title = "SMS"
                it.require = false
                consentOptions.add(it)
            }
        }

        consentMessage?.setting?.line?.let{
            if(it.is_enabled == true) {
                it.title = "Line"
                it.require = false
                consentOptions.add(it)
            }
        }

        consentMessage?.setting?.facebookMessenger?.let{
            if(it.is_enabled == true) {
                it.title = "Facebook Messenger"
                it.require = false
                consentOptions.add(it)
            }
        }

    }

    private fun acceptAll(){
        val acceptList = mutableMapOf<String, Boolean>()

        consentMessage?.setting?.termsAndConditions?.is_enabled?.let{
            if(it) acceptList["_allow_terms_and_conditions"] = true
        }

        consentMessage?.setting?.privacyOverview?.is_enabled?.let{
            if(it) acceptList["_allow_privacy_overview"] = true
        }

        consentMessage?.setting?.email?.is_enabled?.let{
            if(it) acceptList["_allow_email"] = true
        }

        consentMessage?.setting?.sms?.is_enabled?.let{
            if(it) acceptList["_allow_sms"] = true
        }

        consentMessage?.setting?.line?.is_enabled?.let{
            if(it) acceptList["_allow_line"] = true
        }

        consentMessage?.setting?.facebookMessenger?.is_enabled?.let{
            if(it) acceptList["_allow_facebook_messenger"] = true
        }

        consentMessage?.setting?.pushNotification?.is_enabled?.let{
            if(it) acceptList["_allow_push_notification"] = true
        }

        onAccept?.invoke(acceptList)
        this.dismiss()
    }

    private fun saveSetting() {

        val acceptList = mutableMapOf<String, Boolean>()

        consentMessage?.setting?.termsAndConditions?.let{
            if(it.is_enabled == true) acceptList["_allow_terms_and_conditions"] = it.is_allow
        }

        consentMessage?.setting?.privacyOverview?.let{
            if(it.is_enabled == true) acceptList["_allow_privacy_overview"] = it.is_allow
        }

        consentMessage?.setting?.email?.let{
            if(it.is_enabled == true) acceptList["_allow_email"] = it.is_allow
        }

        consentMessage?.setting?.sms?.let{
            if(it.is_enabled == true) acceptList["_allow_sms"] = it.is_allow
        }

        consentMessage?.setting?.line?.let{
            if(it.is_enabled == true) acceptList["_allow_line"] = it.is_allow
        }

        consentMessage?.setting?.facebookMessenger?.let{
            if(it.is_enabled == true) acceptList["_allow_facebook_messenger"] = it.is_allow
        }

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
        binding.iconText.text = consentMessage?.styleConfiguration?.consentDetail?.popupMainIcon?.toUpperCase()
    }

}