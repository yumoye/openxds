/**
 *  Copyright � 2009 Misys plc, Sysnet International, Medem and others
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 *
 *  Contributors:
 *     Misys plc - Initial API and Implementation
 */
package org.openhealthexchange.openxds.repository;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

import org.openhealthexchange.common.configuration.ModuleManager;
import org.openhealthexchange.openpixpdq.ihe.IPixManagerAdapter;
import org.openhealthexchange.openpixpdq.ihe.audit.IheAuditTrail;
import org.openhealthexchange.openpixpdq.ihe.configuration.ConfigurationLoader;
import org.openhealthexchange.openpixpdq.ihe.impl_v2.MockPixAdapter;
import org.openhealthexchange.openpixpdq.ihe.impl_v2.PixManager;
import org.openhealthexchange.openpixpdq.ihe.impl_v2.TestLogContext;
import org.openhealthexchange.openpixpdq.ihe.impl_v2.TestPixQuery;
import org.openhealthexchange.openpixpdq.ihe.impl_v2.TestPixQuery.OidMock;
import org.openhealthexchange.openxds.repository.api.IXdsRepositoryItem;
import org.openhealthexchange.openxds.repository.api.IXdsRepositoryManager;
import org.openhealthexchange.openxds.repository.api.RepositoryRequestContext;

import com.misyshealthcare.connect.net.ConnectionFactory;
import com.misyshealthcare.connect.net.IConnectionDescription;
import com.misyshealthcare.connect.net.StandardConnectionDescription;
import com.misyshealthcare.connect.net.StandardConnectionDescriptionTest;
import com.misyshealthcare.connect.util.OID;

import junit.framework.TestCase;

/**
 * This class is used to test the file system based repository manager implementation.
 *  
 * @author <a href="mailto:Rasakannu.Palaniyandi@misys.com">Raja</a>
 *
 */
public class FileSystemRepositoryTest extends TestCase {

   private static File content1K;
   private static File content1M;
   private static File content2M;
   private IXdsRepositoryManager repositoryManager;
 //  private FileSystemRepositoryManager repositoryManager;
   private RepositoryRequestContext requestContext;
   private static final String id = Utility.getInstance().createId();
   String documentId = Utility.getInstance().stripId(id);
   private IConnectionDescription connection = null;
   private IPixManagerAdapter pixAdapter = null;   
	private PixManager actor = null;
   
   protected void setUp() throws Exception {
	   repositoryManager =(IXdsRepositoryManager)ModuleManager.getInstance().getBean("repositoryManager");
		 if (content1K == null) {			   
	            // initialize test content
			    requestContext = new RepositoryRequestContext(); 
	            char content1KArray[] = new char[1024]; //1Kb
	            char content1MArray[] = new char[1024*1024]; //1Mb
	            char content2MArray[] = new char[1024*1024*2]; //2Mb
	            Arrays.fill(content1KArray, 'a');
	            Arrays.fill(content1MArray, 'b');
	            Arrays.fill(content1MArray, 'c');
	            content1K = createTempFile(true, new String(content1KArray));
	            content1M = createTempFile(true, new String(content1MArray));
	            content2M = createTempFile(true, new String(content2MArray));
	            ConnectionFactory.loadConnectionDescriptionsFromFile(FileSystemRepositoryTest.class.getResource("XdsRepositoryConnectionsTest.xml").getPath());
	    		connection = ConnectionFactory.getConnectionDescription("xds-repository");
	    		requestContext.setConnection(connection);
	    	  }    
	}

   /**
	 * Test FileSystemRepositoryManager: insert method
	 */
	public void testInsertRepoItem(){
		try {
			IXdsRepositoryItem ro = createRepositoryItem(id, content1M);
			repositoryManager.insert(ro, requestContext);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
	/**
	 * Test FileSystemRepositoryManager: getRepoItem method
	 */
    public void testgetRepoItem(){
    	IXdsRepositoryItem invalidRepositoryId =null;
    	try {    		
    		IXdsRepositoryItem repositoryItem = repositoryManager.getRepositoryItem(documentId, new RepositoryRequestContext());
    		assertEquals(repositoryItem.getDocumentUniqueId(),documentId);
    		invalidRepositoryId = repositoryManager.getRepositoryItem("3d1a4aa5-e353-4d97-ae60-aa3ca9c96515", new RepositoryRequestContext());
    	} catch (Exception e) {
			assertNull(invalidRepositoryId);
		}
    	System.out.println("completed");
    	
	}
    
    /**
	 * Test FileSystemRepositoryManager: deleteDocumentID method
	 */
    public void testDeleteDocumentId(){
    	try {		
    		repositoryManager.delete(documentId, new RepositoryRequestContext());    		
    	} catch (Exception e) {
		   e.printStackTrace();
		}
    	
	}
    
    private static File createTempFile(boolean deleteOnExit, String content) throws IOException {
        // Create temp file.
        File temp = File.createTempFile("omar", ".txt");
        // Delete temp file when program exits.
        if (deleteOnExit) {
            temp.deleteOnExit();
        }
        
        // Write to temp file
        BufferedWriter out = new BufferedWriter(new FileWriter(temp));
        out.write(content);
        out.close();
        
        return temp;
    }
    
    private IXdsRepositoryItem createRepositoryItem(String id, File content) throws Exception {    	
        DataHandler contentDataHandler = new DataHandler(new FileDataSource(content));
        IXdsRepositoryItem repositoryItem = new XdsRepositoryItem(id, contentDataHandler);
        return repositoryItem;
        
    }
    
    public class OidMock implements OID.OidSource {
        public synchronized String generateId() {
            return Long.toString( System.currentTimeMillis() );
        }
    }
}