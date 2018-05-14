package org.trustnote.wallet.widget

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import org.trustnote.wallet.R

class MyDialogFragment : DialogFragment() {
    internal var mNum: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mNum = arguments.getInt("num")

        // Pick a style based on the num.
        var style = DialogFragment.STYLE_NORMAL
        var theme = 0
        when ((mNum - 1) % 6) {
            1 -> style = DialogFragment.STYLE_NO_TITLE
            2 -> style = DialogFragment.STYLE_NO_FRAME
            3 -> style = DialogFragment.STYLE_NO_INPUT
            4 -> style = DialogFragment.STYLE_NORMAL
            5 -> style = DialogFragment.STYLE_NORMAL
            6 -> style = DialogFragment.STYLE_NO_TITLE
            7 -> style = DialogFragment.STYLE_NO_FRAME
            8 -> style = DialogFragment.STYLE_NORMAL
        }
        when ((mNum - 1) % 6) {
            4 -> theme = android.R.style.Theme_Holo
            5 -> theme = android.R.style.Theme_Holo_Light_Dialog
            6 -> theme = android.R.style.Theme_Holo_Light
            7 -> theme = android.R.style.Theme_Holo_Light_Panel
            8 -> theme = android.R.style.Theme_Holo_Light
        }
        setStyle(style, theme)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
//        View tv = v.findViewById(R.id.text);
        //        ((TextView)tv).setText("Dialog #" + mNum + ": using style "
        //                + getNameForNum(mNum));
        //
        //        // Watch for button clicks.
        //        Button button = (Button)v.findViewById(R.id.show);
        //        button.setOnClickListener(new OnClickListener() {
        //            public void onClick(View v) {
        //                // When button is clicked, call up to owning activity.
        //                ((FragmentDialog)getActivity()).showDialog();
        //            }
        //        });

        return inflater!!.inflate(R.layout.l_dialog, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : Dialog(activity, theme) {
            override fun onBackPressed() {
                dismiss()
            }
        }
    }

    companion object {

        /**
         * Create a new instance of MyDialogFragment, providing "num"
         * as an argument.
         */
        internal fun newInstance(num: Int): MyDialogFragment {
            val f = MyDialogFragment()

            // Supply num input as an argument.
            val args = Bundle()
            args.putInt("num", num)
            f.arguments = args

            return f
        }
    }
}