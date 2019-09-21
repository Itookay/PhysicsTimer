package itookay.android.org.setting;

import android.R
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.ListFragment

class VibrationListFragment : ListFragment(), AdapterView.OnItemClickListener  {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        listAdapter = ArrayAdapter<String>(context, R.layout.simple_list_item_single_choice, VibrationList.getVibrationNameList(context))
        listView.choiceMode = ListView.CHOICE_MODE_SINGLE
        listView.onItemClickListener = this
        val index = Settings.getVibrationIndex(context)
        listView.setItemChecked(index, true)
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        Settings.saveVibration(context, position)

        //振動
        VibrationList.vibrate(context, position, VibrationList.NOT_REPEAT);
    }

    override fun onDestroy() {
        super.onDestroy()

        VibrationList.stop()
    }
}
