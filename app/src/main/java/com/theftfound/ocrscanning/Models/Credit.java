package com.theftfound.ocrscanning.Models;

public class Credit {
    public String nameIcon;
    public String iconURL;

    public Credit(String nameIcon, String iconURL) {
        this.nameIcon = nameIcon;
        this.iconURL = iconURL;
    }

    public String getNameIcon() {
        return nameIcon;
    }

    public void setNameIcon(String nameIcon) {
        this.nameIcon = nameIcon;
    }

    public String getIconURL() {
        return iconURL;
    }

    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }
}
