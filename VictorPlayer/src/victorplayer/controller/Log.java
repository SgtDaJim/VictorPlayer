/**
 * @author Jim
 * @Time 2015年11月21日 上午1:17:57
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
 * @Time 2015年11月21日 上午1:17:57
 *
 */
public class Log {
	
	private String logMessage = "";//日志消息
	private Date date;
	private DateFormat df ;
	private String time = "";//时间字符串
	
	private File log;//日志文件
	private OutputStreamWriter osw = null;
	private String charset = "GBK";//设置字符编码
	
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
		df = new SimpleDateFormat("yyyy年MM月dd日EE hh时mm分ss秒", Locale.CHINA); 
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
			osw.write("----------以上消息生成时间：" + time + " ------------");
			osw.write("\r\n");
			osw.flush();
			osw.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	//清除日志
	public void deleteLog(){
		
		try{
			log.delete();
			log.createNewFile();
			System.out.println("清除日志成功！");
		}catch(Exception e){
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "清除日志失败！");
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
