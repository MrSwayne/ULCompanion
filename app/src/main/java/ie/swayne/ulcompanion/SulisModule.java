package ie.swayne.ulcompanion;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Adam on 02/03/2018.
 */

public class SulisModule implements Serializable {

    private String name;
    private String code;
    private String sem;
    private String moduleLink;
    private String resourcesLink;
    private String announcementsLink;
    private int numResources;

    private ArrayList<SulisResource> resources = new ArrayList<SulisResource>();


    public SulisModule(String name, String code, String sem) {
        this.name = name;
        this.code = code;
        this.sem = sem;
    }

    public void setModuleLink(String moduleLink) {
        this.moduleLink = moduleLink;
        setResourcesLink(moduleLink);
        setAnnouncementsLink(moduleLink);
    }

    public String getModuleLink() {
        return moduleLink;
    }

    public void setResourcesLink(String resourcesLink) {
        this.resourcesLink = resourcesLink + "/resources";
    }

    public String getResourcesLink() {
        return resourcesLink;
    }

    public void setAnnouncementsLink(String announcementsLink) {
        this.announcementsLink = announcementsLink + "/announcements";
    }

    public String getAnnouncementsLink() {
        return announcementsLink;
    }

    public void addResource(SulisResource resource) {
        resources.add(resource);
        numResources++;
    }

    public void addResource(String folder, String title, String link) {
        addResource(new SulisResource(folder, title, link));
    }

    public int getNumResources() {
        return numResources;
    }

    public SulisResource getResource(int index) {
        return resources.get(index);
    }

    public String toString() {
        return "n:" + this.name + " code:" + this.code + " sem:" + this.sem + " link:" + this.moduleLink;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getSem() {
        return sem;
    }
}
