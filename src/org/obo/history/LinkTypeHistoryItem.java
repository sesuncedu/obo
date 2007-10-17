package org.obo.history;

import org.bbop.util.*;
import org.obo.datamodel.*;

import java.util.*;


public class LinkTypeHistoryItem extends LinkHistoryItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4652336581904427325L;

	String relType;

	public LinkTypeHistoryItem() {
		this(null, (String) null);
	}

	public LinkTypeHistoryItem(StringRelationship rel, String relType) {
		this.rel = rel;
		this.target = null;
		setRelType(relType);
	}

	public LinkTypeHistoryItem(Link tr, OBOProperty relType) {
		this.rel = createStringRelationship(tr);
		this.target = null;
		setRelType(relType);
	}

	@Override
	public int hashCode() {
		return getHash(rel) ^ getHash(relType);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof LinkTypeHistoryItem))
			return false;
		LinkTypeHistoryItem item = (LinkTypeHistoryItem) o;
		return ObjectUtil.equals(rel, item.getRel())
				&& ObjectUtil.equals(relType, item.getRelationshipType());
	}

	@Override
	public String getShortName() {
		return "change relationship";
	}

	public void setRelType(String relType) {
		this.relType = relType;
	}

	public void setRelType(OBOProperty type) {
		relType = type.getID();
	}

	public String getRelationshipType() {
		return relType;
	}

	@Override
	public String toString() {
		return "Changed relationship of " + rel + " to " + relType;
	}

	@Override
	public HistoryList forwardID(String oldID, Collection newIDs) {
		if (rel.canForward(oldID) || ObjectUtil.equals(target, oldID)) {
			HistoryList out = new DefaultHistoryList();
			Iterator it = newIDs.iterator();
			while (it.hasNext()) {
				String id = it.next().toString();

				LinkTypeHistoryItem newitem = (LinkTypeHistoryItem) clone();
				newitem.getRel().forwardID(oldID, id);
				if (newitem.getRelationshipType().equals(oldID))
					newitem.setRelType(id);
				out.addItem(newitem);
			}
			return out;
		}
		return null;
	}
}