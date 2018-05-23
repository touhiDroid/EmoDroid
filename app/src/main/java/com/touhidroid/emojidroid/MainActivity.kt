package com.touhidroid.emojidroid

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.RelativeLayout
import com.touhidroid.library.EmoEditText
import com.touhidroid.library.EmoGridView
import com.touhidroid.library.EmoPopup

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val lv: ListView = findViewById(R.id.lv)
        val mAdapter = ArrayAdapter<String>(this, R.layout.listview_row_layout)
        lv.adapter = mAdapter
        val emojiconEditText = findViewById<EmoEditText>(R.id.emojicon_edit_text)
        val rootView = findViewById<RelativeLayout>(R.id.root_view)
        val emojiButton = findViewById<ImageView>(R.id.emoji_btn)
        val submitButton = findViewById<ImageView>(R.id.submit_btn)

        // Give the topmost view of your activity layout hierarchy. This will be used to measure soft keyboard height
        val popup = EmoPopup(rootView, this)

        //Will automatically set size according to the soft keyboard size
        popup.setSizeForSoftKeyboard()

        //If the emoji popup is dismissed, change emojiButton to smiley icon
        popup.setOnDismissListener({ changeEmoKeyboardIcon(emojiButton, R.drawable.smiley) })

        //If the text keyboard closes, also dismiss the emoji popup
        popup.setOnSoftKeyboardOpenCloseListener(object : EmoPopup.OnSoftKeyboardOpenCloseListener {
            override fun onKeyboardOpen(keyBoardHeight: Int) {
            }

            override fun onKeyboardClose() {
                if (popup.isShowing)
                    popup.dismiss()
            }
        })

        //On emo clicked, add it to edittext
        popup.setOnEmoClickListener(EmoGridView.OnEmoClickListener { emo ->
            if (emojiconEditText == null || emo == null) {
                return@OnEmoClickListener
            }

            val start = emojiconEditText.selectionStart
            val end = emojiconEditText.selectionEnd
            if (start < 0) {
                emojiconEditText.append(emo.emo)
            } else {
                emojiconEditText.text.replace(Math.min(start, end),
                        Math.max(start, end), emo.emo, 0,
                        emo.emo.length)
            }
        })

        //On backspace clicked, emulate the KEYCODE_DEL key event
        popup.setOnEmoBackspaceClickListener {
            val event = KeyEvent(
                    0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL)
            emojiconEditText!!.dispatchKeyEvent(event)
        }

        // To toggle between text keyboard and emoji keyboard keyboard(Popup)
        emojiButton.setOnClickListener {
            //If popup is not showing => emoji keyboard is not visible, we need to show it
            if (!popup.isShowing) {

                //If keyboard is visible, simply show the emoji popup
                if (popup.isKeyBoardOpen) {
                    popup.showAtBottom()
                    changeEmoKeyboardIcon(emojiButton, R.drawable.ic_action_keyboard)
                } else {
                    emojiconEditText!!.isFocusableInTouchMode = true
                    emojiconEditText.requestFocus()
                    popup.showAtBottomPending()
                    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.showSoftInput(emojiconEditText, InputMethodManager.SHOW_IMPLICIT)
                    changeEmoKeyboardIcon(emojiButton, R.drawable.ic_action_keyboard)
                }//else, open the text keyboard first and immediately after that show the emoji popup
            } else {
                popup.dismiss()
            }//If popup is showing, simply dismiss it to show the underlying text keyboard
        }

        //On submit, add the edittext text to listview and clear the edittext
        submitButton.setOnClickListener {
            val newText = emojiconEditText!!.text.toString()
            emojiconEditText.text.clear()
            mAdapter.add(newText)
            mAdapter.notifyDataSetChanged()
        }
    }

    private fun changeEmoKeyboardIcon(iconToBeChanged: ImageView, drawableResourceId: Int) {
        iconToBeChanged.setImageResource(drawableResourceId)
    }
}
