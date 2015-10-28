package com.wingspan.platform.rs.links;

/**
 * Created by agoodale on 10/28/15.
 */
public class TestGroups
{
    public interface Group1 {}

    public interface Group2 extends Group1 {}

    public interface Group3 extends Group1, Group2 {}
}
