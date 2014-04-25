/**
 * 
 */
package org.sme.tools.cloudstack;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.sme.tools.cloudstack.model.Template;

import com.cloud.template.VirtualMachineTemplate.TemplateFilter;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 *
 * Apr 25, 2014
 */
public class TemplateAPITestCase {

  @Test
  public void listTemplates() throws Exception {
    List<Template> templates = TemplateAPI.listTemplates(TemplateFilter.all, null, "jenkins-slave",  null);
    Assert.assertEquals(1, templates.size());
  }
}
