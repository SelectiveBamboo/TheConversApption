package fr.smb.univ.iut.acy.rt2.daroldj.theconversapption;

public class Entry {

    private static final String ns = null;

    private String published;
    private String link;
    private String title;
    private String summary;

    public Entry(String published, String link, String title, String summary) {
        this.published = published;
        this.link = link;
        this.title = title;
        this.summary = summary;
    }

    public String getPublished() { return published; }

    public void setPublished(String published) { this.published = published; }

    public String getLink() { return link; }

    public void setLink(String link) { this.link = link; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getSummary() { return summary; }

    public void setSummary(String summary) { this.summary = summary; }

}