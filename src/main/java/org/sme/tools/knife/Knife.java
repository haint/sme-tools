/**
 * 
 */
package org.sme.tools.knife;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.Queue;

import org.sme.tools.ssh.SSHClient;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSchException;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 *
 * Apr 26, 2014
 */
public class Knife {
  
  /** .*/
  private static Knife instance = null;
  
  /** .*/
  String workstation;
  
  /** .*/
  String server;
  
  /** .*/
  String username;
  
  /** .*/
  String password;

  private Knife() throws IOException {
    InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("knife.properties");
    Properties properties = new Properties();
    properties.load(is);
    this.workstation = properties.getProperty("chef-workstation");
    this.server = properties.getProperty("chef-server");
    this.username = properties.getProperty("username");
    this.password = properties.getProperty("password");
  }
  
  public boolean bootstrap(String nodeIP, String nodeName, String... recipes) throws JSchException, IOException {
    return bootstrap(nodeIP, nodeName, null, System.err, recipes);
  }
  
  public boolean bootstrap(String nodeIP, String nodeName, InputStream is, OutputStream err, String... recipes) throws JSchException, IOException {
    StringBuilder sb = new StringBuilder("knife bootstrap ").append(nodeIP);
    sb.append(" -x ubuntu -P ubuntu --sudo --use-sudo-password --no-host-key-verify -N ").append(nodeName);
    if(recipes != null && recipes.length != 0) {
      sb.append(" -r ");
      for (int i = 0; i < recipes.length; i++) {
        sb.append(recipes[i]);
        if (i < recipes.length - 1) sb.append(",");
      }
    }
    Channel channel = SSHClient.execCommand(workstation, 22, username, password, sb.toString(), is, err);
    int exitCode = SSHClient.printOut(System.out, channel);
    return exitCode == 0;
  }
  
  public boolean bootstrap(String nodeIP, String nodeName, Queue<String> queue, String... recipes) throws JSchException, IOException {
    StringBuilder sb = new StringBuilder("knife bootstrap ").append(nodeIP);
    sb.append(" -x ubuntu -P ubuntu --sudo --use-sudo-password --no-host-key-verify -N ").append(nodeName);
    if(recipes != null && recipes.length != 0) {
      sb.append(" -r ");
      for (int i = 0; i < recipes.length; i++) {
        sb.append(recipes[i]);
        if (i < recipes.length - 1) sb.append(",");
      }
    }
    Channel channel = SSHClient.execCommand(workstation, 22, username, password, sb.toString(), null, System.err);
    int exitCode = SSHClient.printOut(queue, channel);
    return exitCode == 0;
  }
  
  public boolean deleteNode(String nodeName) throws JSchException, IOException {
    StringBuilder sb = new StringBuilder("knife node delete ").append(nodeName).append(" -y && knife client delete ").append(nodeName).append(" -y");
    Channel channel = SSHClient.execCommand(workstation, 22, username, password, sb.toString(), null, System.err);
    int exitCode = SSHClient.printOut(System.out, channel);
    return exitCode == 0;
  }

  public static Knife getInstance() throws IOException {
    return instance == null ? instance = new Knife() : instance;
  }
}
