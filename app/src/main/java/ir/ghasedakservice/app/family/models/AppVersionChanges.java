package ir.ghasedakservice.app.family.models;

/**
 * Created by MA .
 */
public class AppVersionChanges {
    private String title;
    private int type;

    public AppVersionChanges() {
    }

    public AppVersionChanges(String title, int type) {
        this.title = title;
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public int getType() {
        return type;
    }

    public void setType(String year) {
        this.type = type;
    }

}
