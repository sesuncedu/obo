package org.obo.datamodel.impl;

import java.util.*;

import org.bbop.util.*;
import org.obo.datamodel.*;
import org.obo.filters.*;

import org.apache.log4j.*;

public class FilteredLinkDatabase extends AbstractLinkDatabase {

	//initialize logger
	protected final static Logger logger = Logger.getLogger(FilteredLinkDatabase.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 8789956339731954652L;
	protected Filter termFilter;
	protected Filter linkFilter;

	protected LinkDatabase linkDatabase;
	protected boolean allowDangling = false;
	protected boolean followIsaClosure = false;

	public FilteredLinkDatabase(LinkDatabase linkDatabase) {
		this.linkDatabase = linkDatabase;
	}

	public void setLinkDatabase(LinkDatabase linkDatabase) {
		this.linkDatabase = linkDatabase;
	}

	public void setAllowDangling(boolean allowDangling) {
		this.allowDangling = allowDangling;
	}

	public boolean getAllowDangling() {
		return allowDangling;
	}

	public void setFollowIsaClosure(boolean followIsaClosure) {
		this.followIsaClosure = followIsaClosure;
	}

	public boolean getFollowIsaClosure() {
		return followIsaClosure;
	}

	protected final VectorFilter childFilter = new VectorFilter() {
		/**
		 * 
		 */
		private static final long serialVersionUID = -4341033786269422197L;

		public boolean satisfies(Object o) {
			if (o instanceof Link) {
				return satisfiesChild((Link) o);
			} else
				return false;
		}
	};

	protected final VectorFilter parentFilter = new VectorFilter() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 8146325578230708703L;

		public boolean satisfies(Object o) {
			if (o instanceof Link) {
				return satisfiesParent((Link) o);
			} else
				return false;
		}
	};

	protected boolean isPassThrough() {
		return linkFilter == null && termFilter == null;
	}

	protected Subset reusableSet;

	public static final int LOCAL_CACHE = 0;
	public static final int REUSABLE_ITERATOR = 4;

	protected int filterMethod;
	protected Subset reusableSubset = new Subset();

	{
		setFilterMethod(LOCAL_CACHE);
	}

	public void setFilterMethod(int filterMethod) {
		if (filterMethod == REUSABLE_ITERATOR)
			reusableSet = new Subset();
		else
			reusableSet = null;

		this.filterMethod = filterMethod;
	}

	public LinkDatabase getLinkDatabase() {
		return linkDatabase;
	}

	public Collection<Link> getChildren(LinkedObject lo) {
		if (isPassThrough())
			return linkDatabase.getChildren(lo);
		else {
			if (filterMethod == LOCAL_CACHE)
				return new Subset(childFilter, linkDatabase.getChildren(lo));
			else if (filterMethod == REUSABLE_ITERATOR) {
				reusableSubset.setFilter(childFilter);
				reusableSubset.setData(linkDatabase.getChildren(lo));
				return reusableSubset;
			}
			return null;
		}
	}

	public Collection<Link> getParents(LinkedObject lo) {
		if (isPassThrough())
			return linkDatabase.getParents(lo);
		else {
			if (filterMethod == LOCAL_CACHE)
				return new Subset(parentFilter, linkDatabase.getParents(lo));
			else if (filterMethod == REUSABLE_ITERATOR) {
				reusableSubset.setFilter(parentFilter);
				reusableSubset.setData(linkDatabase.getParents(lo));
				return reusableSubset;
			}
			return null;
		}
	}


	public Collection<IdentifiedObject> getObjects() {
		if (termFilter == null)
			return linkDatabase.getObjects();
		else 
			return new Subset(termFilter, linkDatabase.getObjects());

	}

	protected boolean satisfiesChild(Link link) {
		if (linkFilter == null) {
			return allowDangling || satisfies(link.getChild());
		} else {
			return linkFilter.satisfies(link)
			&& (allowDangling || satisfies(link.getChild()));
		}
	}

	protected boolean satisfiesParent(Link link) {
		if (linkFilter == null) {
			return satisfies(link.getChild())
			&& (allowDangling || satisfies(link.getParent()));
		} else {
			return linkFilter.satisfies(link)
			&& (allowDangling || satisfies(link.getParent()));
		}
	}

	protected boolean satisfies(LinkedObject lo) {
//		logger.debug("FilteredLinkDatabase.satisfies: " + lo);
		return termFilter == null || termFilter.satisfies(lo);
	}

	public void setFilters(Filter termFilter, Filter linkFilter) {
		this.termFilter = termFilter;
		this.linkFilter = linkFilter;
	}

	public void setLinkFilter(Filter linkFilter) {
		this.linkFilter = linkFilter;
	}

	public void setTermFilter(Filter termFilter) {
		this.termFilter = termFilter;
	}

	// getting objects for global filters
	public IdentifiedObject getObject(String id) {
		//		logger.debug("FilteredLinkDatabase.getObject");
		IdentifiedObject out = linkDatabase.getObject(id);
		if (out == null)
			return null;
		if (termFilter == null || termFilter.satisfies(out))
			return out;
		else
			return null;
	}
}
