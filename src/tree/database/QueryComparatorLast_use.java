package tree.database;

import java.util.Comparator;

public class QueryComparatorLast_use implements Comparator<Query>
{
	@Override
	public int compare(Query x, Query y) {
        if (x.getLast_use() < y.getLast_use())
        {
            return -1;
        }
        if (x.getLast_use() > y.getLast_use())
        {
            return 1;
        }
        return 0;
	}
}