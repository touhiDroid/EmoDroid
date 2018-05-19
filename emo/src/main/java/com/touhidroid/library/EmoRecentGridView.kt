/*
 * Copyright 2014 Ankush Sachdeva
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.touhidroid.library

import android.content.Context
import android.widget.GridView

import com.touhidroid.library.emo.Emo

/**
 * @author Touhid
 * @author Daniele Ricci
 * @author Ankush Sachdeva (sankush@yahoo.co.in)
 */
class EmoRecentGridView(context: Context, emojicons: Array<Emo>,
                        recents: EmoRecent, emoPopup: EmoPopup) : EmoGridView(context, emojicons, recents, emoPopup), EmoRecent {
    private var mAdapter: EmoAdapter? = null

    init {
        val recent1 = EmoRecentManager.getInstance(rootView.context)
        mAdapter = EmoAdapter(rootView.context, recent1)
        mAdapter!!.setEmoClickListener { emo ->
            if (mEmojiconPopup.onEmoClickListener != null) {
                mEmojiconPopup.onEmoClickListener.onEmoClicked(emo)
            }
        }
        val gridView = rootView.findViewById<GridView>(R.id.Emoji_GridView)
        gridView.adapter = mAdapter
    }

    override fun addRecentEmo(context: Context, emojicon: Emo) {
        val recentEmo = EmoRecentManager
                .getInstance(context)
        recentEmo.push(emojicon)

        // notify dataset changed
        if (mAdapter != null)
            mAdapter!!.notifyDataSetChanged()
    }

}
