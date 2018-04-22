package ie.swayne.ulcompanion;

import java.io.Serializable;
import java.util.ArrayList;

public class SulisModule implements Serializable {

    private String name;
    private String code;
    private String sem;
    private String moduleLink;
    private String resourcesLink;
    private String announcementsLink;
    private int numResources;
    private String moduleID;

    private ArrayList<SulisResource> resources = new ArrayList<>();


    protected SulisModule(String name, String code, String sem) {
        this.name = name;
        this.code = code;
        this.sem = sem;
    }

    private void setModuleID(String ID) {
        String[] temp = ID.split("/");
        moduleID = temp[temp.length - 1];
    }

    protected String getModuleID() {
        return moduleID;
    }

    protected void setModuleLink(String moduleLink) {
        this.moduleLink = moduleLink;
        setModuleID(moduleLink);
        setResourcesLink(moduleLink);
        setAnnouncementsLink(moduleLink);
    }

    public String getModuleLink() {
        return moduleLink;
    }

    private void setResourcesLink(String resourcesLink) {
        this.resourcesLink = resourcesLink + "/resources";
    }


    public String getResourcesLink() {
        return resourcesLink;
    }

    private void setAnnouncementsLink(String announcementsLink) {
        this.announcementsLink = announcementsLink + "/announcements";
    }

    public String getAnnouncementsLink() {
        return announcementsLink;
    }

    private void addResource(SulisResource resource) {
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

    protected String getCode() {
        return code;
    }

    public String getSem() {
        return sem;
    }
}
