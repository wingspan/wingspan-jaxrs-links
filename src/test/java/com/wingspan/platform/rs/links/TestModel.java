package com.wingspan.platform.rs.links;

/**
 * Simple model for the tests.
 */
public class TestModel
{
    String id;
    String filename = "foo/bar/asdfa\\\\sdf//foo";

    public TestModel()
    {}
    public TestModel(String id)
    {
        this.id = id;
    }

    public String getId()
    {
        return id;
    }
    public LinksObject getLinks()
    {
        return null;
    }

    public String getFilename()
    {
        return filename;
    }

    static class CommentModel
    {
        String id;
        String parentId;
        String text;

        public CommentModel(String id)
        {
            this.id = id;
        }

        public String getId()
        {
            return id;
        }

        public String getParentId()
        {
            return parentId;
        }

        public String getText()
        {
            return text;
        }
    }
}
