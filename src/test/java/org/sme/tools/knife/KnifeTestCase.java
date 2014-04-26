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
    super.tearDown();
    Knife.getInstance().deleteNode(vm.name);
  }
  
  @Test
  public void testBootstrapAndDelete() throws Exception {
    Knife knife = Knife.getInstance();
    if (SSHClient.checkEstablished(vm.nic[0].ipAddress, 22, 3 * 1000)) {
      if (knife.bootstrap(vm.nic[0].ipAddress, vm.name, "jenkins-slave")) {
        JenkinsMaster jkMaster = new JenkinsMaster("git.sme.org", "http", 8080);
        List<String> slaves = jkMaster.listSlaves();
        Assert.assertTrue(slaves.contains(vm.nic[0].ipAddress));
      }
    }
  }
}
