package org.sme.tools.cloudstack;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
/**
 * 
 */
import org.sme.tools.cloudstack.model.ServiceOffering;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 *
 * Apr 25, 2014
 */
public class ServiceOfferingAPITestCase {

  @Test
  public void listServiceOfferings() throws Exception {
    List<ServiceOffering> list = ServiceOfferingAPI.listServiceOfferings(null, null);
    Assert.assertEquals(3, list.size());
    list = ServiceOfferingAPI.listServiceOfferings(null, "Small Instance");
    Assert.assertEquals(1, list.size());
    
    ServiceOffering so = list.get(0);
    Assert.assertEquals(1, so.cpuNumber);
    Assert.assertEquals(500, so.cpuSpeed);
    Assert.assertEquals(512, so.memory);
  }
}
