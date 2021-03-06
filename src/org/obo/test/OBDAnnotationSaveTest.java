package org.obo.test;

import java.io.PrintStream;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.bbop.dataadapter.DataAdapterException;
import org.bbop.io.AuditedPrintStream;
import org.obo.annotation.datamodel.Annotation;
import org.obo.dataadapter.GOStyleAnnotationFileAdapter;
import org.obo.dataadapter.OBDSQLDatabaseAdapter;
import org.obo.dataadapter.OBOAdapter;
import org.obo.dataadapter.OBOFileAdapter;
import org.obo.dataadapter.OBOSerializationEngine;
import org.obo.dataadapter.OBDSQLDatabaseAdapter.OBDSQLDatabaseAdapterConfiguration;
import org.obo.dataadapter.OBOFileAdapter.OBOAdapterConfiguration;
import org.obo.datamodel.OBOSession;
import org.obo.datamodel.impl.OBOClassImpl;
import org.obo.datamodel.impl.OBOSessionImpl;
import org.obo.reasoner.impl.ForwardChainingReasoner;
import org.obo.util.AnnotationUtil;

import org.apache.log4j.*;

public class OBDAnnotationSaveTest extends AbstractAnnotationTest {

	//initialize logger
	protected final static Logger logger = Logger.getLogger(OBDAnnotationSaveTest.class);

	String jdbcPath = "jdbc:postgresql://localhost:5432/obdtest";
	
	public OBDAnnotationSaveTest(String name) {
		super(name);
	}

	public Collection<String> getFilesToLoad() {
		String[] files={"gene_assoc.test"};
		return Arrays.asList(files);
	}
	
	public void setUp() throws Exception {
		ForwardChainingReasoner.checkRecache = false;
		
		// TODO: DRY - GOAnnotationFileTest
		GOStyleAnnotationFileAdapter adapter = new GOStyleAnnotationFileAdapter();
		OBOFileAdapter.OBOAdapterConfiguration config = new OBOFileAdapter.OBOAdapterConfiguration();
		for (String f : getFilesToLoad()) {
			config.getReadPaths().add(
					getResourcePath()+"/" + f);
			logger.info(f);
		}
		config.setAllowDangling(true);
		config.setBasicSave(false);
		config.setFailFast(false);
		session = (OBOSession) adapter.doOperation(OBOAdapter.READ_ONTOLOGY, config,
				null);
		testForAnnotationAssignedBy("FB:FBgn0061475","GO:0005843","FlyBase");

		
		// write
		OBDSQLDatabaseAdapterConfiguration wconfig = 
			new OBDSQLDatabaseAdapter.OBDSQLDatabaseAdapterConfiguration();
		wconfig.setSaveImplied(false);
		
		wconfig.setWritePath(jdbcPath);
		OBDSQLDatabaseAdapter wadapter = new OBDSQLDatabaseAdapter();
		//ReasonedLinkDatabase reasoner = rf.createReasoner();
		//reasoner.setLinkDatabase(linkDatabase);
		//wadapter.setReasoner(reasoner);
		//reasoner.recache();
		wadapter.doOperation(OBOAdapter.WRITE_ONTOLOGY, wconfig, session);
		

	}

	public void testHasLoaded() throws DataAdapterException, SQLException {
		// database -> session
		logger.info("reading");
		OBDSQLDatabaseAdapterConfiguration wconfig = 
			new OBDSQLDatabaseAdapter.OBDSQLDatabaseAdapterConfiguration();
		wconfig.setReadPath(jdbcPath);
		OBDSQLDatabaseAdapter wadapter = new OBDSQLDatabaseAdapter();
		session = wadapter.doOperation(OBOAdapter.READ_ONTOLOGY, wconfig, null);
		logger.info("read: "+session);
		
		testForName("FB:FBgn0061475","18SrRNA");
		Collection<Annotation> annots = AnnotationUtil.getAnnotations(session);
		logger.info("N annots:"+annots.size());
		testForAnnotation("FB:FBgn0061475","GO:0005843");
		testForAnnotation("FB:FBgn0024177","GO:0005921");
		testForNamespace("FB:FBgn0061475","FB");
		testForLink("FB:FBgn0061475","has_taxon","taxon:7227");
		testForAnnotationPublication("FB:FBgn0061475","GO:0005843","FB:FBrf0121292");
		testForAnnotationWithEvidenceCode("FB:FBgn0061475","GO:0005843","ISS");
		testForAnnotationAssignedBy("FB:FBgn0061475","GO:0005843","FlyBase");
		
		session = new OBOSessionImpl();
		annots = wadapter.fetchAnnotationsByObject(session, new OBOClassImpl("GO:0005843"));
		logger.info("N matching annots:"+annots.size());
		assertTrue(annots.size() > 0);
	}
	
