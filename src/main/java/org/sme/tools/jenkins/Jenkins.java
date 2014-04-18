/**
 * 
 */
package org.sme.tools.jenkins;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.sme.tools.HttpClientFactory;
import org.sme.tools.HttpClientUtil;


/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 *
 * Apr 18, 2014
 */
public class Jenkins {

  /** .*/
  private String host;
  
  public Jenkins(String host) {
    this.host = host;
  }
  
  public void createJenkinsSlave() {
  }
  
  public String getHost() {
    return host;
  }
  
  public static void main(String[] args) throws IOException, InterruptedException {
    DefaultHttpClient client = HttpClientFactory.getInstance();
    HttpContext httpContext = new BasicHttpContext();
    HttpPost post = new HttpPost("http://git.sme.org:8080/computer/doCreateItem");
    JenkinsSlave slave =  new JenkinsSlave("slave1", "172.27.4.72");
    post.setEntity(slave.buildFormData());
    HttpResponse res = client.execute(post, httpContext);
    System.out.println(HttpClientUtil.getContentBodyAsString(res));
    
    slave = new JenkinsSlave("slave2", "172.27.4.81");
    post = new HttpPost("http://git.sme.org:8080/computer/doCreateItem");
    post.setEntity(slave.buildFormData());
    res = client.execute(post, httpContext);
    System.out.println(HttpClientUtil.getContentBodyAsString(res));
  }
}
