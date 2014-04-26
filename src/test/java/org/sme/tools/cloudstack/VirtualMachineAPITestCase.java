/**
 * 
 */
package org.sme.tools.cloudstack;

import java.io.IOException;
import java.util.List;

import junit.framework.Assert;

import org.apache.cloudstack.api.ApiConstants;
import org.apache.cloudstack.jobs.JobInfo.Status;
import org.junit.AfterClass;
import org.junit.Test;
import org.sme.tools.cloudstack.model.Job;
import org.sme.tools.cloudstack.model.VirtualMachine;

import com.cloud.vm.VirtualMachine.State;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 *
 * Apr 25, 2014
 */
public class VirtualMachineAPITestCase {
  
  @AfterClass 
  public static void tearDown() throws IOException {
    VolumeAPI.clearNotAttachedVolumes();
  }
  
  @Test
  public void testListVirutalMachines() throws IOException {
    List<VirtualMachine> vms = VirtualMachineAPI.listVirtualMachines(null, "chef-workstation", State.Running, null, ApiConstants.VMDetails.nics);
    Assert.assertEquals(1, vms.size());
    VirtualMachine vm = vms.get(0);
    Assert.assertEquals("chef-workstation", vm.name);
    Assert.assertEquals("chef-workstation", vm.displayName);
    Assert.assertEquals(1, vm.nic.length);
    Assert.assertEquals("172.27.4.86", vm.nic[0].ipAddress);
    
    vms = VirtualMachineAPI.listVirtualMachines(null, "not-existed", State.Unknown, "not-existed", null);
    Assert.assertEquals(0, vms.size());
  }
  
  @Test
  public void quickDeployAndDestroy() throws Exception {
    String[] response = VirtualMachineAPI.quickDeployVirtualMachine("test-" + System.currentTimeMillis(), "jenkins-slave-non-ui", "Small Instance", "Small");
    String vmId = response[0];
    String jobId = response[1];
    Job job = AsyncJobAPI.queryAsyncJobResult(jobId);
    while (!job.getStatus().done()) {
      job = AsyncJobAPI.queryAsyncJobResult(jobId);
    }
    
    if (job.getStatus() == Status.SUCCEEDED) {
      System.out.println("Created VM: " + vmId);
      jobId = VirtualMachineAPI.destroyVM(vmId, true);
      job = AsyncJobAPI.queryAsyncJobResult(jobId);
      while (!job.getStatus().done()) {
        job = AsyncJobAPI.queryAsyncJobResult(jobId);
      }
      if (job.getStatus() == Status.SUCCEEDED) {
        System.out.println("Destroyed VM: " + vmId);
        return;
      } else {
        Assert.fail();
      }
    } else {
      Assert.fail();
    }
  }
}
