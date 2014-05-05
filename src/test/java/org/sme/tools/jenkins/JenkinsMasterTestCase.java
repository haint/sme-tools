/**
 * 
 */
package org.sme.tools.jenkins;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.sme.tools.cloudstack.AbstractVMTestCase;
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
    master = new JenkinsMaster("git.sme.org", "http", 8080);
  }
  
  @Override
  public void tearDown() throws Exception {
    master.deleteAllSlaves();
    super.tearDown();
  }

  //@Test
  public void testCreateSlave1() throws Exception {
    
    this.vm = this.createVM("jenkins-slave-non-ui");
    
    String ipAddress = this.vm.nic[0].ipAddress;

    System.out.print("Checking sshd on " + ipAddress);
    
    if (SSHClient.checkEstablished(ipAddress, 22, 30)) {
      JenkinsSlave slave1 = new JenkinsSlave(master, ipAddress);

      System.out.println("create slave " + ipAddress);
      Assert.assertTrue(slave1.join());

      List<String> slaves = master.listSlaves();

      Assert.assertEquals(1, slaves.size());
      Assert.assertTrue(slaves.contains(ipAddress));
    } else {
      Assert.fail("Can not establish ssh connection for " + ipAddress);
    }
  }
  
  //@Test
  public void testCreateJob() throws Exception {
    
    this.vm = this.createVM("jenkins-slave-non-ui");
    
    String ipAddress = vm.nic[0].ipAddress;
    
    if (SSHClient.checkEstablished(ipAddress, 22, 30)) {
      JenkinsSlave slave1 = new JenkinsSlave(master, ipAddress);

      System.out.println("creating slave " + ipAddress);
      Assert.assertTrue(slave1.join());
      
      JenkinsMavenJob job = new JenkinsMavenJob(master, "job-" + System.currentTimeMillis(), slave1.getSlaveAddress(), "http://git.sme.org/root/simple-nlp.git", "clean install", "");
      int buildNumber = job.submit();
      Assert.assertEquals(1, buildNumber);
      
      System.out.print("Progress: ");
      int start = 0;
      int last = 0;
      byte[] bytes = null;
      while(job.isBuilding(buildNumber)) {
        
        bytes = job.getConsoleOutput(buildNumber, start);
        
        Thread.sleep(1 * 1000);
        
        start += bytes.length;
        last = bytes.length;
        bytes = job.getConsoleOutput(buildNumber, start);
        byte[] next = new byte[bytes.length - last];
        
        System.arraycopy(bytes, last, next, 0, next.length);
        if (next.length > 0) { 
          String output = new String(next);
          System.out.println(output);
          if (output.indexOf("channel stopped") != -1) break;
        }
      }
      
      Assert.assertEquals("SUCCESS", job.getStatus(buildNumber));
      System.out.println(job.getName() + " build successful");
      
      Assert.assertTrue(job.delete());
    } else {
      Assert.fail("Can not establish ssh connection for " + ipAddress);
    }
  }
  
  @Test
  public void testCreateJob2() throws Exception {
    
    this.vm = this.createVM("jenkins-slave");
    
    String ipAddress = vm.nic[0].ipAddress;
    
    if (SSHClient.checkEstablished(ipAddress, 22, 120)) {
      Map<String, String> env = new HashMap<String,String>();
      env.put("DISPLAY", ":0");
      JenkinsSlave slave1 = new JenkinsSlave(master, ipAddress, env);

      System.out.println("creating slave " + ipAddress);
      Assert.assertTrue(slave1.join());
      
      JenkinsMavenJob job = new JenkinsMavenJob(
          master, 
          "job-" + System.currentTimeMillis(), slave1.getSlaveAddress(), 
          "http://git.sme.org/root/lms.git", "clean install", "");
      int buildNumber = job.submit();
      Assert.assertEquals(1, buildNumber);
      
      System.out.print("Progress: ");
      int start = 0;
      int last = 0;
      byte[] bytes = null;
      while(job.isBuilding(buildNumber)) {
        
        bytes = job.getConsoleOutput(buildNumber, start);
        
        Thread.sleep(1 * 1000);
        
        start += bytes.length;
        last = bytes.length;
        bytes = job.getConsoleOutput(buildNumber, start);
        byte[] next = new byte[bytes.length - last];
        
        System.arraycopy(bytes, last, next, 0, next.length);
        if (next.length > 0) { 
          String output = new String(next);
          System.out.println(output);
          if (output.indexOf("channel stopped") != -1) break;
        }
      }
      
      Assert.assertEquals("SUCCESS", job.getStatus(buildNumber));
      System.out.println(job.getName() + " build successful");
      
      Assert.assertTrue(job.delete());
    } else {
      Assert.fail("Can not establish ssh connection for " + ipAddress);
    }
  }
}
