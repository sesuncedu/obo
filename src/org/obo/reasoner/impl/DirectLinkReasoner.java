package org.obo.reasoner.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.apache.log4j.Logger;
import org.obo.datamodel.IdentifiedObject;
import org.obo.datamodel.Instance;
import org.obo.datamodel.Link;
import org.obo.datamodel.LinkDatabase;
import org.obo.datamodel.LinkedObject;
import org.obo.datamodel.Namespace;
import org.obo.datamodel.NestedValue;
import org.obo.datamodel.OBOClass;
import org.obo.datamodel.OBOProperty;
import org.obo.datamodel.OBORestriction;
import org.obo.datamodel.PathCapable;
import org.obo.datamodel.impl.AbstractLinkDatabase;
import org.obo.datamodel.impl.OBORestrictionImpl;
import org.obo.reasoner.Explanation;
import org.obo.reasoner.ReasonedLinkDatabase;
import org.obo.reasoner.ReasonerListener;
import org.obo.util.TermUtil;

/**
 *  Infers "direct" links - these are typically trivial inferences that are useful
 *  for working in OboEdit
 *  
 *  - infers a simple link from an intersection link
 *  - infers symmetric class-level axioms (e.g. X disjoint_from Y <-> Y disjoint_from X)
 *  
 *  use of this reasoner may possibly introduce a slight impact in responsiveness in OE, compared to
 *  running with no reasoner. however, this should
 *  be constant (i.e. it should be no worse on an ontology of 10,000 terms compared to an ontology of 10 terms).
 *  brief tests indicate that this decrease in responsiveness is not really noticeable
 *  
 *  note that this class was cloned from OnTheFlyReasoner, there is a fair amount of duplicated code.
 *  it might be nice to abstract some of this.
 *  
 * @author CJM-2011
 */
public class DirectLinkReasoner extends AbstractLinkDatabase implements ReasonedLinkDatabase {

	//initialize logger
	protected final static Logger logger = Logger.getLogger(DirectLinkReasoner.class);

	public static class ReasonerLink implements Link {

		protected LinkedObject child;
		protected LinkedObject parent;
		protected LinkedObject ancestor;
		protected OBOProperty type;
		protected boolean lookedAt;
		protected ArrayList<AbstractExplanation> explanations;
		protected String id;
		protected int hash;

		public ReasonerLink(LinkedObject child, OBOProperty type,
				LinkedObject parent) {
			this.child = child;
			this.type = type;
			this.parent = parent;
			id = child.getID() + '-' + type.getID() + "->" + parent.getID();
			hash = child.hashCode() + type.hashCode() + parent.hashCode();
		}

		public Collection<AbstractExplanation> getExplanations() {
			if (explanations == null)
				return Collections.emptyList();
			else
				return explanations;
		}

		public Object clone() {
			throw new UnsupportedOperationException();
		}

		public void addExplanation(AbstractExplanation exp) {
			exp.setExplainedLink(this);
			if (explanations == null) {
				explanations = new ArrayList<AbstractExplanation>(5);
			}
			explanations.add(exp);
		}

		public void removeExplanation(AbstractExplanation exp) {
			if (explanations != null)
				explanations.remove(exp);
		}

		public boolean isImplied() {
			return true;
		}

		public String getID() {
			return id;
		}

		@Override
		public int hashCode() {
			return hash;
		}

		@Override
		public boolean equals(Object o) {
			if (o == null) {
				return false;
			} else if (o instanceof Link) {
				return TermUtil.equals(this, (Link) o);
			} else
				return false;
		}

		public void setNestedValue(NestedValue nv) {
		}

		public NestedValue getNestedValue() {
			return null;
		}

		public boolean isAnonymous() {
			return false;
		}

		public void setNamespace(Namespace namespace) {
		}

		public Namespace getNamespace() {
			return null;
		}

		public LinkedObject getChild() {
			return child;
		}

		public void setChild(LinkedObject child) {
			throw new UnsupportedOperationException();
		}

		public LinkedObject getParent() {
			return parent;
		}

		public void setParent(LinkedObject parent) {
			throw new UnsupportedOperationException();
		}

		public OBOProperty getType() {
			return type;
		}

		public void setType(OBOProperty type) {
			throw new UnsupportedOperationException();
		}

		public boolean isLookedAt() {
			return lookedAt;
		}

		public void setLookedAt(boolean lookedAt) {
			this.lookedAt = lookedAt;
		}
	}

	protected LinkDatabase linkDatabase;
	protected Collection<ReasonerListener> reasonerListeners = new ArrayList<ReasonerListener>();

	public DirectLinkReasoner() {
	}
	
