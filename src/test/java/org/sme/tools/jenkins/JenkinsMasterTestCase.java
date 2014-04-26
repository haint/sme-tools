/**
 * 
 */
package org.sme.tools.jenkins;

import java.io.IOException;
import java.util.List;

import junit.framework.Assert;

import org.apache.cloudstack.api.ApiConstants.VMDetails;
import org.apache.cloudstack.jobs.JobInfo.Status;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sme.tools.cloudstack.AsyncJobAPI;
import org.sme.tools.cloudstack.VirtualMachineAPI;
import org.sme.tools.cloudstack.VolumeAPI;
import org.sme.tools.cloudstack.model.Job;
import org.sme.tools.cloudstack.model.VirtualMachine;
import org.sme.tools.ssh.SSHClient;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 *
 * Apr 21, 2014
 */
public class JenkinsMasterTestCase {
  
  /** .*/
  private JenkinsMaster master;
  
  /** .*/
  private VirtualMachine vm;
  
  @Before
  public void setUp() throws IOException {
    master = new JenkinsMaster("git.sme.org", "http", "8080");
    String[] response = VirtualMachineAPI.quickDeployVirtualMachine("slave-" + System.currentTimeMillis(), "jenkins-slave-non-ui", "Small Instance", "Small");
    String vmId = response[0];
    String jobId = response[1];
    Job job = AsyncJobAPI.queryAsyncJobResult(jobId);
    while (!job.getStatus().done()) {
      job = AsyncJobAPI.queryAsyncJobResult(jobId);
    }
    if (job.getStatus() == Status.SUCCEEDED) {
      System.out.println("Created VM: " + vmId);
      vm = VirtualMachineAPI.findVMById(vmId, VMDetails.nics);
    }
  }
  
  @After
  public void tearDown() throws IOException {
    master.deleteAllSlaves();
    String jobId = VirtualMachineAPI.destroyVM(vm.id, true);
    Job job = AsyncJobAPI.queryAsyncJobResult(jobId);
    while (!job.getStatus().done()) {
      job = AsyncJobAPI.queryAsyncJobResult(jobId);
    }
    if (job.getStatus() == Status.SUCCEEDED) {
      System.out.println("Destroyed VM: " + vm.id);
      VolumeAPI.clearNotAttachedVolumes();
    }
  }

  @Test
  public void testCreateSlave() throws IOException {

    String ipAddress = vm.nic[0].ipAddress;
    if (SSHClient.checkEstablished(ipAddress, 22, 3 * 1000)) {
      JenkinsSlave slave1 = new JenkinsSlave(ipAddress);
      Assert.assertTrue(master.createSlave(slave1));
      List<String> slaves = master.listSlaves();
      Assert.assertEquals(1, slaves.size());
      Assert.assertTrue(slaves.contains(ipAddress));
    }
  }
}
