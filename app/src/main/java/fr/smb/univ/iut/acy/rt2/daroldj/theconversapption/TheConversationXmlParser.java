package fr.smb.univ.iut.acy.rt2.daroldj.theconversapption;

import android.util.Log;
import android.util.Xml;

import org.threeten.bp.Clock;
import org.threeten.bp.Instant;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class TheConversationXmlParser {

    private static final String ns = null;

    public static List<Entry> parseTilADayAgo(InputStream in) throws XmlPullParserException, IOException
    {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();

            Instant aDayAgo = Instant.now(Clock.systemUTC()).minusSeconds(86400);   //Minus 24Hours

            return readFeed(parser, aDayAgo);
        }
        finally
        {
            in.close();
        }
    }

    //Give a null object as UTCDateLimit if no date limit is required
    private static List<Entry> readFeed(XmlPullParser parser, Instant UTCDateLimit) throws XmlPullParserException, IOException
    {
        Log.v(TheConversationXmlParser.class.getName(),"start of readFeed" );

        List<Entry> entries = new ArrayList<>();
        boolean isOutdated = false;

        parser.require(XmlPullParser.START_TAG, ns, "feed");
        while (parser.next() != XmlPullParser.END_TAG && !isOutdated)
        {
            if (parser.getEventType() != XmlPullParser.START_TAG)
            {
                continue;
            }
            String name = parser.getName();

            // Starts by looking for the entry tag
            if (name.equals("entry"))
            {
                Entry entry = readEntry(parser);

                Instant publishedInstant = Instant.parse(entry.getPublished());

                if (UTCDateLimit != null && publishedInstant.isBefore(UTCDateLimit))
                {
                    Log.d(TheConversationXmlParser.class.getName(),"isOutdated set to TRUE" );

                    isOutdated = true;
                }
                else
                {
                    entries.add(entry);
                }
            }
            else
            {
                skip(parser);
            }
        }

        return entries;
    }

    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
    // to their respective "read" methods for processing. Otherwise, skips the tag.
    private static Entry readEntry(XmlPullParser parser) throws XmlPullParserException, IOException
    {
        parser.require(XmlPullParser.START_TAG, ns, "entry");
        String title = null;
        String summary = null;
        String link = null;
        String published = null;

        while (parser.next() != XmlPullParser.END_TAG)
        {
            if (parser.getEventType() != XmlPullParser.START_TAG)
            {
                continue;
            }

            String name = parser.getName();

            switch (name)
            {
                case "title":
                    title = readTitle(parser);
                    break;

                case "summary":
                    summary = readSummary(parser);
                    break;

                case "link":
                    link = readLink(parser);
                    break;

                case "published":
                    published = readPublished(parser);
                    break;

                default:
                    skip(parser);
                    break;
            }
        }
        return new Entry(published, link, title, summary);
    }

    // Processes title tags in the feed.
    private static String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException
    {
        parser.require(XmlPullParser.START_TAG, ns, "title");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "title");

        return title;
    }

    //process published tags in the feed
    private static String readPublished(XmlPullParser parser) throws IOException, XmlPullParserException
    {
        parser.require(XmlPullParser.START_TAG, ns, "published");
        String published = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "published");

        return published;
    }

    // Processes link tags in the feed.
    private static String readLink(XmlPullParser parser) throws IOException, XmlPullParserException
    {
        String link = "";
        parser.require(XmlPullParser.START_TAG, ns, "link");
        String tag = parser.getName();
        String relType = parser.getAttributeValue(null, "rel");

        if (tag.equals("link")) {
            if (relType.equals("alternate"))
            {
                link = parser.getAttributeValue(null, "href");
                parser.nextTag();
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, "link");

        return link;
    }

    // Processes summary tags in the feed.
    private static String readSummary(XmlPullParser parser) throws IOException, XmlPullParserException
    {
        parser.require(XmlPullParser.START_TAG, ns, "summary");
        String summary = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "summary");

        return summary;
    }

    // For the tags title and summary, extracts their text values.
    private static String readText(XmlPullParser parser) throws IOException, XmlPullParserException
    {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT)
        {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException
    {
        if (parser.getEventType() != XmlPullParser.START_TAG)
        {
            throw new IllegalStateException();
        }

        int depth = 1;
        while (depth != 0) {
            switch (parser.next())
            {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;

                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
