/**
 * 
 */
package org.sme.tools.cloudstack;

import org.apache.cloudstack.api.ApiConstants.VMDetails;
import org.apache.cloudstack.jobs.JobInfo.Status;
import org.junit.After;
import org.junit.Before;
import org.sme.tools.cloudstack.model.Job;
import org.sme.tools.cloudstack.model.VirtualMachine;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 *
 * Apr 26, 2014
 */
public class AbstractVMTestCase {
  /** .*/
  protected VirtualMachine vm;
  
  @Before
  public void setUp() throws Exception {
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
  public void tearDown() throws Exception {
    Thread.sleep(15 * 1000); //unstable
    
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
}
