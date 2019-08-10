package itookay.android.org.setting

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.ListFragment

class RingtoneSettingFragment : ListFragment(), AdapterView.OnItemClickListener {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val adapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_single_choice, RingtoneList.getRingtoneList(context))
        listAdapter = adapter
        listView.choiceMode = ListView.CHOICE_MODE_SINGLE
        listView.setOnItemClickListener(this)
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    }
}