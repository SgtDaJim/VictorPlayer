/**
 * @author Jim
 * @Time 2015年11月16日 下午3:43:42
 */
package victorplayer.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.EndOfMediaEvent;
import javax.media.MediaLocator;
import javax.media.NoPlayerException;
import javax.media.RealizeCompleteEvent;
import javax.media.bean.playerbean.MediaPlayer;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;

import java.awt.event.ActionEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

import victorplayer.controller.Log;
import victorplayer.model.*;

import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JPopupMenu;

import java.awt.Toolkit;

import javax.swing.JCheckBox;


/**
 * @author Jim
 * @Time 2015年11月16日 下午3:43:42
 *
 */
public class VictorPlayer extends JFrame implements ControllerListener,ActionListener, Runnable {

	private JPanel contentPane;
	private JList list = null; //播放列表
	private PlayListItem pli = null; //播放列表音乐项
	private String fileDir = ""; //文件路径
	private MediaPlayer mp ; //播放器实例
    private JPanel playpanel ; //控制条
    private ObjectOutputStream oos = null; //对象输出
    private ObjectInputStream ois = null; //对象读入    
    private File playList = null; //播放列表文件
    private Vector<String> nameList = null; //播放列表显示的歌曲名称
    private Vector<PlayListItem> vPli = null;
    private String title = ""; //显示标题
    private int index; //选项索引
    private Log log;//日志消息
    
    //播放列表右击选项
    private JMenuItem play;
	private JMenuItem delete;
	private JMenuItem deleteAll;
	
	private boolean top = false;//总在最前
	
	//播放控制
	ButtonGroup turn ;
	JRadioButtonMenuItem[] turnValues ;
	String[] content = {"顺序播放","随机播放"};
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VictorPlayer frame = new VictorPlayer();
					frame.setVisible(true);
					frame.addWindowListener(new WindowAdapter()//注册窗口事件
					  {
					   public void windowClosing(WindowEvent e)
					      {
					        frame.newExit(); //退出时保存播放列表
					      }
					  }
					);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public VictorPlayer() {
		setIconImage(Toolkit.getDefaultToolkit().getImage("src/328274-14012115502096.jpg"));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 506, 265);
		
		//顶栏菜单
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu fileMenu = new JMenu("文件");
		menuBar.add(fileMenu);
		
		JMenuItem itemOpen = new JMenuItem("打开");
		fileMenu.add(itemOpen);
		itemOpen.addActionListener(this);
		fileMenu.addSeparator();
		
		JMenuItem itemExit = new JMenuItem("退出");
		fileMenu.add(itemExit);
		itemExit.addActionListener(this);
		
		JMenu playMenu = new JMenu("播放");
		menuBar.add(playMenu);
		
		JMenuItem itemPlay = new JMenuItem("播放");
		playMenu.add(itemPlay);
		itemPlay.addActionListener(this);
		playMenu.addSeparator();
		
		JMenuItem itemStop = new JMenuItem("停止");
		playMenu.add(itemStop);
		itemStop.addActionListener(this);
		playMenu.addSeparator();
		
		JMenu playControllerMenu = new JMenu("播放控制");
		playMenu.add(playControllerMenu);
		
		JCheckBox onTop = new JCheckBox("总在最前");
		playControllerMenu.add(onTop);
		playControllerMenu.addSeparator();
		
