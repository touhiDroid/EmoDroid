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

import android.content.Context;
import android.content.SharedPreferences;

import com.touhidroid.library.emo.Emo;

import java.util.ArrayList;
import java.util.StringTokenizer;


/**
 * @author Daniele Ricci
 */
public class EmoRecentManager extends ArrayList<Emo> {

    private static final String PREFERENCE_NAME = "emojicon";
    private static final String PREF_RECENTS = "recent_emojis";
    private static final String PREF_PAGE = "recent_page";

    private static final Object LOCK = new Object();
    private static EmoRecentManager sInstance;

    private Context mContext;

    private EmoRecentManager(Context context) {
        mContext = context.getApplicationContext();
        loadRecents();
    }

    public static EmoRecentManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                if (sInstance == null) {
                    sInstance = new EmoRecentManager(context);
                }
            }
        }
        return sInstance;
    }

    public int getRecentPage() {
        return getPreferences().getInt(PREF_PAGE, 0);
    }

    public void setRecentPage(int page) {
        getPreferences().edit().putInt(PREF_PAGE, page).apply();
    }

    public void push(Emo object) {
        // FIXME totally inefficient way of adding the emoji to the adapter
        // TODO this should be probably replaced by a deque
        if (contains(object)) {
            super.remove(object);
        }
        add(0, object);
    }

    @Override
    public boolean add(Emo object) {
        return super.add(object);
    }

    @Override
    public void add(int index, Emo object) {
        super.add(index, object);
    }

    @Override
    public boolean remove(Object object) {
        return super.remove(object);
    }

    private SharedPreferences getPreferences() {
        return mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    private void loadRecents() {
        SharedPreferences prefs = getPreferences();
        String str = prefs.getString(PREF_RECENTS, "");
        StringTokenizer tokenizer = new StringTokenizer(str, "~");
        while (tokenizer.hasMoreTokens()) {
            try {
                add(new Emo(tokenizer.nextToken()));
            } catch (NumberFormatException e) {
                // ignored
            }
        }
    }

    public void saveRecents() {
        StringBuilder str = new StringBuilder();
        int c = size();
        for (int i = 0; i < c; i++) {
            Emo e = get(i);
            str.append(e.getEmo());
            if (i < (c - 1)) {
                str.append('~');
            }
        }
        SharedPreferences prefs = getPreferences();
        prefs.edit().putString(PREF_RECENTS, str.toString()).apply();
    }

}
