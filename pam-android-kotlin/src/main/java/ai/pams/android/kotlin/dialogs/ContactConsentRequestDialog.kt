package ai.pams.android.kotlin.dialogs

import ai.pams.android.kotlin.R
import ai.pams.android.kotlin.dialogs.adapters.ConsentOptionListAdapter
import ai.pams.android.kotlin.models.consent.contact.ContactConsentModel
import ai.pams.android.kotlin.models.consent.tracking.message.ConsentOption
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class ContactConsentRequestDialog(
    val consentMessage: ContactConsentModel?
) : DialogFragment() {

    private var currentLang = "th"
    private var consentOptions: MutableList<ConsentOption> = mutableListOf()

    private val listAdapter = ConsentOptionListAdapter()

    var onAccept: ((Map<String,Boolean>)->Unit)? = null
    var languageSpinner: Spinner? = null
    var acceptAllBtn:Button? = null
    var saveSettingBtn:Button? = null
    var listview:RecyclerView? = null
    var scrollView: ConstraintLayout? = null
    var closeFullVersionBtn:Button? = null
    var fullVersionText: TextView? = null
    var titleText: TextView? = null
    var iconText: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.consent_fragment, container)
    }

    private fun findView(view: View){
        languageSpinner = view.findViewById(R.id.languageSpinner)
        acceptAllBtn = view.findViewById(R.id.accept_all_btn)
        saveSettingBtn = view.findViewById(R.id.save_setting_btn)
        listview = view.findViewById(R.id.listview)
        scrollView = view.findViewById(R.id.scroll_view)
        closeFullVersionBtn = view.findViewById(R.id.close_full_version_btn)
        fullVersionText = view.findViewById(R.id.full_version_text)
        titleText = view.findViewById(R.id.title_text)
        iconText = view.findViewById(R.id.icon_text)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        findView(view)

        val width = view.context.resources.displayMetrics.widthPixels * 0.98
        val height = view.context.resources.displayMetrics.heightPixels * 0.75

        dialog?.window?.setLayout(width.toInt(), height.toInt())

        val langs: Array<String> =
            consentMessage?.setting?.availableLanguages?.toTypedArray() ?: arrayOf()
        val adapter = ArrayAdapter(view.context, R.layout.spinner_item, langs)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languageSpinner?.adapter = adapter

        languageSpinner?.onItemSelectedListener =
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

        acceptAllBtn?.setOnClickListener {
            acceptAll()
        }

        saveSettingBtn?.setOnClickListener {
            saveSetting()
        }

        currentLang = consentMessage?.setting?.defaultLanguage ?: ""

        val layoutManager = LinearLayoutManager(this.context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL

        listview?.layoutManager = layoutManager
        listview?.adapter = listAdapter

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        initFullVersionReader()
        renderPopup()
    }

    private fun initFullVersionReader(){
        listAdapter.onShowFullDescription = {
            showFullVersion(it)
        }

        scrollView?.visibility = View.GONE
        closeFullVersionBtn?.visibility = View.GONE
        closeFullVersionBtn?.setOnClickListener{
            it.visibility = View.GONE
            scrollView?.visibility = View.GONE
            languageSpinner?.visibility = View.VISIBLE
            closeFullVersionBtn?.visibility = View.GONE
            acceptAllBtn?.visibility = View.VISIBLE
            saveSettingBtn?.visibility = View.VISIBLE
        }
    }

    @SuppressWarnings("deprecation")
    private fun fromHtml(source: String?): Spanned? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(source)
        }
    }

    private fun showFullVersion(text:String){
        languageSpinner?.visibility = View.GONE
        scrollView?.visibility = View.VISIBLE
        val html = fromHtml(text)
        fullVersionText?.text = html
        scrollView?.visibility = View.VISIBLE
        closeFullVersionBtn?.visibility = View.VISIBLE
        acceptAllBtn?.visibility = View.GONE
        saveSettingBtn?.visibility = View.GONE
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
        titleText?.text = when(currentLang){
            "th"->consentMessage?.setting?.consentDetailTitle?.th
            else->consentMessage?.setting?.consentDetailTitle?.en
        }
        iconText?.text = consentMessage?.styleConfiguration?.consentDetail?.popupMainIcon?.uppercase()
    }

}