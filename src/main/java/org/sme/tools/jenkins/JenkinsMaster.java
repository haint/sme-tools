/**
 * 
 */
package org.sme.tools.jenkins;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONObject;
import org.sme.tools.http.HttpClientFactory;
import org.sme.tools.http.HttpClientUtil;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 *
 * Apr 21, 2014
 */
public class JenkinsMaster {

  /** .*/
  private String masterHost;
  
  /** .*/
  private String scheme;
  
  /** .*/
  private int port;
  
  /** .*/
  private String[] systemNodes = { "master", "chef-workstation" };
  
  public JenkinsMaster(String masterHost, String scheme, int port) {
    this.masterHost = masterHost;
    this.scheme = scheme;
    this.port = port;
  }
  
  public void createSlave(String slaveAddress) throws IOException {
    this.createSlave(new JenkinsSlave(slaveAddress));
  }
  
  public boolean createSlave(JenkinsSlave slave) throws IOException {
    DefaultHttpClient client = HttpClientFactory.getInstance();
    HttpContext httpContext = new BasicHttpContext();
    HttpPost post = new HttpPost(buildURL("computer/doCreateItem"));
    post.setEntity(slave.buildFormData());
    HttpResponse res = client.execute(post, httpContext);
    String body = HttpClientUtil.getContentBodyAsString(res);
    
    //JSONObject json = new JSONObject(HttpClientUtil.fetch(client, buildURL("computer/" + slave.getSlaveAddress() + "/api/json")));
    return body.length() == 0 /*&& !json.getBoolean("offline")*/;
  }
  
    List<String> slaves = new ArrayList<String>();
    public List<String> listSlaves() throws IOException {
    DefaultHttpClient client = HttpClientFactory.getInstance();
    String json = HttpClientUtil.fetch(client, buildURL("computer/api/json"));
    JSONObject jsonObj = new JSONObject(json);
    JSONArray array = jsonObj.getJSONArray("computer");
    for (int i = 0; i < array.length(); i++) {
      JSONObject obj = array.getJSONObject(i);
      String displayName = obj.getString("displayName");
      boolean isSystemNode = false;
      for (String sysNode : systemNodes) {
        if (displayName.equals(sysNode)) {
          isSystemNode = true; 
        }
      }
      if (!isSystemNode) slaves.add(displayName);
    }
    return Collections.unmodifiableList(slaves);
  }
  
  public void deleteAllSlaves() throws IOException {
    for (String slave : listSlaves()) {
      this.deleteSlave(slave);
    }
  }
  
  public boolean deleteSlave(JenkinsSlave slave) throws IOException {
    return this.deleteSlave(slave.getSlaveAddress());
  }
  
  public boolean deleteSlave(String slaveName) throws IOException {
    String url = buildURL("computer/" + slaveName + "/doDelete");
    DefaultHttpClient client = HttpClientFactory.getInstance();
    HttpContext httpContext = new BasicHttpContext();
    HttpPost post = new HttpPost(url);
    
    List<NameValuePair> params = new ArrayList<NameValuePair>();
    params.add(new BasicNameValuePair("Submit", "yes"));
    params.add(new BasicNameValuePair("json", "{}"));
    post.setEntity(new UrlEncodedFormEntity(params));
    
    HttpResponse res = client.execute(post, httpContext);
    String body = HttpClientUtil.getContentBodyAsString(res);
    return body.length() == 0;
  }
  
  /**
   * 
   * @return build number
   * @throws InterruptedException 
   */
  public int createMavenJobFromGit(JenkinsJob job) throws IOException, InterruptedException{
    String url = buildURL("createItem");
    DefaultHttpClient client = HttpClientFactory.getInstance();
    HttpContext httpContext = new BasicHttpContext();
    HttpPost post = new HttpPost(url);

    StringBuilder sb = new StringBuilder();
    sb.append("{").append("\"name\":").append("\"").append(job.getName()).append("\"");
    sb.append(", \"mode\" : \"hudson.maven.MavenModuleSet\", \"Submit\" : \"OK\"}");
    
    List<NameValuePair> params = new ArrayList<NameValuePair>();
    params.add(new BasicNameValuePair("Submit", "OK"));
    params.add(new BasicNameValuePair("json", sb.toString()));
    params.add(new BasicNameValuePair("mode", "hudson.maven.MavenModuleSet"));
    params.add(new BasicNameValuePair("name", job.getName()));
    post.setEntity(new UrlEncodedFormEntity(params));
    
    HttpResponse res = client.execute(post, httpContext);
    String body = HttpClientUtil.getContentBodyAsString(res);
    if (body.length() == 0) {
      url = buildURL("job/" + job.getName() + "/configSubmit");
      post = new HttpPost(url);
      post.setEntity(job.buildFormData());
      res = client.execute(post, httpContext);
      body = HttpClientUtil.getContentBodyAsString(res);
      
      if (body.length() == 0) {
        url = buildURL("job/" + job.getName() + "/build?delay=0sec");
        body = HttpClientUtil.fetch(client, url);
        
        if (body.length() == 0) {
          url = buildURL("job/" + job.getName() + "/api/json");
          body = HttpClientUtil.fetch(client, url);
          JSONObject json = new JSONObject(body);
          int nextBuildNumber = json.getInt("nextBuildNumber");
          return nextBuildNumber == 1 ? 1 : nextBuildNumber - 1;
        }
      }
    }
    return -1;
  }
  
  public String buildURL(String actionURL) {
    StringBuilder sb = new StringBuilder(scheme);
    sb.append("://").append(masterHost).append(":").append(port).append("/").append(actionURL);
    return sb.toString();
  }
  
  public String getMasterHost() {
    return masterHost;
  }
  
  public String scheme() {
    return scheme;
  }
  
  public int getPort() {
    return port;
  }
}
