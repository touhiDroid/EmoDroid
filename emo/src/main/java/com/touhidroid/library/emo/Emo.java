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

package com.touhidroid.library.emo;

import java.io.Serializable;

/**
 * @author Hieu Rocker (rockerhieu@gmail.com)
 */
public class Emo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String emo;

    private Emo() {
    }

    public static Emo fromCodePoint(int codePoint) {
        Emo emoji = new Emo();
        emoji.emo = newString(codePoint);
        return emoji;
    }

    public static Emo fromChar(char ch) {
        Emo emo = new Emo();
        emo.emo = Character.toString(ch);
        return emo;
    }

    public static Emo fromChars(String chars) {
        Emo emo = new Emo();
        emo.emo = chars;
        return emo;
    }

    public Emo(String emo) {
        this.emo = emo;
    }

    public String getEmo() {
        return emo;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Emo && emo.equals(((Emo) o).emo);
    }

    @Override
    public int hashCode() {
        return emo.hashCode();
    }

    public static final String newString(int codePoint) {
        if (Character.charCount(codePoint) == 1) {
            return String.valueOf(codePoint);
        } else {
            return new String(Character.toChars(codePoint));
        }
    }
}
