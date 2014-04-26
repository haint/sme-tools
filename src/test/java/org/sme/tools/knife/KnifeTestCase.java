/**
 * 
 */
package org.sme.tools.knife;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.sme.tools.cloudstack.AbstractVMTestCase;
import org.sme.tools.jenkins.JenkinsMaster;

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
    Knife knife = Knife.getInstance();
    Thread.sleep(10 * 1000);//for VM booted
    if (knife.bootstrap(vm.nic[0].ipAddress, vm.name, "jenkins-slave")) {
      JenkinsMaster jkMaster = new JenkinsMaster("git.sme.org", "http", 8080);
      List<String> slaves = jkMaster.listSlaves();
      Assert.assertTrue(slaves.contains(vm.nic[0].ipAddress));
    }
  }
}
