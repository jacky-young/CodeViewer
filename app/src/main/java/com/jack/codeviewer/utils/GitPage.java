package com.jack.codeviewer.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jack on 2/24/16.
 */
public class GitPage {
    private List<String> elementList;
    private String gitName;
    private String parent;
    private String homePath;
    private int levelCount;
    private boolean isEmpty;

    public GitPage() {
        gitName = "";
        parent = "";
        homePath = "";
        levelCount = 0;
        isEmpty = false;
    }

    public void setGitName(String name) {
        gitName = name;
    }

    public void initElementList() {
        elementList = new ArrayList<String>();
    }

    public void addElementList(String element) {
        elementList.add(element);
    }

    public void setParent(String p) {
        parent = p;
    }

    public void setHomePath(String u) {
        homePath = u;
    }

    public void levelLower() {
        levelCount++;
    }

    public void  levelUpper() {
        levelCount--;
    }

    public List<String> getElementList() {
        return elementList;
    }

    public String getParent() {
        return parent;
    }

    public String getHomePath() {
        return homePath;
    }

    public int getLevelCount() {
        return levelCount;
    }

    public String getGitName() {
        return gitName;
    }
}