		//设置总在最前
		onTop.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				if(onTop.isSelected()){
					top = true;
				}else{
					top = false;
				}
				setAlwaysOnTop(top);				
			}
		});
		
		turn = new ButtonGroup();
		turnValues = new JRadioButtonMenuItem[2];
		for(int i = 0; i<2; i++){
			turnValues[i] = new JRadioButtonMenuItem(content[i]);
			turn.add(turnValues[i]);
			playControllerMenu.add(turnValues[i]);
		}
		turnValues[0].setSelected(true);
		
		JMenu logMenu = new JMenu("日志");
		menuBar.add(logMenu);
		
		JMenuItem itemOpenLog = new JMenuItem("打开日志");
		logMenu.add(itemOpenLog);
		itemOpenLog.addActionListener(this);
		
		JMenuItem itemCleanLog = new JMenuItem("清除日志");
		logMenu.add(itemCleanLog);
		itemCleanLog.addActionListener(this);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout());
		

			
			//播放器栏
			playpanel = new JPanel();
			playpanel.setLayout(new BorderLayout());
			
			JButton upButton = new JButton("上一首");
			playpanel.add(upButton, BorderLayout.WEST);
			upButton.addActionListener(this);
			
			JButton downButton = new JButton("下一首");
			playpanel.add(downButton, BorderLayout.EAST);
			downButton.addActionListener(this);
			

		
		playpanel.doLayout();
		
		//将窗口分开两边
		JSplitPane splitPane = new JSplitPane();
		contentPane.add(splitPane, BorderLayout.CENTER);
		splitPane.setDividerSize(4);
		splitPane.setResizeWeight(1.0);
		splitPane.setLeftComponent(playpanel);
		
		//播放列表右击菜单
		JPopupMenu playListMenu = new JPopupMenu();
		JMenuItem play = new JMenuItem("播放选中首项");
		JMenuItem delete = new JMenuItem("删除选中项");	
		JMenuItem deleteAll = new JMenuItem("删除全部");
		playListMenu.add(play);
		playListMenu.addSeparator();
		playListMenu.add(delete);
		playListMenu.addSeparator();
		playListMenu.add(deleteAll);
		play.addActionListener(this);
		delete.addActionListener(this);
		deleteAll.addActionListener(this);
		
		list = new JList();
		list.addMouseListener(new MouseAdapter(){
			
			public void mouseClicked(MouseEvent e){
				
				if(e.getClickCount() == 2){
					index = list.locationToIndex(e.getPoint());
					//System.out.println(index);
					//System.out.println("click2");
					playFromList();
				}
				
				
			}
			
			
			@Override
			public void mousePressed(MouseEvent e) {
				
				if((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0){
					playListMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
		
		
		JScrollPane scrollPane = new JScrollPane(list);
		scrollPane.setSize(100, 265);
		splitPane.setRightComponent(scrollPane);
		
		playList = new File("list.ini");//播放列表储存目录
		Thread readList=new Thread(this); //读取播放列表的线程
		
		readList.start();
		try{
			Thread.sleep(10);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		nameList = new Vector();
		vPli = new Vector();
		log = new Log();
	}
	
	
	//打开文件
	public void OpenFile(){
		
		FileDialog fd = new FileDialog(this,"选择文件",FileDialog.LOAD);
		pli = new PlayListItem();
		String name = "";
		fd.setVisible(true);
		name = fd.getFile();
		//title = fd.getFile();
		this.setTitle(title);
		pli.setFileDir(fd.getDirectory());
		pli.setFileName(fd.getFile());
		fileDir = pli.getWholeName();
		if(fileDir.equals("")||fileDir.equals("nullnull"))return;
		System.out.println(fileDir);
		
		Boolean b = false;
		for(String s : nameList){
			if(s.equals(name)){
				b = true;
			}
		}
		if(b != true){
			nameList.add(name);
			vPli.add(pli);
			list.setListData(nameList);
			list.setSelectedIndex(nameList.size()-1);
		}
		
	}
	
	//播放
	public void play(){
		
		if(fileDir != "" && fileDir != "nullnull"){
			
			if(mp == null){
				
				mp = new MediaPlayer();
				
			}else{
				
				closePreviousPlayer();
				
			}
			
		}
		
		File f = new File(fileDir);
		if(!f.exists()){
			System.out.println("需要播放的文件不存在！");
			/**弹窗**/JOptionPane.showMessageDialog(null,"需要播放的文件不存在！");
			log.writeLog("播放了不存在的文件。");
			return;
		}
		mp.setMediaLocator(new MediaLocator("file:///" + fileDir));
		mp.addControllerListener(this);
		mp.realize();
		mp.start();
		this.setTitle(title);
		list.setSelectedValue(nameList.elementAt(index), true);
	}
	
	//双击歌曲时所用播放
	public void playFromList(){
		
		pli = vPli.elementAt(index);
		title = pli.getFileName();
		fileDir = pli.getWholeName();
		System.out.print(fileDir);
		play();
		
	}
	
	//右键弹出菜单时所用播放
	public void playFromList2(){
		
		index = list.getSelectedIndex();
		System.out.println(index);
		if(index == -1){
			/*请选择歌曲！*/JOptionPane.showMessageDialog(null, "请选择歌曲！");
			System.out.println("请选择歌曲！");
			log.writeLog("播放了不存在的文件。");
		}else{
			playFromList();
		}
		
	}
	
	//歌曲结束后采用的播放方法
public void playFromList3(){
		
		pli = vPli.elementAt(index);
		title = pli.getFileName();
		fileDir = pli.getWholeName();
		System.out.print(fileDir);
		
		closePreviousPlayer();
		
		File f = new File(fileDir);
		if(!f.exists()){
			System.out.println("需要播放的文件不存在！");
			/**弹窗**/JOptionPane.showMessageDialog(null,"需要播放的文件不存在！");
			log.writeLog("播放了不存在的文件。");
			return;
		}

		mp.setMediaLocator(new MediaLocator("file:///" + fileDir));
		mp.addControllerListener(this);
		mp.realize();
		mp.start();
		this.setTitle(title);
		list.setSelectedValue(nameList.elementAt(index), true);
		
	}
	
	//停止播放
	public void stop(){
		
		if (mp != null) { 
			mp.stop(); 
			mp.deallocate(); 
		}
		title = "";
		
	}
	
	//删除选中歌曲
	public void delete(){
		
		int[] selected = list.getSelectedIndices();
		
		if(selected.length == 0){
			System.out.println("No Selected.");/*请选择歌曲！*/
			JOptionPane.showMessageDialog(null, "请选择歌曲！");
		}else{
			System.out.println(selected.length);
			for(int i = 0,j=0;i<=selected.length-1;i++,j++){
				if(title == nameList.elementAt(selected[i]-j)){
					stop();
				}
				nameList.remove(selected[i]-j);
				vPli.remove(selected[i]-j);
				list.setListData(nameList);
			}
		}
		
	}
	
	//删除播放列表中所有歌曲
	public void deleteAll(){
		
		nameList.removeAllElements();
		vPli.removeAllElements();
		list.setListData(nameList);
		stop();
		
	}
	
	//上一首
	public void upper(){
		
		if(title == "")return;
		int i = nameList.indexOf(title)-1;
		if(i<0){
			System.out.println("已经没有上一首了！");
			/**弹窗**/JOptionPane.showMessageDialog(null, "已经没有上一首了！");
		}else{
			index = i;
			playFromList();
			list.setSelectedIndex(index);
		}
		
	}
	
	//下一首
	public void next(){
		
		if(title == "")return;
		int i = nameList.indexOf(title)+1;
		if(i>nameList.size()-1){
			System.out.println("已经没有下一首了！");
			/**弹窗**/JOptionPane.showMessageDialog(null, "已经没有下一首了！");
		}else{
			index = i;
			playFromList();
			list.setSelectedIndex(index);
		}
		
	}
	
	public void closePreviousPlayer(){
		
		if(mp == null){
			return ;
		}		
		mp.stop(); //停止播放
		mp.deallocate(); //重新装载DateSource 
		Component visual =mp.getVisualComponent();
		Component control = mp.getControlPanelComponent();
		if(visual != null){
			playpanel.remove(visual);
		}
		if(control != null){
			playpanel.remove(control);
		}
		playpanel.doLayout();
		
	}
	

	public synchronized void controllerUpdate(ControllerEvent event){
		
		if(event instanceof RealizeCompleteEvent){
			Component comp; 
			if ((comp = mp.getControlPanelComponent()) != null) { 
				playpanel.add(comp, BorderLayout.CENTER);
			} else { 
				closePreviousPlayer(); 
			}
			if ((comp = mp.getVisualComponent()) != null) { 
				playpanel.add(comp, BorderLayout.SOUTH); 
			} 
		}
		
		playpanel.doLayout();
		
		if(event instanceof EndOfMediaEvent){
			
			closePreviousPlayer();
			//顺序播放
			if(turnValues[0].isSelected()){
				if(vPli.size() == 0)return;
				index=(index+1)%vPli.size();
			}
			
			//随机播放
			if(turnValues[1].isSelected()){
				if(vPli.size() == 0)return;
				int i = (int)(Math.random()*vPli.size());
				if(vPli.size() >= 2){
					while(i == index){
						i = (int)(Math.random()*vPli.size());
					}
				}
				index = i;
			}
			playFromList3();
			
		}
		
	}
	

	public void actionPerformed(ActionEvent e){
		
		String action = e.getActionCommand().toString();
		
		if (action.equals("打开")) { 
			OpenFile(); 
		} 
		if (action.equals("播放")) { 
			playFromList2(); 
		} 
		if (action.equals("停止")) { 
			stop(); 
		} 
		if (action.equals("退出")) { 
			dispose(); 
			newExit(); 
		}
		if(action.equals("播放选中首项")){
			playFromList2();
		}
		if(action.equals("删除选中项")){
			delete();
		}
		if(action.equals("删除全部")){
			deleteAll();
		}
		if(action.equals("上一首")){
			upper();
		}
		if(action.equals("下一首")){
			next();
		}
		if(action.equals("打开日志")){
			log = new Log();
			log.openLog();
		}
		if(action.equals("清除日志")){
			log = new Log();
			log.deleteLog();
		}
		
	}
	
	//保存播放列表
	public void saveList(){
		
		try{
		   oos=new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(playList)));
		   for(PlayListItem p : vPli){
			   oos.writeObject(p);
		   }
		   oos.flush();
		   oos.close();
		}catch(Exception e){
			/**日志**/log.writeLog("保存播放列表时出现错误！错误消息："+e.toString()+" "+e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	public void run(){
		
		log = new Log();
		try{
			Thread.sleep(1);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		try{
			log.writeLog("开始运行播放器。");
			if(!playList.exists()){
				log.writeLog("播放列表文件不存在。新建播放列表文件。");
				playList.createNewFile();
				return;
			}
			
			ois=new ObjectInputStream(new BufferedInputStream(new FileInputStream(playList)));
			while(true){
				pli = (PlayListItem)ois.readObject();
				nameList.add(pli.getFileName());
				vPli.add(pli);
			}
		}catch(Exception e){
			/*日志*/log.writeLog("启动时出现错误！错误消息："+e.toString()+" "+e.getMessage());
			e.printStackTrace();
		}
		
		list.setListData(nameList);
		
	}
	
	public void newExit(){
		saveList();
		log.writeLog("退出播放器。");
		System.exit(0);
	}
	
	
}
