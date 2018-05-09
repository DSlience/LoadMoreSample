package com.adealink.library.view;

import com.adealink.library.R;

/**
 * 列表加载更多UI的样式配置
 */
public class LoadMoreStyleConfig {

    private int bgColor;
    private int textColor;
    private String tipError;
    private String tipLoading;
    private String tipEnd;

    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public String getTipError() {
        return tipError;
    }

    public void setTipError(String tipError) {
        this.tipError = tipError;
    }

    public String getTipLoading() {
        return tipLoading;
    }

    public void setTipLoading(String tipLoading) {
        this.tipLoading = tipLoading;
    }

    public String getTipEnd() {
        return tipEnd;
    }

    public void setTipEnd(String tipEnd) {
        this.tipEnd = tipEnd;
    }

    public LoadMoreStyleConfig(Build build) {
        this.bgColor = build.bgColor;
        this.textColor = build.textColor;
        this.tipError = build.tipError;
        this.tipLoading = build.tipLoading;
        this.tipEnd = build.tipEnd;
    }

    public static class Build {
        //赋上默认值
        private int bgColor = android.R.color.transparent;
        private int textColor = R.color.recycler_load_more;
        private String tipError = "加载失败，点击重试";
        private String tipLoading = "加载中...";
        private String tipEnd = "没有更多了";

        public Build setBgColor(int bgColor) {
            this.bgColor = bgColor;
            return this;
        }

        public Build setTextColor(int textColor) {
            this.textColor = textColor;
            return this;
        }

        public Build setTipError(String tipError) {
            this.tipError = tipError;
            return this;
        }

        public Build setTipLoading(String tipLoading) {
            this.tipLoading = tipLoading;
            return this;
        }

        public Build setTipEnd(String tipEnd) {
            this.tipEnd = tipEnd;
            return this;
        }

        public LoadMoreStyleConfig build() {
            return new LoadMoreStyleConfig(this);
        }
    }

}