package com.cjx.bean;

/**
 * Created by CJX on 2016/8/6.
 */
public class StudyItem {

    private int icon;
    private String function;
    //若需要跳转则显示这个图标
    private int whetherTurn;

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public int getWhetherTurn() {
        return whetherTurn;
    }

    public void setWhetherTurn(int whetherTurn) {
        this.whetherTurn = whetherTurn;
    }
}
