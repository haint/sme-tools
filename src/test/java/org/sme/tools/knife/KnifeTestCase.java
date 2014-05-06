/**
 * 
 */
package org.sme.tools.knife;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.sme.tools.cloudstack.AbstractVMTestCase;
import org.sme.tools.jenkins.JenkinsMaster;
import org.sme.tools.ssh.SSHClient;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 *
 * Apr 26, 2014
 */
public class KnifeTestCase extends AbstractVMTestCase {
  
  @Override
  public void tearDown() throws Exception {
    Knife.getInstance().deleteNode(vm.name);
    super.tearDown();
  }
  
  @Test
  public void testBootstrapAndDelete() throws Exception {
    
    String ipAddress = vm.nic[0].ipAddress;
    
    Knife knife = Knife.getInstance();
    
    if (SSHClient.checkEstablished(ipAddress, 22, 120)) {
      if (knife.bootstrap(ipAddress, vm.name, "jenkins-slave")) {
        JenkinsMaster jkMaster = new JenkinsMaster("git.sme.org", "http", 8080);
        List<String> slaves = jkMaster.listSlaves();
        Assert.assertTrue(slaves.contains(ipAddress));
      }
    } else {
      Assert.fail("Can not establish ssh connection for " + ipAddress);
    }
  }
}
