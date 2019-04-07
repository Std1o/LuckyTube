package com.akhilpatoliya.videoplayerdemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Rect;
import android.support.v4.view.VelocityTrackerCompat;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * Created by Ken Lin on 2015-03-25.
 */
public class SwipeToDeleteListener implements View.OnTouchListener {

    private float historicX = Float.NaN, historicY = Float.NaN;
    private int mMinFlingVelocity;
    private int mMaxFlingVelocity;
    private long mShortAnimationTime;


    private PendingRow mPendingRow;
    private int mSlop;
    private int mSwipingSlop;
    private boolean mSwiping;
    private SwipeCallbacks mCallbacks;
    private ListView mListView;
    private Container mChild;
    private VelocityTracker mVelocityTracker;
    private int mViewWidth;
    private int mDownPosition;
    private boolean mPaused;


    public SwipeToDeleteListener(ListView listview, SwipeCallbacks callbacks) {
        ViewConfiguration vc = ViewConfiguration.get(listview.getContext());
        mSlop = vc.getScaledTouchSlop();
        mMinFlingVelocity = vc.getScaledMinimumFlingVelocity() * 16;
        mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();
        mShortAnimationTime = listview.getContext().getResources().getInteger(
                android.R.integer.config_shortAnimTime);
        mListView = listview;
        mCallbacks = callbacks;
    }

    public class Container {
        final View container;
        final View frontView;
        final View behindView;
        Boolean isFrontViewDismissed;

        public Container(ViewGroup container) {

            this.container = container;
            frontView = container.getChildAt(0);
            behindView = container.getChildAt(1);
            isFrontViewDismissed = false;
        }

        public View getCurrentView() {
            if (isFrontViewDismissed) {
                return behindView;
            } else {
                return frontView;
            }
        }
    }

    public class PendingRow {
        public int position;
        public Container container;
        public SwipeCallbacks swipeCallbacks;
        public boolean isSwipingRight;

        public PendingRow(int position, Container container, SwipeCallbacks swipeCallbacks, boolean isSwipingRight) {
            this.position = position;
            this.container = container;
            this.swipeCallbacks = swipeCallbacks;
            this.isSwipingRight = isSwipingRight;
        }
    }

    public interface SwipeCallbacks {
        void onSwipe(int position);

        void onOpen(View view, int position);

        void onClose(View view, int position);

        void onDelete(View view, int position);
    }

