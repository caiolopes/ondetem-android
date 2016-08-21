package com.icmc.ic.bixomaps.models;

/**
 * Model for a category item on the main menu
 */
public class Category {
    private String name;
    private String tag;
    private int icon;

    /**
     * @param name category name
     * @param icon category icon
     */
    public Category(String name, String tag, int icon) {
        this.name = name;
        this.tag = tag;
        this.icon = icon;
    }

    /**
     * @return category name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name category name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return tag
     */
    public String getTag() {
        return tag;
    }

    /**
     * @param tag tag
     */
    public void setTag(String tag) {
        this.tag = tag;
    }

    /**
     * @return category icon
     */
    public int getIcon() {
        return icon;
    }

    /**
     * @param icon category icon
     */
    public void setIcon(int icon) {
        this.icon = icon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Category category = (Category) o;

        return icon == category.icon && name.equals(category.name) && tag.equals(category.tag);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + tag.hashCode();
        result = 31 * result + icon;
        return result;
    }
}
