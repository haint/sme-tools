/**
 * 
 */
package org.sme.tools.jenkins;

import java.io.IOException;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.sme.tools.cloudstack.AbstractVMTestCase;
import org.sme.tools.cloudstack.model.VirtualMachine;
import org.sme.tools.ssh.SSHClient;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 *
 * Apr 21, 2014
 */
public class JenkinsMasterTestCase extends AbstractVMTestCase {
  
  /** .*/
  private JenkinsMaster master;
  
  @Override
  public void setUp() throws Exception {
    super.setUp();
    master = new JenkinsMaster("git.sme.org", "http", 8080);
  }
  
  @Override
  public void tearDown() throws Exception {
    super.tearDown();
    master.deleteAllSlaves();
  }

  @Test
  public void testCreateSlave() throws IOException, InterruptedException {
    String ipAddress = vm.nic[0].ipAddress;

    Thread.sleep(10 * 1000); //for VM booted
    
    JenkinsSlave slave1 = new JenkinsSlave(ipAddress);

    System.out.println("create slave " + ipAddress);
    Assert.assertTrue(master.createSlave(slave1));

    List<String> slaves = master.listSlaves();

    Assert.assertEquals(1, slaves.size());
    Assert.assertTrue(slaves.contains(ipAddress));
  }
}
