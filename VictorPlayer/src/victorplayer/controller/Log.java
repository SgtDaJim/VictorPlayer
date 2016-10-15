/**
 * @author Jim
 * @Time 2015��11��21�� ����1:17:57
 */
package victorplayer.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.awt.Desktop;

import javax.swing.JOptionPane;

/**
 * @author Jim
 * @Time 2015��11��21�� ����1:17:57
 *
 */
public class Log {
	
	private String logMessage = "";//��־��Ϣ
	private Date date;
	private DateFormat df ;
	private String time = "";//ʱ���ַ���
	
	private File log;//��־�ļ�
	private OutputStreamWriter osw = null;
	private String charset = "GBK";//�����ַ�����
	
	private Desktop desktop;
	
	/**
	 * @param logMessage
	 * @param time
	 * @param date
	 */
	public Log() {
		super();
		//this.logMessage = logMessage;
		this.date = new Date();
		df = new SimpleDateFormat("yyyy��MM��dd��EE hhʱmm��ss��", Locale.CHINA); 
		time = df.format(date).toString();
		log = new File("log.ini");
	}
	

	public void writeLog(String logMessage){
		
		this.logMessage = logMessage;
		try{
			if(!log.exists()){
				log.createNewFile();
			}
			
			osw = new OutputStreamWriter(new FileOutputStream(log,true),charset);
			osw.write("\r\n");
			osw.write(new String(this.logMessage));
			osw.write("\r\n\r\n");
			osw.write("----------������Ϣ����ʱ�䣺" + time + " ------------");
			osw.write("\r\n");
			osw.flush();
			osw.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	//�����־
	public void deleteLog(){
		
		try{
			log.delete();
			log.createNewFile();
			System.out.println("�����־�ɹ���");
		}catch(Exception e){
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "�����־ʧ�ܣ�");
		}
		
	}
	
	public void openLog(){
		
		if(Desktop.isDesktopSupported()){
			desktop = Desktop.getDesktop();
		}
		try{
			desktop.open(log);
		}catch(Exception e){
			e.printStackTrace();
		}

		
	}
	
	

}
