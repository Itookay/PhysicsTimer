package itookay.android.org.setting
import android.os.Bundle
import android.widget.ListView
import androidx.fragment.app.ListFragment

class FontListFragment : ListFragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val adapter = FontListAdapter(activity, resources)
        listAdapter = adapter
        listView.choiceMode = ListView.CHOICE_MODE_SINGLE
        listView.setSelection(Settings.getFontIndex(context))
    }
}
