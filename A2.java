import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;
import java.io.File; 
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.*;
import java.util.*;
import java.util.regex.*;
import javax.swing.table.*;
import java.util.Set; 
import java.util.HashSet;
import javax.swing.table.*;
import java.util.Scanner;

public class A2 extends JFrame{
	private JMenuBar menuBar;
	private JMenu menu, submenu;
	private JMenuItem menuItem1, menuItem2, menuItem3;
	private JFileChooser fileChooser;
	private ArrayList<String> arraylist = new ArrayList<String>();
	private JComboBox<String> combobox = new JComboBox<String>();
	private DefaultComboBoxModel<String> cmodel = new DefaultComboBoxModel<String>();
	private Simulator getscr;
	private File file;
	private String select_string;
	private boolean show_SH;
	private boolean show_DH;
	private Set<String> myUniqueStrings = new HashSet<String>();
	private DefaultTableModel tmodel = new DefaultTableModel();
	private JTable table = new JTable();
	private JScrollPane scrollPane;
	private boolean firstload=true;
	private DefaultTableCellRenderer tableRender = new DefaultTableCellRenderer();
	
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new A2();
			}
		});
	}
	
	public A2(){
		//stage1
		JFrame frame = new JFrame("A2");
		JFileChooser fileChooser = new JFileChooser();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1000, 500);
		scrollPane = new JScrollPane(table);
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		menuBar = new JMenuBar();
		menu = new JMenu("File");
		menuBar.add(menu);
		menuItem1 = new JMenuItem("Open trace file");
		menuItem1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				int returnValue = fileChooser.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					file = fileChooser.getSelectedFile();
					cmodel.removeAllElements();
					if(file.getName().contains(".txt")){
						show_SH = true;
						show_DH = false;			
						getscr = new Simulator(file);
						Object[] gcr = getscr.getUniqueSortedSourceHosts();
						String[] s = new String[gcr.length];
						for (int i=0; i<gcr.length; i++){
							String convertedToString = String.valueOf(gcr[i]);
							cmodel.addElement(convertedToString);
						}
						combobox.setModel(cmodel);
						show_table_scr();
					}
				}
			}
		});	
		
		menuItem2 = new JMenuItem("Quit");
		menuItem2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				System.exit(0);
			}
		});	
		menuItem3 = new JMenuItem("Save");
		menuItem3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
			int returnValue = fileChooser.showSaveDialog(null);
			if (returnValue==JFileChooser.APPROVE_OPTION){
				try{
					File file = fileChooser.getSelectedFile();
					PrintWriter os = new PrintWriter(file);
					for (int row=0;row<table.getRowCount(); row++){
						for (int col=0; col<table.getColumnCount(); col++){
							os.print(table.getValueAt(row,col)+"\t");
						}
						os.println("");
					}
					os.close();
				}catch (IOException e){
					e.printStackTrace();
				}
			}
		}});
		
			
		menu.add(menuItem1);
		menu.add(menuItem2);
		menu.add(menuItem3);
		frame.setJMenuBar(menuBar);
		
		JPanel topPanel = new JPanel();
		JRadioButton radioButtonS = new JRadioButton("Source hosts");
		
		
		radioButtonS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try{
				if(file.getName().contains(".txt")&&(show_DH==true)){
						show_DH = false;
						show_SH = true;
						cmodel.removeAllElements();
						getscr = new Simulator(file);
						Object[] gcr = getscr.getUniqueSortedSourceHosts();
						for (int i=0; i<gcr.length; i++){
							String convertedToString = String.valueOf(gcr[i]);
							cmodel.addElement(convertedToString);
						}
						combobox.setModel(cmodel);
						show_table_scr();
				}}catch(Exception e){}
		}});
				
		JRadioButton radioButtonD = new JRadioButton("Destination hosts");
		
		radioButtonD.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try{
				if(file.getName().contains(".txt")&&(show_SH==true)){
						show_DH = true;
						show_SH = false;
						cmodel.removeAllElements();
						getscr = new Simulator(file);
						Object[] gcr = getscr.getUniqueSortedDestHosts();
						for (int i=0; i<gcr.length; i++){
							String convertedToString = String.valueOf(gcr[i]);
							cmodel.addElement(convertedToString);
						}
						firstload = false;
						combobox.setModel(cmodel);
						show_table_Dest();
				}}catch(Exception e){}
		}});	
		
		combobox.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				if(show_SH&& (combobox.getItemCount()!=0)){
				show_table_scr();}
				else if(show_DH&&firstload){}
				else if (show_DH&& (combobox.getItemCount()!=0)){
					show_table_Dest();
				}
				
		}});
		
		topPanel.add(radioButtonS);
		topPanel.add(radioButtonD);
		topPanel.add(combobox);
		ButtonGroup radioButtons  = new ButtonGroup();
		radioButtons.add(radioButtonS);
		radioButtons.add(radioButtonD);
		
		frame.getContentPane().add(topPanel, BorderLayout.NORTH);
		frame.setVisible(true);
	}
	
	/*
	*this method can update table data of DestHost, when combobox is changed.
	*@param frame 
	*/
	
	public void show_table_Dest(){
		Packet[] data; 
		Object[][] RowValues;
		String s = (String)combobox.getSelectedItem();
		Object[] ColNmaes = new Object[]{"Time Stamp","Source IP Address","Packet Size"};
		data = getscr.getTableData(s, false);
		PacketTableModel t = new PacketTableModel(data,false);
		RowValues = new Object[t.getRowCount()][t.getColumnCount()];
		for (int row=0; row<RowValues.length-2;row++){
			for (int col=0; col<RowValues[row].length; col++){
				RowValues[row][col] = t.getValueAt(row,col);
				}
			}
			RowValues[RowValues.length-2][2] = t.getValueAt(RowValues.length-2,2);
			RowValues[RowValues.length-1][2] = t.getValueAt(RowValues.length-1,2);
		tmodel.setDataVector(RowValues, ColNmaes);
		tmodel.fireTableDataChanged();
		table.setModel(tmodel);
		setOneRowBackgroundColor(table, RowValues.length-2, Color.RED);}

	
	public void show_table_scr(){
		Packet[] data; 
		Object[][] RowValues;
		
		String s = (String)combobox.getSelectedItem();
		Object[] ColNmaes = new Object[]{"Time Stamp","Destination IP Address","Packet Size"};
		data = getscr.getTableData(s, true);
		PacketTableModel t = new PacketTableModel(data,true);
		RowValues = new Object[t.getRowCount()][t.getColumnCount()];
		for (int row=0; row<RowValues.length-2;row++){
			for (int col=0; col<RowValues[row].length; col++){
				RowValues[row][col] = t.getValueAt(row,col);
				}
			}
			RowValues[RowValues.length-2][2] = t.getValueAt(RowValues.length-2,2);
			RowValues[RowValues.length-1][2] = t.getValueAt(RowValues.length-1,2);
		tmodel.setDataVector(RowValues, ColNmaes);
		tmodel.fireTableDataChanged();
		table.setModel(tmodel);
		setOneRowBackgroundColor(table, RowValues.length-2, Color.RED);
	}
	
	public static void setOneRowBackgroundColor(JTable table, int rowIndex,
			Color color) {
		try {
			DefaultTableCellRenderer tcr = new DefaultTableCellRenderer() {
 
				public Component getTableCellRendererComponent(JTable table,
						Object value, boolean isSelected, boolean hasFocus,
						int row, int column) {
					if (row == rowIndex) {
						setBackground(color);
						setForeground(Color.WHITE);
					}else if(row > rowIndex){
						setBackground(color);
						setForeground(Color.WHITE);
					}else{
						setBackground(Color.WHITE);
						setForeground(Color.BLACK);
					}
 
					return super.getTableCellRendererComponent(table, value,
							isSelected, hasFocus, row, column);
				}
			};
			int columnCount = table.getColumnCount();
			for (int i = 0; i < columnCount; i++) {
				table.getColumn(table.getColumnName(i)).setCellRenderer(tcr);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}


class Simulator{
	File Filename;
	ArrayList<Packet> packets = new ArrayList<Packet>();
	ArrayList<String> packets2 = new ArrayList<String>();
	
	public Simulator(File Filename){
		this.Filename = Filename;
		try{
			Scanner input = new Scanner(Filename);
		}catch(FileNotFoundException e){
			System.out.printf("java.io.FileNotFoundException: "+Filename+" (No such file or directory)\n");
			}
	}
	
	public ArrayList<Packet> getValidPackets(){
	    try{
			Scanner input = new Scanner(Filename);
			while (input.hasNextLine()){
			    String content = input.nextLine();
			    Packet packet = new Packet(content);
			    if (!packet.getSourceHost().equals("")){
			        packets.add(packet);
			    }
			}
		}catch(FileNotFoundException e){}
		return packets;
	}
	
	public Object[] getUniqueSortedSourceHosts(){
		this.getValidPackets();
		Set<String> myUniqueStrings = new HashSet<String>(); 
		for (int i=0; i<packets.size(); i++){
			Host h = new Host(packets.get(i).getSourceHost());
			myUniqueStrings.add(h.toString());
		}
		ArrayList<String> h2 = new ArrayList<String>(myUniqueStrings);
		for (int a=0; a<h2.size(); a++){
			for(int b=0; b<h2.size()-1; b++){
				String first1 = h2.get(b);
				String second1 = h2.get(b+1);
				Host first = new Host(first1);
				Host second = new Host(second1);
				int differ = first.compareTo(second);
				if (differ>0){
					h2.set(b, second1);
					h2.set(b+1, first1);
				}
			}
		}

		ArrayList<String> srcHosts = new ArrayList<String>();
		for (int i=0; i<h2.size(); i++){
			if (!srcHosts.contains(h2.get(i).toString())){
				srcHosts.add(h2.get(i).toString());
			}
		}
		Object[] object = new Object[h2.size()];
		object = h2.toArray(new Object[h2.size()]); 
		return object;
	}
	
	public Object[] getUniqueSortedDestHosts(){
		this.getValidPackets();
		Set<String> myUniqueStrings = new HashSet<String>(); 
		for (int i=0; i<packets.size(); i++){
			Host h = new Host(packets.get(i).getDestinationHost());
			myUniqueStrings.add(h.toString());
		}
		ArrayList<String> h2 = new ArrayList<String>(myUniqueStrings);
		for (int a=0; a<h2.size(); a++){
			for(int b=0; b<h2.size()-1; b++){
				String first1 = h2.get(b);
				String second1 = h2.get(b+1);
				Host first = new Host(first1);
				Host second = new Host(second1);
				int differ = first.compareTo(second);
				if (differ>0){
					h2.set(b, second1);
					h2.set(b+1, first1);
				}
			}
		}
		Object[] object = new Object[h2.size()];
		object = h2.toArray(new Object[h2.size()]); 
		return object;
	}
	
	public Packet[] getTableData(String ip, boolean isSrcHost){
		if (isSrcHost){
			try{
				Scanner input = new Scanner(Filename);
				ArrayList<Packet> p = new ArrayList<Packet>();
				while (input.hasNextLine()){
					String content = input.nextLine();
					Packet packet = new Packet(content);
					if (packet.getSourceHost().equals(ip)){
						p.add(packet);
					}
				}
				Packet[] table = new Packet[p.size()];
				table = p.toArray(new Packet[p.size()]); 
				return table;
			}
			catch(FileNotFoundException e){}
		}
		else{
		    try{
				ArrayList<Packet> p = new ArrayList<Packet>();
				Scanner input = new Scanner(Filename);
				while (input.hasNextLine()){
					String content = input.nextLine();
					Packet packet = new Packet(content);
					if (packet.getDestinationHost().equals(ip)){
						p.add(packet);
					}
				}
				Packet[] table = new Packet[p.size()];
				table = p.toArray(new Packet[p.size()]); 
				return table;
			}
			catch(FileNotFoundException e){}
		}
			Packet[] table = new Packet[packets.size()];
			return table;
	}
}

class Packet {
    private String src;
    private String dest;
    private String time;
    private String size;
    
    public Packet(String packet){
        boolean check = false;
		ArrayList<String> size_array = new ArrayList<String>();
		String time_re = "(\\d+)\\.(\\d+)"; //complete this
		Pattern pattern_time = Pattern.compile(time_re);
		
		String src_re = "^192\\.168\\.0\\.(\\d|\\d\\d|\\d\\d\\d)$"; //complete this
		Pattern pattern_src = Pattern.compile(src_re);
		
		String dest_re = "10\\.0\\.0\\.(\\d|\\d\\d)"; //complete this
		Pattern pattern_dest = Pattern.compile(dest_re);
		
		String size_re = "(\\d\\d)|\\d\\d\\d"; //complete this
		Pattern pattern_size = Pattern.compile(size_re);
        String[] parts = packet.split("\t");
		for (int i=0; i<parts.length; i++){
			Matcher matcher_time = pattern_time.matcher(parts[i]);
			Matcher matcher_src = pattern_src.matcher(parts[i]);
			Matcher matcher_dest = pattern_dest.matcher(parts[i]);
			Matcher matcher_size = pattern_size.matcher(parts[i]);
			if (matcher_time.matches()){
				time = parts[i];
			}
			else if (matcher_src.matches()){
				src = parts[i];
			}
			else if (matcher_dest.matches()){
				dest = parts[i];
				check = true;
			}
			else if(check && matcher_size.matches()){
				size_array.add(parts[i]);
			}
		}
		if (size_array.size() > 1){
		    size = size_array.get(1);
		}
    }
    
    public String getSourceHost(){
		if (src==null){
			return "";
		}
        return src;
    }
    
    public String getDestinationHost(){
		if (dest==null){
			return "";
		}
        return dest;
    }
    
    public double getTimeStamp(){	
		double t = Double.valueOf(time);
        return t;
    }
    
    public int getIpPacketSize(){
		if (size == null){
			return 0;
		}
		int s = Integer.valueOf(size);
        return s;
    }
    
    public void setSourceHost(String src){
        this.src = src;
    }
    
    public void setDestinationHost(String dest){
        this.dest = dest;
    }
    
    public void setTimeStamp(String time){
        this.time = time;
    }
    
    public void setIpPacketSize(String size){
        this.size = size;
    }
    
    public String toString(){
		String output = String.format("src=%s, dest=%s, time=%.2f, size=%d", 
		this.getSourceHost(), this.getDestinationHost(), this.getTimeStamp(), this.getIpPacketSize());
        return output;
    }
}

class Host implements Comparable<Host>{
    private String ip;
    public Host(String ip){
        this.ip = ip;
    }
    
    public String toString(){
        return this.ip;
    }
    
    public String getIP(){
        return ip;
    }
    
    public int compareTo(Host other){
        String var1 = this.ip;
        String var2 = other.getIP();
        int index1 = var1.lastIndexOf(".");
        int index2 = var2.lastIndexOf(".");
        String new_var1 = var1.substring(index1+1, var1.length());
        String new_var2 = var2.substring(index2+1, var2.length());
		int v1 = Integer.parseInt(new_var1);
		int v2 = Integer.parseInt(new_var2);
        return v1-v2;
    }
}


class PacketTableModel{
	public Packet[] packet_array;
	boolean model;
	
	public PacketTableModel(Packet[] packet_array, boolean model){
		this.packet_array = packet_array;
		this.model = model;
	}
	
	public int getRowCount(){
		return packet_array.length+2;
	}
	
	public int getColumnCount(){
		int maximum = 0;
		for (int i = 0; i<packet_array.length; i++){
			int count = 0;
			String packet = packet_array[i].toString();
			String[] parts = packet.split(",");
			count = parts.length;
			if (count > maximum){
			    maximum = count;
			}
		}
		return maximum-1;
	}
	
	public int total(){
	    int total = 0;
	    for(int i = 0; i<packet_array.length; i++){
	        total += packet_array[i].getIpPacketSize();
	    }
	    return total;
	}
	
	public Object getValueAt(int rowIndex, int columnIndex){
	    if (rowIndex<=packet_array.length-1){
		    Packet packet = packet_array[rowIndex];
    		
    		if (columnIndex==0){
    		    double value = packet.getTimeStamp();
    		    return value;
    		}
    		if (model){
        		if (columnIndex==1){
        		    String value = packet.getDestinationHost();
        		    return value;
        		}
    		}
    		if (!model){
    		    if (columnIndex==1){
        		    String value = packet.getSourceHost();
        		    return value;
        		}
    		}
    		
    		if (columnIndex==2){
    		    int value = packet.getIpPacketSize();
    		    return value;
    		}
	    }
    	else if (rowIndex==(packet_array.length)){
    		    return this.total();
    	}
    	else if (rowIndex == (packet_array.length+1)){
    	        double result = (this.total())/(packet_array.length);
    		    return result;
    	}
    	return "";
	}
}


