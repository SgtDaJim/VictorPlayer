/**
 * @author Jim
 * @Time 2015��11��16�� ����3:43:42
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
 * @Time 2015��11��16�� ����3:43:42
 *
 */
public class VictorPlayer extends JFrame implements ControllerListener,ActionListener, Runnable {

	private JPanel contentPane;
	private JList list = null; //�����б�
	private PlayListItem pli = null; //�����б�������
	private String fileDir = ""; //�ļ�·��
	private MediaPlayer mp ; //������ʵ��
    private JPanel playpanel ; //������
    private ObjectOutputStream oos = null; //�������
    private ObjectInputStream ois = null; //�������    
    private File playList = null; //�����б��ļ�
    private Vector<String> nameList = null; //�����б���ʾ�ĸ�������
    private Vector<PlayListItem> vPli = null;
    private String title = ""; //��ʾ����
    private int index; //ѡ������
    private Log log;//��־��Ϣ
    
    //�����б��һ�ѡ��
    private JMenuItem play;
	private JMenuItem delete;
	private JMenuItem deleteAll;
	
	private boolean top = false;//������ǰ
	
	//���ſ���
	ButtonGroup turn ;
	JRadioButtonMenuItem[] turnValues ;
	String[] content = {"˳�򲥷�","�������"};
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VictorPlayer frame = new VictorPlayer();
					frame.setVisible(true);
					frame.addWindowListener(new WindowAdapter()//ע�ᴰ���¼�
					  {
					   public void windowClosing(WindowEvent e)
					      {
					        frame.newExit(); //�˳�ʱ���沥���б�
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
		
		//�����˵�
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu fileMenu = new JMenu("�ļ�");
		menuBar.add(fileMenu);
		
		JMenuItem itemOpen = new JMenuItem("��");
		fileMenu.add(itemOpen);
		itemOpen.addActionListener(this);
		fileMenu.addSeparator();
		
		JMenuItem itemExit = new JMenuItem("�˳�");
		fileMenu.add(itemExit);
		itemExit.addActionListener(this);
		
		JMenu playMenu = new JMenu("����");
		menuBar.add(playMenu);
		
		JMenuItem itemPlay = new JMenuItem("����");
		playMenu.add(itemPlay);
		itemPlay.addActionListener(this);
		playMenu.addSeparator();
		
		JMenuItem itemStop = new JMenuItem("ֹͣ");
		playMenu.add(itemStop);
		itemStop.addActionListener(this);
		playMenu.addSeparator();
		
		JMenu playControllerMenu = new JMenu("���ſ���");
		playMenu.add(playControllerMenu);
		
		JCheckBox onTop = new JCheckBox("������ǰ");
		playControllerMenu.add(onTop);
		playControllerMenu.addSeparator();
		
		//����������ǰ
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
		
		JMenu logMenu = new JMenu("��־");
		menuBar.add(logMenu);
		
		JMenuItem itemOpenLog = new JMenuItem("����־");
		logMenu.add(itemOpenLog);
		itemOpenLog.addActionListener(this);
		
		JMenuItem itemCleanLog = new JMenuItem("�����־");
		logMenu.add(itemCleanLog);
		itemCleanLog.addActionListener(this);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout());
		

			
			//��������
			playpanel = new JPanel();
			playpanel.setLayout(new BorderLayout());
			
			JButton upButton = new JButton("��һ��");
			playpanel.add(upButton, BorderLayout.WEST);
			upButton.addActionListener(this);
			
			JButton downButton = new JButton("��һ��");
			playpanel.add(downButton, BorderLayout.EAST);
			downButton.addActionListener(this);
			

		
		playpanel.doLayout();
		
		//�����ڷֿ�����
		JSplitPane splitPane = new JSplitPane();
		contentPane.add(splitPane, BorderLayout.CENTER);
		splitPane.setDividerSize(4);
		splitPane.setResizeWeight(1.0);
		splitPane.setLeftComponent(playpanel);
		
		//�����б��һ��˵�
		JPopupMenu playListMenu = new JPopupMenu();
		JMenuItem play = new JMenuItem("����ѡ������");
		JMenuItem delete = new JMenuItem("ɾ��ѡ����");	
		JMenuItem deleteAll = new JMenuItem("ɾ��ȫ��");
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
		
		playList = new File("list.ini");//�����б���Ŀ¼
		Thread readList=new Thread(this); //��ȡ�����б���߳�
		
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
	
	
	//���ļ�
	public void OpenFile(){
		
		FileDialog fd = new FileDialog(this,"ѡ���ļ�",FileDialog.LOAD);
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
	
	//����
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
			System.out.println("��Ҫ���ŵ��ļ������ڣ�");
			/**����**/JOptionPane.showMessageDialog(null,"��Ҫ���ŵ��ļ������ڣ�");
			log.writeLog("�����˲����ڵ��ļ���");
			return;
		}
		mp.setMediaLocator(new MediaLocator("file:///" + fileDir));
		mp.addControllerListener(this);
		mp.realize();
		mp.start();
		this.setTitle(title);
		list.setSelectedValue(nameList.elementAt(index), true);
	}
	
	//˫������ʱ���ò���
	public void playFromList(){
		
		pli = vPli.elementAt(index);
		title = pli.getFileName();
		fileDir = pli.getWholeName();
		System.out.print(fileDir);
		play();
		
	}
	
	//�Ҽ������˵�ʱ���ò���
	public void playFromList2(){
		
		index = list.getSelectedIndex();
		System.out.println(index);
		if(index == -1){
			/*��ѡ�������*/JOptionPane.showMessageDialog(null, "��ѡ�������");
			System.out.println("��ѡ�������");
			log.writeLog("�����˲����ڵ��ļ���");
		}else{
			playFromList();
		}
		
	}
	
	//������������õĲ��ŷ���
public void playFromList3(){
		
		pli = vPli.elementAt(index);
		title = pli.getFileName();
		fileDir = pli.getWholeName();
		System.out.print(fileDir);
		
		closePreviousPlayer();
		
		File f = new File(fileDir);
		if(!f.exists()){
			System.out.println("��Ҫ���ŵ��ļ������ڣ�");
			/**����**/JOptionPane.showMessageDialog(null,"��Ҫ���ŵ��ļ������ڣ�");
			log.writeLog("�����˲����ڵ��ļ���");
			return;
		}

		mp.setMediaLocator(new MediaLocator("file:///" + fileDir));
		mp.addControllerListener(this);
		mp.realize();
		mp.start();
		this.setTitle(title);
		list.setSelectedValue(nameList.elementAt(index), true);
		
	}
	
	//ֹͣ����
	public void stop(){
		
		if (mp != null) { 
			mp.stop(); 
			mp.deallocate(); 
		}
		title = "";
		
	}
	
	//ɾ��ѡ�и���
	public void delete(){
		
		int[] selected = list.getSelectedIndices();
		
		if(selected.length == 0){
			System.out.println("No Selected.");/*��ѡ�������*/
			JOptionPane.showMessageDialog(null, "��ѡ�������");
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
	
	//ɾ�������б������и���
	public void deleteAll(){
		
		nameList.removeAllElements();
		vPli.removeAllElements();
		list.setListData(nameList);
		stop();
		
	}
	
	//��һ��
	public void upper(){
		
		if(title == "")return;
		int i = nameList.indexOf(title)-1;
		if(i<0){
			System.out.println("�Ѿ�û����һ���ˣ�");
			/**����**/JOptionPane.showMessageDialog(null, "�Ѿ�û����һ���ˣ�");
		}else{
			index = i;
			playFromList();
			list.setSelectedIndex(index);
		}
		
	}
	
	//��һ��
	public void next(){
		
		if(title == "")return;
		int i = nameList.indexOf(title)+1;
		if(i>nameList.size()-1){
			System.out.println("�Ѿ�û����һ���ˣ�");
			/**����**/JOptionPane.showMessageDialog(null, "�Ѿ�û����һ���ˣ�");
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
		mp.stop(); //ֹͣ����
		mp.deallocate(); //����װ��DateSource 
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
			//˳�򲥷�
			if(turnValues[0].isSelected()){
				if(vPli.size() == 0)return;
				index=(index+1)%vPli.size();
			}
			
			//�������
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
		
		if (action.equals("��")) { 
			OpenFile(); 
		} 
		if (action.equals("����")) { 
			playFromList2(); 
		} 
		if (action.equals("ֹͣ")) { 
			stop(); 
		} 
		if (action.equals("�˳�")) { 
			dispose(); 
			newExit(); 
		}
		if(action.equals("����ѡ������")){
			playFromList2();
		}
		if(action.equals("ɾ��ѡ����")){
			delete();
		}
		if(action.equals("ɾ��ȫ��")){
			deleteAll();
		}
		if(action.equals("��һ��")){
			upper();
		}
		if(action.equals("��һ��")){
			next();
		}
		if(action.equals("����־")){
			log = new Log();
			log.openLog();
		}
		if(action.equals("�����־")){
			log = new Log();
			log.deleteLog();
		}
		
	}
	
	//���沥���б�
	public void saveList(){
		
		try{
		   oos=new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(playList)));
		   for(PlayListItem p : vPli){
			   oos.writeObject(p);
		   }
		   oos.flush();
		   oos.close();
		}catch(Exception e){
			/**��־**/log.writeLog("���沥���б�ʱ���ִ��󣡴�����Ϣ��"+e.toString()+" "+e.getMessage());
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
			log.writeLog("��ʼ���в�������");
			if(!playList.exists()){
				log.writeLog("�����б��ļ������ڡ��½������б��ļ���");
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
			/*��־*/log.writeLog("����ʱ���ִ��󣡴�����Ϣ��"+e.toString()+" "+e.getMessage());
			e.printStackTrace();
		}
		
		list.setListData(nameList);
		
	}
	
	public void newExit(){
		saveList();
		log.writeLog("�˳���������");
		System.exit(0);
	}
	
	
}
