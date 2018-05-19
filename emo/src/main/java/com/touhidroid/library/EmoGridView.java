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

package com.touhidroid.library;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;

import com.touhidroid.library.emo.Emo;
import com.touhidroid.library.emo.People;

import java.util.Arrays;

/**
 * @author Hieu Rocker (rockerhieu@gmail.com)
 * @author Ankush Sachdeva (sankush@yahoo.co.in)
 */
public class EmoGridView {
    public View rootView;
    EmoPopup mEmojiconPopup;
    EmoRecent mRecents;
    Emo[] mData;

    public EmoGridView(Context context, Emo[] emojicons, EmoRecent recents, EmoPopup emojiconPopup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        mEmojiconPopup = emojiconPopup;
        rootView = inflater.inflate(R.layout.emo_grid, null);
        setRecents(recents);
        GridView gridView = (GridView) rootView.findViewById(R.id.Emoji_GridView);
        if (emojicons == null) {
            mData = People.DATA;
        } else {
            Object[] o = (Object[]) emojicons;
            mData = Arrays.asList(o).toArray(new Emo[o.length]);
        }
        EmoAdapter mAdapter = new EmoAdapter(rootView.getContext(), mData);
        mAdapter.setEmoClickListener(new OnEmoClickListener() {

            @Override
            public void onEmoClicked(Emo emojicon) {
                if (mEmojiconPopup.onEmoClickListener != null) {
                    mEmojiconPopup.onEmoClickListener.onEmoClicked(emojicon);
                }
                if (mRecents != null) {
                    mRecents.addRecentEmo(rootView.getContext(), emojicon);
                }
            }
        });
        gridView.setAdapter(mAdapter);
    }

    private void setRecents(EmoRecent recents) {
        mRecents = recents;
    }

    public interface OnEmoClickListener {
        void onEmoClicked(Emo emojicon);
    }

}
