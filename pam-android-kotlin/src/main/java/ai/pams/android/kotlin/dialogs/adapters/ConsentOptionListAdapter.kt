package ai.pams.android.kotlin.dialogs.adapters

import ai.pams.android.kotlin.R
import ai.pams.android.kotlin.models.consent.tracking.message.ConsentOption
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class ConsentOptionListAdapter : RecyclerView.Adapter<ConsentViewHolder>() {

    private var cellList: List<CellTypes> = listOf()
    private var language: String = ""
    private var consentList: List<ConsentOption> = listOf()

    var onShowFullDescription: ((String) -> Unit)? = null

    sealed class CellTypes(val type: Int) {
        class Header(val consent: ConsentOption) : CellTypes(0)
        class Info(val consent: ConsentOption) : CellTypes(1)
    }

    fun updateList(consentList: List<ConsentOption>, language: String) {
        this.language = language
        val list: MutableList<CellTypes> = mutableListOf()
        this.consentList = consentList

        consentList.forEach {
            list.add(CellTypes.Header(it))
            if (it.is_expanded) {
                list.add(CellTypes.Info(it))
            }
        }

        val diffResult = DiffUtil.calculateDiff(PDPADiffCallback(cellList, list))

        cellList = list
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemViewType(position: Int): Int {
        return cellList[position].type
    }

    private fun collapseAll() {
        cellList.forEach {
            if (it is CellTypes.Header) {
                it.consent.is_expanded = false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsentViewHolder {
        return when (viewType) {
            0 -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.consent_tab_head, parent, false)
                ConsentHeaderViewHolder(view)
            }
            1 -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.consent_tab_info, parent, false)
                ConsentInfoViewHolder(view)
            }
            else -> ConsentViewHolder(View(parent.context))
        }
    }

    override fun onBindViewHolder(holder: ConsentViewHolder, position: Int) {
        when (holder) {
            is ConsentHeaderViewHolder -> {
                val data = cellList[position] as CellTypes.Header
                holder.setContent(data.consent)
                holder.onClick = {
                    val isExpanded = it.is_expanded
                    //collapseAll()
                    it.is_expanded = !isExpanded
                    updateList(consentList, language)
                }
                holder.onChange = {
                    notifyDataSetChanged()
                }
            }
            is ConsentInfoViewHolder -> {
                val data = cellList[position] as CellTypes.Info
                holder.setContent(data.consent, language)
                holder.onChange = {
                    notifyDataSetChanged()
                }
                holder.onShowFullDescription = {
                    this.onShowFullDescription?.invoke(it)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return cellList.size
    }
}

open class ConsentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

class ConsentHeaderViewHolder(itemView: View) : ConsentViewHolder(itemView) {
    private val iconImage: ImageView = itemView.findViewById(R.id.icon_image)
    private val titleText: TextView = itemView.findViewById(R.id.title_text)
    private val arrowIcon: ImageView = itemView.findViewById(R.id.arrow_icon)

    private var consent: ConsentOption? = null

    var onClick: ((ConsentOption) -> Unit)? = null
    var onChange: ((ConsentOption) -> Unit)? = null

    fun setContent(consent: ConsentOption?) {
        this.consent = consent

        titleText.text = consent?.title

        val iconRes = when (consent?.is_allow) {
            true -> R.mipmap.accept_icon
            else -> R.mipmap.denied_icon
        }
        iconImage.setImageResource(iconRes)

        val arrowIconRes = when (consent?.is_expanded) {
            true -> R.mipmap.arrow_up
            else -> R.mipmap.arrow_down
        }
        arrowIcon.setImageResource(arrowIconRes)

        itemView.setOnClickListener {
            this.consent?.let {
                onClick?.invoke(it)
            }
        }


    }
}

class ConsentInfoViewHolder(itemView: View) : ConsentViewHolder(itemView) {
    private val infoText: TextView = itemView.findViewById(R.id.info_text)
    private val switch: SwitchCompat = itemView.findViewById(R.id.switch_btn)
    private val requireLabel: TextView = itemView.findViewById(R.id.require_label)
    private val fullVersionBtn: View = itemView.findViewById(R.id.full_version_btn)

    private var consent: ConsentOption? = null

    var onShowFullDescription: ((String)->Unit)? = null

    var onChange: ((ConsentOption) -> Unit)? = null
    fun setContent(consent: ConsentOption?, language: String) {
        this.consent = consent
        val htmlStr = when (language) {
            "th" -> consent?.brief_description?.th
            else -> consent?.brief_description?.en
        }
        switch.isChecked = consent?.is_allow ?: true
        switch.setOnClickListener {
            if (consent?.require == true) {
                switch.isChecked = true
                return@setOnClickListener
            }
            consent?.let {
                it.is_allow = switch.isChecked
                onChange?.invoke(it)
            }
        }

        htmlStr?.let {
            infoText.text = Html.fromHtml(it, Html.FROM_HTML_MODE_COMPACT)
        }

        requireLabel.visibility = when (consent?.require) {
            true -> View.VISIBLE
            else -> View.INVISIBLE
        }

        if(consent?.is_full_description_enabled == true){
            fullVersionBtn.visibility = View.VISIBLE
            fullVersionBtn.setOnClickListener{
                when (language) {
                    "th" -> consent.full_description?.th
                    else -> consent.full_description?.en
                }?.let{ str ->
                    onShowFullDescription?.invoke(str)
                }
            }
        }else{
            fullVersionBtn.visibility = View.GONE
        }
    }
}

class PDPADiffCallback(
    private val oldList: List<ConsentOptionListAdapter.CellTypes>,
    private val newList: List<ConsentOptionListAdapter.CellTypes>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val theOld = oldList[oldItemPosition]
        val theNew = newList[newItemPosition]

        return (theOld is ConsentOptionListAdapter.CellTypes.Header &&
                theNew is ConsentOptionListAdapter.CellTypes.Header &&
                theOld.consent.title == theNew.consent.title
                )
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val theOld = oldList[oldItemPosition]
        val theNew = newList[newItemPosition]

        return (theOld is ConsentOptionListAdapter.CellTypes.Header &&
                theNew is ConsentOptionListAdapter.CellTypes.Header &&
                theOld.consent.title == theNew.consent.title &&
                theOld.consent.is_expanded == theNew.consent.is_expanded
                )
    }

}
