package org.karnak.profilepipe.profilebody;

import java.util.List;

public class ProfileElementBody {
    private String name;
    private String codename;
    private String condition;
    private String action;
    private String option;
    private String args;
    private List<String> tags;
    private List<String> excludedTags;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCodename() {
        return codename;
    }

    public void setCodename(String codename) {
        this.codename = codename;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public String getArgs() {
        return args;
    }

    public void setArgs(String args) {
        this.args = args;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getExcludedTags() {
        return excludedTags;
    }

    public void setExcludedTags(List<String> excludedTags) {
        this.excludedTags = excludedTags;
    }
}
