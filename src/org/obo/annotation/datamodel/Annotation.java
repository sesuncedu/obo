package org.obo.annotation.datamodel;

import java.util.Collection;

import org.obo.datamodel.Datatype;
import org.obo.datamodel.Instance;
import org.obo.datamodel.Link;
import org.obo.datamodel.LinkLinkedObject;
import org.obo.datamodel.LinkedObject;
import org.obo.datamodel.OBOClass;
import org.obo.datamodel.OBOProperty;
import org.obo.datamodel.impl.DatatypeValueImpl;
import org.obo.datamodel.impl.InstancePropertyValue;
import org.obo.history.HistoryItem;

public interface Annotation extends Instance {
/*

#
    * subject: <term id>
    * relationship: <property id>
    * object: <object id>
    * source: <identifier for the source of this annotation, for example a literature reference or experiment id>
    * evidence: <evidence code, perhaps an identifier for some built-in evidence objects>
    * assigned_by: <user id, probably just a string> 
 */
	
	/**
	 * each annotation posits a link
	 */
	public Link getPositedLink(); 
	public LinkedObject getAssignedBy();
	public LinkedObject getSubject();
	public LinkedObject getObject();
	public Collection<String> getDescriptions();

	public Collection<LinkedObject> getEvidence();
	public Collection<LinkedObject> getSources();
	public OBOProperty getRelationship();
	public boolean getIsNegated();
	
	public void setAssignedBy(String assignedBy); // TODO: deprecate?
	public void setAssignedBy(LinkedObject assignedBy);
	public void setSubject(LinkedObject subject);
	public void setRelationship(OBOProperty relationship);
	public void setObject(LinkedObject object);
	public void addEvidence(LinkedObject evidence);
	public void removeEvidence(LinkedObject evidence);
	public void setIsNegated(boolean isNegated);
	
	public void addDescription(String desc);
	public void removeDescription(String desc);

	// TODO: deprecate these two, or keep them as convenience methods?
	public void addSource(String source);
	//public void removeSource(String source);
	
	public void addSource(LinkedObject source);
	public void removeSource(LinkedObject source);

	
	public HistoryItem getRelationshipChangeItem(OBOProperty relationship);
	public HistoryItem getAssignedByChangeItem(LinkedObject assignedBy);
	public HistoryItem getSubjectChangeItem(LinkedObject subject);
	public HistoryItem getObjectChangeItem(LinkedObject object);

	//public HistoryItem addSourceChangeItem(String source);
	public HistoryItem addSourceChangeItem(LinkedObject source);
	public HistoryItem addEvidenceHistoryItem(LinkedObject code);
	//public HistoryItem removeSourceChangeItem(String source);
	public HistoryItem removeSourceChangeItem(LinkedObject source);
	public HistoryItem removeEvidenceHistoryItem(LinkedObject code);
}
