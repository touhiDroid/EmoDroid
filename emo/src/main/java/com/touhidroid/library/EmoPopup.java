package com.touhidroid.library;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.PopupWindow;

import com.touhidroid.library.emo.Emo;
import com.touhidroid.library.emo.Nature;
import com.touhidroid.library.emo.Objects;
import com.touhidroid.library.emo.People;
import com.touhidroid.library.emo.Places;
import com.touhidroid.library.emo.Symbols;

import java.util.Arrays;
import java.util.List;

public class EmoPopup extends PopupWindow implements ViewPager.OnPageChangeListener, EmoRecent {
    EmoGridView.OnEmoClickListener onEmoClickListener;
    OnEmoBackspaceClickListener onEmoBackspaceClickListener;
    OnSoftKeyboardOpenCloseListener onSoftKeyboardOpenCloseListener;
    View rootView;
    Context mContext;
    private int mEmoTabLastSelectedIndex = -1;
    private View[] mEmoTabs;
    private PagerAdapter mEmoAdapter;
    private EmoRecentManager mRecentManager;
    private int keyBoardHeight = 0;
    private Boolean pendingOpen = false;
    private Boolean isOpened = false;
    private ViewPager emoPager;

    /**
     * Constructor
     *
     * @param rootView The top most layout in your view hierarchy. The difference of this view and the screen height will be used to calculate the keyboard height.
     * @param mContext The context of current activity.
     */
    public EmoPopup(View rootView, Context mContext) {
        super(mContext);
        this.mContext = mContext;
        this.rootView = rootView;
        View customView = createCustomView();
        setContentView(customView);
        setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        //default size
        setSize((int) mContext.getResources().getDimension(R.dimen.keyboard_height), LayoutParams.MATCH_PARENT);
    }

    /**
     * Set the listener for the event of keyboard opening or closing.
     */
    public void setOnSoftKeyboardOpenCloseListener(OnSoftKeyboardOpenCloseListener listener) {
        this.onSoftKeyboardOpenCloseListener = listener;
    }

    /**
     * Set the listener for the event when any of the emoticon is clicked
     */
    public void setOnEmoClickListener(EmoGridView.OnEmoClickListener listener) {
        this.onEmoClickListener = listener;
    }

    /**
     * Set the listener for the event when backspace on emojicon popup is clicked
     */
    public void setOnEmoBackspaceClickListener(OnEmoBackspaceClickListener listener) {
        this.onEmoBackspaceClickListener = listener;
    }

    /**
     * Use this function to show the emoji popup.
     * NOTE: Since, the soft keyboard sizes are variable on different android devices, the
     * library needs you to open the soft keyboard atleast once before calling this function.
     * If that is not possible see showAtBottomPending() function.
     */
    public void showAtBottom() {
        showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
    }

    /**
     * Use this function when the soft keyboard has not been opened yet. This
     * will show the emoji popup after the keyboard is up next time.
     * Generally, you will be calling InputMethodManager.showSoftInput function after
     * calling this function.
     */
    public void showAtBottomPending() {
        if (isKeyBoardOpen())
            showAtBottom();
        else
            pendingOpen = true;
    }

    /**
     * @return Returns true if the soft keyboard is open, false otherwise.
     */
    public Boolean isKeyBoardOpen() {
        return isOpened;
    }

    /**
     * Dismiss the popup
     */
    @Override
    public void dismiss() {
        super.dismiss();
        EmoRecentManager
                .getInstance(mContext).saveRecents();
    }

    /**
     * Call this function to resize the emoji popup according to your soft keyboard size
     */
    public void setSizeForSoftKeyboard() {
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);

