/**
 * 
 */
package org.sme.tools.jenkins;

import java.io.IOException;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.sme.tools.cloudstack.AbstractVMTestCase;

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
  
  @Test
  public void testCreateJob() throws Exception {
    String ipAddress = vm.nic[0].ipAddress;
    
    Thread.sleep(10 * 1000); //for VM booted
    
    JenkinsSlave slave1 = new JenkinsSlave(ipAddress);

    System.out.println("create slave " + ipAddress);
    Assert.assertTrue(master.createSlave(slave1));
    
    Thread.sleep(10 * 1000);
    
    JenkinsJob job = new JenkinsJob(master, "job-" + System.currentTimeMillis(), slave1.getSlaveAddress(), "http://git.sme.org/root/simple-nlp.git", "clean install", "");
    int buildNumber = master.createMavenJobFromGit(job);
    Assert.assertEquals(1, buildNumber);
    
    Thread.sleep(10 * 1000); //for job submitted
    
    System.out.print("Progress: ");
    while(job.isBuilding(buildNumber)) {
      Thread.sleep(3 * 1000);
      System.out.print(".");
    }
    System.out.println(job.getName() + " build successful");
    Assert.assertEquals("SUCCESS", job.getStatus(buildNumber));
    Assert.assertTrue(job.delete());
  }
}
