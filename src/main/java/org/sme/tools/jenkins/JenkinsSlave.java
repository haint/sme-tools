/**
 * 
 */
package org.sme.tools.jenkins;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 *
 * Apr 18, 2014
 */
public class JenkinsSlave {

  /** .*/
  private String slaveAddress;
  
  public JenkinsSlave(String slaveAddress) {
    this.slaveAddress = slaveAddress;
  }
  
  public boolean isExisted() {
    return false;
  }
  
  public HttpEntity buildFormData() throws IOException {
    List<NameValuePair> list = new ArrayList<NameValuePair>();
    BufferedReader reader = new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("jenkins-slave-template")));
    String line = null;
    while ((line = reader.readLine()) != null) {
      String[] arrays = line.split("\t");
      if (arrays.length == 2) {
        String name = arrays[0].trim();
        String value = arrays[1].trim();
        list.add(new BasicNameValuePair(name, value));
      }
    }
    list.add(new BasicNameValuePair("_.host", slaveAddress));
    list.add(new BasicNameValuePair("name", slaveAddress));
    BufferedInputStream is = new BufferedInputStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("jenkins-slave-json-template"));
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    byte[] buff = new byte[1024];
    for (int i = is.read(buff); i != -1; i = is.read(buff)) {
      bos.write(buff, 0, i);
    }
    String json = new String(bos.toByteArray());
    json = String.format(json, slaveAddress, slaveAddress);
    list.add(new BasicNameValuePair("json", json));
    return new UrlEncodedFormEntity(list);
  }
  
  public String getSlaveAddress() {
    return slaveAddress;
  }
}

