package gov.nist.registry.ws.sq.test.getFolAndContents;

import gov.nist.registry.common2.registry.Metadata;
import gov.nist.registry.common2.registry.RegistryResponse;
import gov.nist.registry.common2.registry.Response;
import gov.nist.registry.common2.registry.storedquery.SqParams;
import gov.nist.registry.common2.registry.storedquery.StoredQuerySupport;
import gov.nist.registry.ws.configurations.ebxmlrr21.Ebxmlrr21StoredQueryFactory;
import gov.nist.registry.ws.sq.test.TestBase;
import gov.nist.registry.ws.sq.test.testdata.FindFolTestData;

import org.testng.annotations.Test;

public class BasicGetFolAndContents extends TestBase {
	FindFolTestData testdata;
	
	public BasicGetFolAndContents() throws Exception {
		super();
		testdata = new FindFolTestData();
	}
	

	@Test
	public void basicObjectRefQuery() throws Exception {
		StoredQuerySupport sqs = new StoredQuerySupport(this , log);
		SqParams params = new SqParams();
		sqs.params = params;
		sqs.return_leaf_class = false;
		params.addStringParm("$XDSFolderEntryUUID", testdata.getFolUuids().get(0));
		
		// implementation specific factory
		Ebxmlrr21StoredQueryFactory eFact = new Ebxmlrr21StoredQueryFactory(params, new RegistryResponse(Response.version_2),log);
		Metadata results = eFact.GetFolderAndContents(sqs);
				
		log.addOtherParam("results size", String.valueOf(results.getObjectRefs().size()));
		log.addOtherParam("Ids", results.getObjectIds(results.getObjectRefs()).toString());
		log.addOtherParam("contents", results.structure());
		
		assert sqs.has_validation_errors == false;
		assert results.getObjectRefs().size() == 3;
		assert results.getRegistryPackages().size() == 0;
		assert results.getAssociations().size() == 0;
		assert results.getExtrinsicObjects().size() == 0;
	}

	@Test
	public void basicLeafClassQuery() throws Exception {
		StoredQuerySupport sqs = new StoredQuerySupport(this , log);
		SqParams params = new SqParams();
		sqs.params = params;
		sqs.return_leaf_class = true;
		params.addStringParm("$XDSFolderEntryUUID", testdata.getFolUuids().get(0));
		
		// implementation specific factory
		Ebxmlrr21StoredQueryFactory eFact = new Ebxmlrr21StoredQueryFactory(params,new RegistryResponse(Response.version_2), log);
		Metadata results = eFact.GetFolderAndContents(sqs);
				
		log.addOtherParam("results size", String.valueOf(results.getObjectRefs().size()));
		log.addOtherParam("Ids", results.getObjectIds(results.getObjectRefs()).toString());
		log.addOtherParam("contents", results.structure());
		
		assert sqs.has_validation_errors == false;
		assert results.getRegistryPackages().size() == 1;
		assert results.getFolders().size() == 1;
		assert results.getAssociations().size() == 1;
		assert results.getExtrinsicObjects().size() == 1;
	}

}
