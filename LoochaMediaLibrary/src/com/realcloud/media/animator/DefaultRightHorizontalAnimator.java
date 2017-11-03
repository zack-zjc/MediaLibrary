package com.realcloud.media.animator;

import com.realcloud.media.R;

import me.yokeyword.fragmentation.anim.DefaultHorizontalAnimator;

/**
 * Created by zack on 2017/10/9.
 */

public class DefaultRightHorizontalAnimator extends DefaultHorizontalAnimator {

    public DefaultRightHorizontalAnimator(){
        this.exit = R.anim.fragment_exit;
        this.enter = R.anim.fragment_enter;
        this.popExit = R.anim.fragment_pop_enter;
        this.popEnter = R.anim.fragment_pop_exit;
    }
}
