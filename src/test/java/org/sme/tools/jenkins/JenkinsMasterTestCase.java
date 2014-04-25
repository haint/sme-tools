/**
 * 
 */
package org.sme.tools.jenkins;

import java.io.IOException;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 *
 * Apr 21, 2014
 */
public class JenkinsMasterTestCase {
  
  /** .*/
  private JenkinsMaster master;
  
  @Before
  public void setUp() {
    master = new JenkinsMaster("git.sme.org", "http", "8080");
  }
  
  @After
  public void tearDown() throws IOException {
    master.deleteAllSlaves();
  }

  //@Test
  public void testCreateSlave() throws IOException {
    JenkinsSlave slave1 = new JenkinsSlave("172.27.4.72");
    Assert.assertTrue(master.createSlave(slave1));
    
    JenkinsSlave slave2 = new JenkinsSlave("172.27.4.81");
    Assert.assertTrue(master.createSlave(slave2));
    
    JenkinsSlave slave3 = new JenkinsSlave("172.27.4.81");
    Assert.assertFalse(master.createSlave(slave3));
    
    List<String> slaves = master.listSlaves();
    Assert.assertEquals(2, slaves.size());
    Assert.assertTrue(slaves.contains("172.27.4.72"));
    Assert.assertTrue(slaves.contains("172.27.4.81"));
  }
}