	public void testQuery() throws SQLException, ClassNotFoundException {
		OBDSQLDatabaseAdapterConfiguration wconfig = 
			new OBDSQLDatabaseAdapter.OBDSQLDatabaseAdapterConfiguration();
		wconfig.setReadPath(jdbcPath);
		OBDSQLDatabaseAdapter wadapter = new OBDSQLDatabaseAdapter();
		wadapter.setConfiguration(wconfig);
		wadapter.connect();

		session = new OBOSessionImpl();
		Collection<Annotation> annots = wadapter.fetchAnnotationsByObject(session, new OBOClassImpl("GO:0005843"));
		logger.info("N matching annots:"+annots.size());
		for (Annotation annot : annots)
			logger.info("  match:"+annot);
		assertTrue(annots.size() > 0);
	
	}
	
	public void testNamespaceFilteredLoad() throws DataAdapterException {
		OBDSQLDatabaseAdapterConfiguration config = 
			new OBDSQLDatabaseAdapter.OBDSQLDatabaseAdapterConfiguration();
		OBDSQLDatabaseAdapter adapter = new OBDSQLDatabaseAdapter();
		
		// database -> session
		logger.info("reading ns filtered");
		
		config.addNamespace("MGI");
		config.setReadPath(jdbcPath);
		session = adapter.doOperation(OBOAdapter.READ_ONTOLOGY, config, null);
		logger.info("read: "+session);
		Collection<Annotation> annots = AnnotationUtil.getAnnotations(session);
		for (Annotation annot: annots)
			logger.info(annot.getSubject() + "-----" + annot.getObject());
		logger.info("N annots:"+annots.size());
		testFileSave("mgi-filtered");
		
		testForName("MGI:MGI:95723","Gjb5");
		testForAnnotation("MGI:MGI:95723","GO:0016020");
		testForAnnotationAssignedBy("MGI:MGI:95723","GO:0016020","UniProt");
		testNotPresent("FB:FBgn0061475");
		
	}
	

	
	// TODO: DRY
	public void testFileSave(String base) throws DataAdapterException {
		// session -> file
		OBOFileAdapter fileAdapter = new OBOFileAdapter();
		OBOAdapterConfiguration fileConfig = new OBOFileAdapter.OBOAdapterConfiguration();
		OBOSerializationEngine.FilteredPath path = new OBOSerializationEngine.FilteredPath();
		path.setUseSessionReasoner(false);
		path.setPath(base+".obo");
		fileConfig.getSaveRecords().add(path);
		fileConfig.setBasicSave(false);
		fileConfig.setSerializer("OBO_1_2");
		fileAdapter.doOperation(OBOAdapter.WRITE_ONTOLOGY, fileConfig, session);
	}
	
	public static Test suite() {
		PrintStream audited = new AuditedPrintStream(System.err, 25, true);

		System.setErr(audited);
		TestSuite suite = new TestSuite();
		addTests(suite);
		return suite;
	}

	public static void addTests(TestSuite suite) {
		suite.addTest(new OBDAnnotationSaveTest("testHasLoaded"));
		suite.addTest(new OBDAnnotationSaveTest("testNamespaceFilteredLoad"));
		suite.addTest(new OBDAnnotationSaveTest("testQuery"));
	}
}



