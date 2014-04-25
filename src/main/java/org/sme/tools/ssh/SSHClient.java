/**
 * 
 */
package org.sme.tools.ssh;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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

  public static void main(String[] args) throws JSchException, IOException {
    JSch jsch = new JSch();
    
    Properties config = new Properties();
    config.put("StrictHostKeyChecking", "no");
    
    Session session =jsch.getSession("ubuntu", "172.27.4.86", 22);
    session.setPassword("ubuntu");
    session.setConfig("StrictHostKeyChecking", "no");
    session.connect();
    ChannelExec channel = (ChannelExec) session.openChannel("exec");
    channel.setCommand("knife cs server create slave1 --service \"Medium Instance\" --template jenkins-slave --zone myzone --networks defaultGuestNetwork --disk 10G --cloudstack-hypervisor kvm --node-name slave1 --use-sudo-password ubuntu --ssh-user ubuntu --ssh-password ubuntu -r jenkins-slave"
        /*+ " && knife cs server delete slave -y && knife node delete slave -y && knife client delete slave -y"*/);
    
    channel.setInputStream(null);
    
    //channel.setOutputStream(System.out);

    //FileOutputStream fos=new FileOutputStream("/tmp/stderr");
    //((ChannelExec)channel).setErrStream(fos);
    ((ChannelExec)channel).setErrStream(System.err);

    InputStream in=channel.getInputStream();

    channel.connect();

    byte[] tmp=new byte[1024];
    while(true){
      while(in.available()>0){
        int i=in.read(tmp, 0, 1024);
        if(i<0)break;
        System.out.print(new String(tmp, 0, i));
      }
      if(channel.isClosed()){
        if(in.available()>0) continue; 
        System.out.println("exit-status: "+channel.getExitStatus());
        break;
      }
      try{Thread.sleep(1000);}catch(Exception ee){}
    }
    channel.disconnect();
    session.disconnect();
  }
}
