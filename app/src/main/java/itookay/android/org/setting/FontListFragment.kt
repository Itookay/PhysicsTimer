package itookay.android.org.setting
import android.R
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.ListFragment

class FontListFragment : ListFragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val adapter = FontListAdapter(activity, resources)
        listAdapter = adapter
        listView.choiceMode = ListView.CHOICE_MODE_SINGLE
        listView.setSelection(Settings.getSavedFontIndex(context))
    }
}