    public void makeScrollListener() {
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                setEnabled(scrollState != AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
                cancelPendingRow(null, 0, false);

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
    }

    public void setEnabled(boolean enabled) {
        mPaused = !enabled;
    }


    @Override
    public boolean onTouch(View v, MotionEvent ev) {
        if (mViewWidth < 2) {
            mViewWidth = mListView.getWidth();
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                if (mPaused) {
                    return false;
                }

                Rect rect = new Rect();
                int[] listViewCoords = new int[2];
                mListView.getLocationOnScreen(listViewCoords);
                int x = (int) ev.getRawX() - listViewCoords[0];
                int y = (int) ev.getRawY() - listViewCoords[1];

                int childCount = mListView.getChildCount();
                View child;
                for (int i = 0; i < childCount; i++) {
                    child = mListView.getChildAt(i);
                    child.getHitRect(rect);
                    if (rect.contains(x, y)) {
                        mChild = new Container((ViewGroup) child);
                        break;
                    }
                }

                if (mChild != null) {
                    mDownPosition = mListView.getPositionForView(mChild.container);
                    mChild.isFrontViewDismissed = isPendingViewShowing() && mPendingRow.position == mDownPosition;
                    historicX = ev.getRawX();
                    historicY = ev.getRawY();
                    mVelocityTracker = VelocityTracker.obtain();
                    mVelocityTracker.addMovement(ev);
                }
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                if (mVelocityTracker == null || mPaused) {
                    break;
                }

                mVelocityTracker.addMovement(ev);
                float deltaX = ev.getRawX() - historicX;
                float deltaY = ev.getRawY() - historicY;
                if (Math.abs(deltaX) > mSlop && Math.abs(deltaY) < Math.abs(deltaX) / 2) {
                    mSwiping = true;
                    mSwipingSlop = (deltaX > 0 ? mSlop : -mSlop);
                    mListView.requestDisallowInterceptTouchEvent(true);
                }

                if (mSwiping) {
                    if (isPendingViewShowing() && mPendingRow.position != mDownPosition) {
                        mPendingRow.container.behindView.setAlpha(Math.max(0f, Math.min(1f,
                                1f - 1f * Math.abs(deltaX) / mViewWidth)));
                    }
                    mChild.getCurrentView().setTranslationX(deltaX - mSwipingSlop);
                    mCallbacks.onSwipe(mDownPosition);
                    return true;
                }
                break;
            }

            case MotionEvent.ACTION_UP: {
                if (mVelocityTracker == null || mPaused) {
                    break;
                }
                float deltaX = ev.getRawX() - historicX;
                int index = ev.getActionIndex();
                int pointerId = ev.getPointerId(index);
                mVelocityTracker.addMovement(ev);
                mVelocityTracker.computeCurrentVelocity(1000);
                float velocityX = VelocityTrackerCompat.getXVelocity(mVelocityTracker, pointerId);
                float velocityY = VelocityTrackerCompat.getYVelocity(mVelocityTracker, pointerId);
                float absVelocityX = Math.abs(velocityX);
                float absVelocityY = Math.abs(velocityY);
                long customAnimationTime = 0;
                boolean isSwiping = false;
                boolean isSwipingRight = false;
                if (Math.abs(deltaX) > mViewWidth / 2 && mSwiping) {
                    isSwiping = true;
                    customAnimationTime = mShortAnimationTime;
                    if (deltaX > 0) {
                        isSwipingRight = true;
                    }
                } else if (mMinFlingVelocity <= absVelocityX && absVelocityX <= mMaxFlingVelocity && absVelocityY < absVelocityX && mSwiping) {
                    if ((velocityX < 0) == (deltaX < 0)) {
                        isSwiping = true;
                        customAnimationTime = (long) ((mViewWidth - Math.abs(deltaX)) * 1000 / absVelocityX);
                    }
                    if (mVelocityTracker.getXVelocity() > 0) {
                        isSwipingRight = true;
                    }
                }

                if (isSwiping && mDownPosition != ListView.INVALID_POSITION) {
                    final Container child = mChild;
                    final int swipingPosition = mDownPosition;
                    final boolean isRight = isSwipingRight;

                    mChild.getCurrentView()
                            .animate()
                            .translationX(isRight ? mViewWidth : -mViewWidth)
                            .setDuration(customAnimationTime)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    setEnabled(false);
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    if (mPendingRow != null && mPendingRow.position != mDownPosition) {
                                        cancelPendingRow(child, swipingPosition, isRight);
                                    } else {
                                        switchViews(child, swipingPosition, isRight);
                                    }
                                    setEnabled(true);
                                }
                            });
                    /* successful swipe */

                } else {
                    if (isPendingViewShowing() && mPendingRow.position != mDownPosition) {
                        mPendingRow.container.getCurrentView().animate()
                                .alpha(1)
                                .setDuration(mShortAnimationTime)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {
                                        setEnabled(false);
                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        setEnabled(true);
                                    }
                                });
                    }

                    mChild.getCurrentView().animate()
                            .translationX(0)
                            .alpha(1)
                            .setDuration(mShortAnimationTime)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    setEnabled(false);
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    setEnabled(true);
                                }
                            });
                    /* not a valid swipe */
                }
                mVelocityTracker.recycle();
                mVelocityTracker = null;
                historicX = 0;
                historicY = 0;
                mChild = null;
                mDownPosition = ListView.INVALID_POSITION;
                mSwiping = false;
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                if (mVelocityTracker == null) {
                    break;
                }

                if (mChild != null && mSwiping) {
                    mChild.getCurrentView().animate()
                            .translationX(0)
                            .alpha(1)
                            .setDuration(mShortAnimationTime)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    if (isPendingViewShowing() && mPendingRow.position != mDownPosition) {
                                        mPendingRow.container.getCurrentView().animate()
                                                .alpha(1)
                                                .setDuration(mShortAnimationTime)
                                                .setListener(new AnimatorListenerAdapter() {
                                                    @Override
                                                    public void onAnimationStart(Animator animation) {
                                                        setEnabled(false);
                                                    }

                                                    @Override
                                                    public void onAnimationEnd(Animator animation) {
                                                        setEnabled(true);
                                                    }
                                                });
                                    }
                                    /* cancel the pending row if swiping different row */
                                }
                            });
                    /* cancel current row */
                }
                mVelocityTracker.recycle();
                mVelocityTracker = null;
                historicX = 0;
                historicY = 0;
                mChild = null;
                mDownPosition = ListView.INVALID_POSITION;
                mSwiping = false;
            }
            default:
                return false;
        }
        return false;
    }

    private boolean isPendingViewShowing() {
        return mPendingRow != null && mPendingRow.container.isFrontViewDismissed;
    }

    public void switchViews(final Container container, final int position, final boolean isSwipingRight) {
        if (container.isFrontViewDismissed) {
            container.getCurrentView().findViewById(R.id.txt_cancel).setVisibility(View.INVISIBLE);
            container.getCurrentView().findViewById(R.id.txt_delete).setVisibility(View.INVISIBLE);
            container.getCurrentView().setVisibility(View.GONE);
            container.getCurrentView().findViewById(R.id.txt_cancel).setAlpha(0);
            container.getCurrentView().findViewById(R.id.txt_delete).setAlpha(0);
            container.getCurrentView().setTranslationX(0);
            container.getCurrentView().setAlpha(1);

            container.frontView.animate()
                    .translationX(0)
                    .alpha(1)
                    .setDuration(mShortAnimationTime)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            setEnabled(false);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            setEnabled(true);
                        }
                    });
            container.isFrontViewDismissed = false;
            mPendingRow = null;
            mCallbacks.onClose(container.frontView, position);
        } else {
            container.behindView.setVisibility(View.VISIBLE);
            container.behindView.findViewById(R.id.txt_cancel).setVisibility(View.VISIBLE);
            container.behindView.findViewById(R.id.txt_delete).setVisibility(View.VISIBLE);

            final AlphaAnimation aAnimator = new AlphaAnimation(0.0f, 1.0f);
            aAnimator.setDuration((long) 0.3 * mShortAnimationTime);
            aAnimator.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    container.behindView.animate()
                            .translationX(0)
                            .alpha(1)
                            .setDuration(mShortAnimationTime)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    setEnabled(false);
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    setEnabled(true);
                                }
                            });
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    container.isFrontViewDismissed = true;
                    mPendingRow = new PendingRow(position, container, mCallbacks, isSwipingRight);
                    mPendingRow.swipeCallbacks.onOpen(container.behindView, position);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            container.behindView.startAnimation(aAnimator);
        }
    }

    public void cancelPendingRow(final Container container, final int position, final boolean isSwipingRight) {
        if (isPendingViewShowing()) {
            mPendingRow.container.getCurrentView().findViewById(R.id.txt_cancel).setVisibility(View.INVISIBLE);
            mPendingRow.container.getCurrentView().findViewById(R.id.txt_delete).setVisibility(View.INVISIBLE);
            mPendingRow.container.getCurrentView().findViewById(R.id.txt_cancel).setAlpha(0);
            mPendingRow.container.getCurrentView().findViewById(R.id.txt_delete).setAlpha(0);
            mPendingRow.container.getCurrentView().setVisibility(View.GONE);
            mPendingRow.container.getCurrentView().setTranslationX(0);
            mPendingRow.container.getCurrentView().setAlpha(1);

            mPendingRow.container.frontView.animate()
                    .translationX(0)
                    .alpha(1)
                    .setDuration(mShortAnimationTime)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            setEnabled(false);
                            mPendingRow = null;
                            if (container != null) {
                                switchViews(container, position, isSwipingRight);
                            }
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            setEnabled(true);
                        }
                    });
        }
    }

    public void deletePendingRow(final int position) {
        if (isPendingViewShowing()) {
            final PendingRow tmpPendingRow = mPendingRow;
            mPendingRow = null;

            final ViewGroup.LayoutParams lp = tmpPendingRow.container.container.getLayoutParams();
            final int originalHeight = tmpPendingRow.container.container.getHeight();
            ValueAnimator animator = ValueAnimator.ofInt(originalHeight, 1).setDuration(mShortAnimationTime);

            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    tmpPendingRow.swipeCallbacks.onDelete(tmpPendingRow.container.frontView, position);
                    tmpPendingRow.container.frontView.post(new Runnable() {
                        @Override
                        public void run() {
                            tmpPendingRow.container.frontView.setTranslationX(0);
                            tmpPendingRow.container.frontView.setAlpha(1);
                            tmpPendingRow.container.behindView.setVisibility(View.GONE);
                            tmpPendingRow.container.behindView.findViewById(R.id.txt_cancel).setVisibility(View.INVISIBLE);
                            tmpPendingRow.container.behindView.findViewById(R.id.txt_delete).setVisibility(View.INVISIBLE);
                            tmpPendingRow.container.behindView.setTranslationX(0);
                            tmpPendingRow.container.behindView.setAlpha(1);
                            tmpPendingRow.container.behindView.findViewById(R.id.txt_cancel).setAlpha(0);
                            tmpPendingRow.container.behindView.findViewById(R.id.txt_delete).setAlpha(0);
                            lp.height = originalHeight;
                            tmpPendingRow.container.container.setLayoutParams(lp);
                        }
                    });
                }
            });
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    lp.height = (Integer) valueAnimator.getAnimatedValue();
                    tmpPendingRow.container.container.setLayoutParams(lp);
                }
            });
            animator.start();
            /* animate delete row effect */
        }
    }
}
