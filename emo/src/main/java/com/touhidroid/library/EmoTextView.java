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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * @author Hieu Rocker (rockerhieu@gmail.com).
 */
@SuppressLint("AppCompatCustomView")
public class EmoTextView extends TextView {
    private int mEmojiconSize;
    private int mTextStart = 0;
    private int mTextLength = -1;

    public EmoTextView(Context context) {
        super(context);
        init(null);
    }

    public EmoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public EmoTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs == null) {
            mEmojiconSize = (int) getTextSize();
        } else {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.Emo);
            mEmojiconSize = (int) a.getDimension(R.styleable.Emo_emoSize, getTextSize());
            mTextStart = a.getInteger(R.styleable.Emo_emoTextStart, 0);
            mTextLength = a.getInteger(R.styleable.Emo_emoTextLength, -1);
            a.recycle();
        }
        setText(getText());
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        EmoHandler.addEmojis(getContext(), builder, mEmojiconSize, mTextStart, mTextLength);
        super.setText(builder, type);
    }

    /**
     * Set the size of emojicon in pixels.
     */
    public void setEmojiconSize(int pixels) {
        mEmojiconSize = pixels;
    }
}
