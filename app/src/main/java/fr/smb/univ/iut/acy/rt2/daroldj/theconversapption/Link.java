package fr.smb.univ.iut.acy.rt2.daroldj.theconversapption;

public class Link {

    private String rel;
    private String type;
    private String href;

    public Link(String rel, String type, String href) {
        this.rel = rel;
        this.type = type;
        this.href = href;
    }

    public String getRel()
    {
        return rel;
    }

    public String getType()
    {
        return type;
    }

    public String getLink()
    {
        return href;
    }
}