	public DirectLinkReasoner(LinkDatabase linkDatabase) {
		setLinkDatabase(linkDatabase);
	}
	
	protected void fireDone() {
		for (ReasonerListener reasonerListener : reasonerListeners) {
			reasonerListener.reasoningFinished();
		}
	}

	protected void fireStart() {
		for (ReasonerListener reasonerListener : reasonerListeners) {
			reasonerListener.reasoningStarted();
		}
	}

	public void addReasonerListener(ReasonerListener listener) {
		reasonerListeners.add(listener);
	}

	public void removeReasonerListener(ReasonerListener listener) {
		reasonerListeners.remove(listener);
	}

	public Collection<LinkedObject> getParentsOfType(LinkedObject a,
			OBOProperty prop) {
		Collection<Link> parents = getParents(a);
		Collection<LinkedObject> out = new ArrayList<LinkedObject>();
		for (Link link : parents) {
			if (isSubPropertyOf(link.getType(), prop))
				out.add(link.getParent());
		}
		return out;
	}

	public boolean isInstanceOf(Instance a, OBOClass b) {
		if (a.getType() instanceof OBOClass)
			return isSubclassOf((OBOClass) a.getType(), b);
		else
			return false;
	}

	public boolean isRedundant(Link link) {
		return false;
	}
	
	private Link processLink(Link link) {
		if (link instanceof OBORestriction && ((OBORestriction)link).getCompletes()) {
			//System.out.println("DIRECTLINK:"+link);
			return new ReasonerLink(link.getChild(),link.getType(),link.getParent());
		}
		else {
			//System.out.println("KEEPING:"+link);
			return link;
		}
		
	}

	public Collection<Link> getChildren(LinkedObject lo) {
		Collection<Link> out = new ArrayList<Link>();
		for (Link link : linkDatabase.getChildren(lo)) {
			out.add(processLink(link));
		}
		for (Link link : linkDatabase.getParents(lo)) {
			if (link.getType().isSymmetric() && link.getType().isNonInheritable()) {
				//System.out.println("INV1="+link);
				out.add(new ReasonerLink(link.getParent(),link.getType(),link.getChild()));
			}
		}
		//System.out.println(lo+" xhas "+out.size()+" links");
		return out;
	}

	public Collection<Link> getParents(LinkedObject lo) {
		Collection<Link> out = new ArrayList<Link>();
		for (Link link : linkDatabase.getParents(lo)) {
			out.add(processLink(link));
		}
		for (Link link : linkDatabase.getChildren(lo)) {
			if (link.getType().isSymmetric() && link.getType().isNonInheritable()) {
				//System.out.println("INV2="+link);
				out.add(new ReasonerLink(link.getParent(),link.getType(),link.getChild()));
			}
		}
		//System.out.println(lo+" has "+out.size()+" links");
		return out;
	}

	public boolean isSubPropertyOf(OBOProperty a, OBOProperty b) {
		if (a.equals(b))
			return true;
		return hasRelationship(a, OBOProperty.IS_A, b) != null;
	}

	public boolean isSubclassOf(OBOClass a, OBOClass b) {
		if (a.equals(b))
			return true;
		return hasRelationship(a, OBOProperty.IS_A, b) != null;
	}

	public void addLink(Link link) {
	}

	public void addLinks(Collection<Link> links){
		
	}
	
	public void cancel() {
	}

	public Collection<Explanation> getExplanations(PathCapable link) {
		if (link instanceof ReasonerLink) {
			return (Collection) ((ReasonerLink) link).getExplanations();
		} else
			return Collections.emptySet();
	}

	public LinkDatabase getLinkDatabase() {
		return linkDatabase;
	}

	public boolean isCancelled() {
		return false;
	}

	public boolean isRunning() {
		return false;
	}

	public long recache() {
		fireStart();
		fireDone();
		return 0;
	}

	public void removeLink(Link link) {
	}
	
	public void removeLinks(Collection<Link> link) {
	}

	public void setLinkDatabase(LinkDatabase linkDatabase) {
		this.linkDatabase = linkDatabase;
	}

	public Collection<IdentifiedObject> getObjects() {
		return linkDatabase.getObjects();
	}

	public boolean hasChildren(LinkedObject lo) {
		return linkDatabase.hasChildren(lo);
	}

	public boolean hasParents(LinkedObject lo) {
		return linkDatabase.hasParents(lo);
	}

	public IdentifiedObject getObject(String id) {
		return linkDatabase.getObject(id);
	}

	public String getProgressString() {
		return null;
	}

	public Number getProgressValue() {
		return null;
	}
}
