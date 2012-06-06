package tree.database;

import java.util.Comparator;

public class QueryComparatorUsage implements Comparator<Query>
{
	@Override
	public int compare(Query x, Query y) {
		//es wird immer das Query mit der kleinsten Priorität entfernt
        if (x.getUsage() < y.getUsage())
        {
            return -1;
        }
        if (x.getUsage() > y.getUsage())
        {
            return 1;
        }
        return 0;
	}
}