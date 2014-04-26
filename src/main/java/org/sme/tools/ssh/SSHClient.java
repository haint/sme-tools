/**
 * 
 */
package org.sme.tools.ssh;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 *
 * Apr 24, 2014
 */
public class SSHClient {
  
  public static boolean checkEstablished(final String host, final int port, int timeout) throws IOException {
    final Socket[] socket = new Socket[1];
    Thread checker = new Thread(new Runnable() {
      public void run() {
        socket[0] = null;
        try {
          socket[0] = new Socket(host, port);
        } catch (Exception e) {
          if (socket[0] != null && socket[0].isConnected()) {
            try {
              socket[0].close();
            } catch (Exception eee) {
            }
          }
          socket[0] = null;
        }
      }
    });
    checker.setName("Opening Socket " + host);
    checker.start();
    try {
      checker.join(timeout);
    } catch (java.lang.InterruptedException eee) {
    }
    if (socket[0] != null && socket[0].isConnected()) {
      socket[0].close();
      return true;
    } else {
      checker.interrupt();
      checker = null;
      return false;
    }
  }

  public static Session getSession(String host, int port, String username,
      String password) throws JSchException {
    JSch jsch = new JSch();

    Session session = jsch.getSession(username, host, port);
    session.setPassword(password);
    session.setConfig("StrictHostKeyChecking", "no");
    session.connect(3 * 1000);
    return session;
  }
  
  public static Channel execCommand(String host, int port, String uname, String pwd, String cmd, InputStream is, OutputStream err) throws JSchException, IOException {
    Session session = getSession(host, port, uname, pwd);
    return execCommand(session, cmd, is, err);
  }
  
  public static Channel execCommand(Session session, String command, InputStream is, OutputStream error) throws JSchException, IOException {
    ChannelExec channel = (ChannelExec) session.openChannel("exec");
    channel.setCommand(command);
    channel.setInputStream(is);
    
    ((ChannelExec)channel).setErrStream(error);

    channel.connect();
    
    return channel;
  }
  
  public static int printOut(PrintStream out, Channel channel) throws IOException {
    InputStream in=channel.getInputStream();
    byte[] tmp=new byte[1024];
    while(true){
      while(in.available()>0){
        int i=in.read(tmp, 0, 1024);
        if(i<0)break;
        out.print(new String(tmp, 0, i));
      }
      if(channel.isClosed()){
        if(in.available()>0) continue;
        return channel.getExitStatus();
//        out.println("exit-status: "+channel.getExitStatus());
      }
      try{Thread.sleep(1000);}catch(Exception ee){}
    }
  }

  public static void main(String[] args) throws JSchException, IOException {
    Session session = SSHClient.getSession("172.27.4.86", 22, "ubuntu", "ubuntu");
    Channel channel = SSHClient.execCommand(session, "knife bootstrap not-existed -x ubuntu -P ubuntu --sudo --use-sudo-password  --no-host-key-verify -N node-test", null, System.err);
    SSHClient.printOut(System.out, channel);
    channel.disconnect();
    session.disconnect();
  }
}
