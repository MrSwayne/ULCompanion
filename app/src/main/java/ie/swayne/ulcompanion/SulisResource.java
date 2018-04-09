package ie.swayne.ulcompanion;

/**
 * Created by Adam on 02/03/2018.
 */

public class SulisResource {

    private String folder;
    private String title;
    private String link;

    public SulisResource(String folder, String title, String link) {
        this.folder = folder;
        this.title = title;
        this.link = link;
    }

    public String getFolder() {
        return folder;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }
}