                int screenHeight = getUsableScreenHeight();
                int heightDifference = screenHeight
                        - (r.bottom - r.top);
                int resourceId = mContext.getResources()
                        .getIdentifier("status_bar_height",
                                "dimen", "android");
                if (resourceId > 0) {
                    heightDifference -= mContext.getResources()
                            .getDimensionPixelSize(resourceId);
                }
                if (heightDifference > 100) {
                    keyBoardHeight = heightDifference;
                    setSize(LayoutParams.MATCH_PARENT, keyBoardHeight);
                    if (!isOpened) {
                        if (onSoftKeyboardOpenCloseListener != null)
                            onSoftKeyboardOpenCloseListener.onKeyboardOpen(keyBoardHeight);
                    }
                    isOpened = true;
                    if (pendingOpen) {
                        showAtBottom();
                        pendingOpen = false;
                    }
                } else {
                    isOpened = false;
                    if (onSoftKeyboardOpenCloseListener != null)
                        onSoftKeyboardOpenCloseListener.onKeyboardClose();
                }
            }
        });
    }

    private int getUsableScreenHeight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics metrics = new DisplayMetrics();

            WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            if (windowManager != null)
                windowManager.getDefaultDisplay().getMetrics(metrics);

            return metrics.heightPixels;

        } else {
            return rootView.getRootView().getHeight();
        }
    }

    /**
     * Manually set the popup window size
     *
     * @param width  Width of the popup
     * @param height Height of the popup
     */
    public void setSize(int width, int height) {
        setWidth(width);
        setHeight(height);
    }

    private View createCustomView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.emo, null, false);
        emoPager = view.findViewById(R.id.emojis_pager);
        emoPager.setOnPageChangeListener(this);
        EmoRecent recents = this;
        mEmoAdapter = new EmoPagerAdapter(
                Arrays.asList(
                        new EmoRecentGridView(mContext, null, null, this),
                        new EmoGridView(mContext, People.DATA, recents, this),
                        new EmoGridView(mContext, Nature.DATA, recents, this),
                        new EmoGridView(mContext, Objects.DATA, recents, this),
                        new EmoGridView(mContext, Places.DATA, recents, this),
                        new EmoGridView(mContext, Symbols.DATA, recents, this)
                )
        );
        emoPager.setAdapter(mEmoAdapter);
        mEmoTabs = new View[6];
        mEmoTabs[0] = view.findViewById(R.id.emojis_tab_0_recents);
        mEmoTabs[1] = view.findViewById(R.id.emojis_tab_1_people);
        mEmoTabs[2] = view.findViewById(R.id.emojis_tab_2_nature);
        mEmoTabs[3] = view.findViewById(R.id.emojis_tab_3_objects);
        mEmoTabs[4] = view.findViewById(R.id.emojis_tab_4_cars);
        mEmoTabs[5] = view.findViewById(R.id.emojis_tab_5_punctuation);
        for (int i = 0; i < mEmoTabs.length; i++) {
            final int position = i;
            mEmoTabs[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    emoPager.setCurrentItem(position);
                }
            });
        }
        view.findViewById(R.id.emojis_backspace).setOnTouchListener(new RepeatListener(1000, 50, new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (onEmoBackspaceClickListener != null)
                    onEmoBackspaceClickListener.onEmoBackspaceClicked(v);
            }
        }));

        // get last selected page
        mRecentManager = EmoRecentManager.getInstance(view.getContext());
        int page = mRecentManager.getRecentPage();
        // last page was recents, check if there are recents to use
        // if none was found, go to page 1
        if (page == 0 && mRecentManager.size() == 0) {
            page = 1;
        }

        if (page == 0) {
            onPageSelected(page);
        } else {
            emoPager.setCurrentItem(page, false);
        }
        return view;
    }

    @Override
    public void addRecentEmo(@NonNull Context context, @NonNull Emo emo) {
        EmoRecentGridView fragment = ((EmoPagerAdapter) emoPager.getAdapter()).getRecentFragment();
        fragment.addRecentEmo(context, emo);
    }


    @Override
    public void onPageScrolled(int i, float v, int i2) {
    }

    @Override
    public void onPageSelected(int i) {
        if (mEmoTabLastSelectedIndex == i) {
            return;
        }
        switch (i) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                if (mEmoTabLastSelectedIndex >= 0 && mEmoTabLastSelectedIndex < mEmoTabs.length) {
                    mEmoTabs[mEmoTabLastSelectedIndex].setSelected(false);
                }
                mEmoTabs[i].setSelected(true);
                mEmoTabLastSelectedIndex = i;
                mRecentManager.setRecentPage(i);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }

    public interface OnEmoBackspaceClickListener {
        void onEmoBackspaceClicked(View v);
    }

    public interface OnSoftKeyboardOpenCloseListener {
        void onKeyboardOpen(int keyBoardHeight);

        void onKeyboardClose();
    }

    private static class EmoPagerAdapter extends PagerAdapter {
        private List<EmoGridView> views;

        public EmoPagerAdapter(List<EmoGridView> views) {
            super();
            this.views = views;
        }

        public EmoRecentGridView getRecentFragment() {
            for (EmoGridView it : views)
                if (it instanceof EmoRecentGridView)
                    return (EmoRecentGridView) it;
            return null;
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View v = views.get(position).rootView;
            container.addView(v, 0);
            return v;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object view) {
            container.removeView((View) view);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object key) {
            return key == view;
        }
    }

    /**
     * A class, that can be used as a TouchListener on any view (e.g. a Button).
     * It cyclically runs a clickListener, emulating keyboard-like behaviour. First
     * click is fired immediately, next before initialInterval, and subsequent before
     * normalInterval.
     * <p/>
     * <p>Interval is scheduled before the onClick completes, so it has to run fast.
     * If it runs slow, it does not generate skipped onClicks.
     */
    public static class RepeatListener implements View.OnTouchListener {

        private final int normalInterval;
        private final View.OnClickListener clickListener;
        private Handler handler = new Handler();
        private int initialInterval;
        private View downView;
        private Runnable handlerRunnable = new Runnable() {
            @Override
            public void run() {
                if (downView == null) {
                    return;
                }
                handler.removeCallbacksAndMessages(downView);
                handler.postAtTime(this, downView, SystemClock.uptimeMillis() + normalInterval);
                clickListener.onClick(downView);
            }
        };

        /**
         * @param initialInterval The interval before first click event
         * @param normalInterval  The interval before second and subsequent click
         *                        events
         * @param clickListener   The OnClickListener, that will be called
         *                        periodically
         */
        public RepeatListener(int initialInterval, int normalInterval, View.OnClickListener clickListener) {
            if (clickListener == null)
                throw new IllegalArgumentException("null runnable");
            if (initialInterval < 0 || normalInterval < 0)
                throw new IllegalArgumentException("negative interval");

            this.initialInterval = initialInterval;
            this.normalInterval = normalInterval;
            this.clickListener = clickListener;
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downView = view;
                    handler.removeCallbacks(handlerRunnable);
                    handler.postAtTime(handlerRunnable, downView, SystemClock.uptimeMillis() + initialInterval);
                    clickListener.onClick(view);
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_OUTSIDE:
                    handler.removeCallbacksAndMessages(downView);
                    downView = null;
                    return true;
            }
            view.performClick();
            return false;
        }
    }
}
