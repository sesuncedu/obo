package org.obo.dataadapter;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.io.IOException;

import org.obo.datamodel.*;

public interface OBOParser extends OBOSimpleParser {

	public static class XrefPair {
		public String xref = null;
		public String desc = null;
		public NestedValue nv;

		public XrefPair(String xref, String desc) {
			this.xref = xref;
			this.desc = desc;
		}

		public void setNestedValue(NestedValue nv) {
			this.nv = nv;
		}
	}

	public boolean prefersRaw(String tag, String value, NestedValue nv)
			throws OBOParseException;

	public void readFormatVersion(String version) throws OBOParseException;

	public void readImport(String path) throws IOException, OBOParseException;

	public void readNamespaceIDRule(String ns, String rule)
			throws OBOParseException;

	public void readDefaultNamespace(String ns) throws OBOParseException;

	public void readFileVersion(String version) throws OBOParseException;

	public void readIDMapping(String originalid, String newid)
			throws OBOParseException;

	public void readIDPrefix(String prefix) throws OBOParseException;
	
	public void readIDSpace(String idspace, String uriPrefix) throws OBOParseException;

	public void readDate(Date date) throws OBOParseException;

	public void readSavedBy(String savedBy) throws OBOParseException;

	public void readAutogeneratedBy(String autogeneratedBy)
			throws OBOParseException;

	public void readRemark(String remark) throws OBOParseException;

	public void readSubsetDef(String name, String desc)
			throws OBOParseException;

	public void readSynonymCategory(String id, String name, int scope)
			throws OBOParseException;

	public void readNamespace(String ns, NestedValue val)
			throws OBOParseException;

	public void readRange(String range, NestedValue val)
			throws OBOParseException;

	public void readDomain(String domain, NestedValue val)
			throws OBOParseException;

	public void readIsCyclic(boolean isCyclic, NestedValue nv)
			throws OBOParseException;

	public void readIsSymmetric(boolean isSymmetric, NestedValue nv)
			throws OBOParseException;

	public void readIsTransitive(boolean isTransitive, NestedValue nv)
	throws OBOParseException;
	public void readIsUniversallyQuantified(boolean isUniversallyQuantified, NestedValue nv)
	throws OBOParseException;
	
	public void readTransitiveOver(String id, String ns, boolean implied,
			NestedValue nv) throws OBOParseException;
	public void readHoldsOverChain(String[] ids, String ns, boolean implied,
			NestedValue nv) throws OBOParseException;


	public void readID(String id, NestedValue val) throws OBOParseException;

	public void readName(String name, NestedValue val) throws OBOParseException;

	public void readAltID(String id, NestedValue val) throws OBOParseException;

	public void readComment(String comment, NestedValue val)
			throws OBOParseException;

	public void readDef(String def, OBOParser.XrefPair[] xrefs, NestedValue val)
			throws OBOParseException;

	public void readInstanceOf(String id, NestedValue val)
			throws OBOParseException;

	public void readPropertyValue(String propID, String val, String typeID,
			boolean quoted, NestedValue nv) throws OBOParseException;

	public void readXrefAnalog(XrefPair pair) throws OBOParseException;

	public void readXrefUnk(XrefPair pair) throws OBOParseException;

	public void readSubset(String name, NestedValue val)
			throws OBOParseException;

	public void readSynonym(String name, OBOParser.XrefPair[] xrefs, int scope,
			String catID, NestedValue val) throws OBOParseException;

	public void readRelationship(String rel_type, String id, boolean necessary,
			boolean inverseNecessary, boolean completes, boolean implied,
			Integer minCardinality, Integer maxCardinality,
			Integer cardinality, String ns, NestedValue val, List<String> args, boolean parentIsProperty)
			throws OBOParseException;

	public void readIsa(String id, String ns, boolean completes,
			boolean implied, NestedValue val) throws OBOParseException;

	public void readDisjoint(String id, String ns, boolean implied,
			NestedValue val) throws OBOParseException;
	public void readUnion(String id, String ns, boolean implied,
			NestedValue val) throws OBOParseException;

	public void readInverseOf(String id, String ns, boolean implied,
			NestedValue val) throws OBOParseException;

	public void readIsObsolete(NestedValue val) throws OBOParseException;

	public void readIsAnonymous(NestedValue val) throws OBOParseException;
	public void readIsMetadataTag(NestedValue val) throws OBOParseException;

	public void readExpandAssertionTo(String name, String value, NestedValue nv) throws OBOParseException;
	
	public void readIsClassLevel(String name, boolean value, NestedValue val) throws OBOParseException;
	
	public void readConsider(String id, NestedValue val)
			throws OBOParseException;

	public void readReplacedBy(String id, NestedValue val)
			throws OBOParseException;

	public void readModifiedBy(String user, NestedValue val)
			throws OBOParseException;

	public void readModificationDate(Date date, NestedValue val)
			throws OBOParseException;

	public void readCreatedBy(String user, NestedValue val)
			throws OBOParseException;

	public void readCreationDate(Date date, NestedValue val)
			throws OBOParseException;

	public void readAlwaysImpliesInverse(boolean b, NestedValue nv)
			throws OBOParseException;

	public void readIsReflexive(boolean b, NestedValue nv)
			throws OBOParseException;

	public void readImpliedID();
}
