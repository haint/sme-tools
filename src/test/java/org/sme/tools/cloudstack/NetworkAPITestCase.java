/**
 * 
 */
package org.sme.tools.cloudstack;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.sme.tools.cloudstack.model.Network;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 *
 * Apr 25, 2014
 */
public class NetworkAPITestCase {

  @Test
  public void listNetworks() throws Exception {
    List<Network> list = NetworkAPI.listNetworks(null, null, null);
    Assert.assertEquals(1, list.size());
  }
}
