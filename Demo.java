//Developed by Feng Xie
import java.io.*;
import java.awt.*;
import java.applet.*;
import java.awt.event.*;
import java.net.*;

import javax.swing.*;

import java.math.*;
import java.text.*;
import java.lang.*;

public class Demo extends Applet implements ActionListener,ItemListener,WindowListener 
{
///layout of the simulator interface is:
///menuframe: the whole window;
///variablesPanel(left);
///DrawArea(right-up);
///DrawPanel(right-down),including legend, status bar and buttons.

	URL url=null;
	URL helpurl=null;
	public String currentInputFile;
	Frame menuframe;
	VariablesPanel vp;
	public DrawArea da;
	public DrawPanel dp;
	public NetworkDynamics nd;
	public Frame fAttributeEdit;
	Label id, attribute1, attribute2;
	

	boolean  graphRead = false;
	boolean  evolved = false;
	int  drawAttributes = 0;//true when speed is drawn; false when volume is drawn
	public String getnetwork;
	public Frame fStat;
	public TextArea stat;
	MenuBar mbar,fmbar;
	
	public int linkselected=-1;
	public int nodeselected=-1;
	public Label noticeAttributeEdit;
	public Label selectedid;
	public TextField textfieldAttribute1;
	public TextField textfieldAttribute2;
	
	public Button change;
	public Button cancel;
	
	public boolean networksaved=false;
	public boolean networkModified=false;
	
	GridBagLayout gbl = new GridBagLayout();
	GridBagConstraints constraints=new GridBagConstraints();


	DecimalFormat Formatter = new DecimalFormat("#######.00");
	

	public void init() {

		vp = new VariablesPanel();
		dp = new DrawPanel(this);
		da = new DrawArea( dp );
		getnetwork="River Network";


	//Define the main window
		menuframe = new MenuFrame("SAND: Simulator and Analyst of Network Design 1.0",  this )  ;
		//define the size of menuframe according to the screen size
		Dimension screensize = getToolkit().getScreenSize();

		menuframe.setLayout(new BorderLayout());
		menuframe.add("West", vp);
		menuframe.add("Center", da);

		menuframe.addWindowListener(this);
		menuframe.setSize ((int)(1.0*screensize.width),
					  (int)(0.99*screensize.height));

		menuframe.setVisible(true);
		//define the menu
		mbar = new MenuBar();
		menuframe.setMenuBar(mbar);
		Menu song = new Menu("SAND");
		Menu help=new Menu("Help");

		MenuItem  evolve1,quit,about,instruction;
		song.add(evolve1 = new MenuItem("Evolve "));
		song.add(quit = new MenuItem("Quit"));
		help.add(instruction = new MenuItem("Instructions"));
		
		help.add(about=new MenuItem("About SAND1.0"));

	//Define the link editing window	
		fAttributeEdit = new Frame("Edit Link/Node Properties");
		fAttributeEdit.addWindowListener(this);
		fAttributeEdit.setSize ((int)(0.2*screensize.width),  (int)(0.20*screensize.height));
		fAttributeEdit.setVisible(false);
		
		fAttributeEdit.setLayout(gbl);

		constraints.weightx =1.0;
		constraints.weighty=1.0;

		constraints.anchor=GridBagConstraints.WEST;
		constraints.fill=GridBagConstraints.HORIZONTAL ;
		
		addComponent(0,0,1,1,new Label(" "));
		id=new Label(" Link ID:",Label.RIGHT);
		
		addComponent(1,0,1,1,id);
		
		selectedid = new Label("      ");
		selectedid.setText("");
		addComponent(2,0,1,1,selectedid);
		addComponent(3,0,1,1,new Label(" "));
		
		attribute1=new Label(" Number of lanes:");
		addComponent(1,1,1,1,attribute1);
		textfieldAttribute1 = new TextField(4);
		textfieldAttribute1.setText("");
		addComponent(2,1,1,1,textfieldAttribute1);
			
		attribute2=new Label(" Toll in dollar:");
		addComponent(1,2,1,1,attribute2);
		textfieldAttribute2 = new TextField(4);
		textfieldAttribute2.setText("");
		addComponent(2,2,1,1,textfieldAttribute2);

		change = new Button("Change");
		addComponent(1,3,1,1,change);
		change.addActionListener(this);

		cancel = new Button("Cancel");
		addComponent(2,3,1,1,cancel);
		cancel.addActionListener(this);

		noticeAttributeEdit = new Label("Change properties above");
		addComponent(1,4,4,1,noticeAttributeEdit);
		
		
		mbar.add(song);
		mbar.add (help);

		evolve1.addActionListener(this);
		quit.addActionListener(this);
		about.addActionListener(this);
		instruction.addActionListener(this);

	//Define the statistics window
		fStat=new Frame("Statistics");
		stat=new TextArea("");
		fStat.dispose();
		fStat=new Frame("Statistics");
		fStat.addWindowListener(this);

		stat=new TextArea("");
		fStat.setSize ((int)(0.35*screensize.width),
					  (int)(0.80*screensize.height));
		//define the menu
		fmbar = new MenuBar();
		fStat.setMenuBar(fmbar);
		Menu file = new Menu("File");

		MenuItem  save,close;
		file.add(save = new MenuItem("Save Statistics to File"));
		file.add(close = new MenuItem("Close"));

		fmbar.add(file);
		fStat.add(stat,"Center");

		save.addActionListener(this);
		close.addActionListener(this);
		fStat.setVisible(false);
		
///load the 10*10 network when the window is opened
		vp.network.select("River Network" );
		dp.showStatus.setText("A network accoss river loaded...");

		dp.evolve.setEnabled(true) ;
		dp.statistics .setEnabled(false);
		currentInputFile = "River.txt";
		url=getClass().getResource(currentInputFile);


		try {
			nd = new NetworkDynamics( vp.variables,url, currentInputFile);
		} catch (IOException e) {
		}
		
		dp.statistics .setEnabled( false);
		dp.whichAttribute.setEnabled(false) ;
		dp.scale .setEnabled( false);

		da.setMapVariables();
		graphRead = true;
		evolved = false;
		da.currentYear = 0;
		da.repaint();
///////////

	}

	public void addComponent(int x, int y, int w, int h, Component c)
	{
		constraints.gridx=x;
		constraints.gridy=y;
		constraints.gridwidth=w;
		constraints.gridheight=h;

		gbl.setConstraints( c,constraints);

		fAttributeEdit.add(c);
	}


	public void paint( Graphics g ) {
		//da.paint( g);
	}


///define the events related to window
	public void windowClosing(WindowEvent e){
		Object obj = e.getSource();
		if(obj.equals( menuframe))menuframe.dispose() ;
		else if (obj.equals( fStat))fStat.dispose() ;
		else if (obj.equals( fAttributeEdit)){
			fAttributeEdit.dispose() ;
			linkselected=-1;
			nodeselected=-1;
			vp.edit.setEnabled(false);
			da.setEnabled(true);
			da.repaint();
		}
	}

	public void windowOpened(WindowEvent e){
		da.setVisible(true) ;

	}

	public void windowActivated(WindowEvent e){

		da.repaint() ;
	}

	public void windowDeactivated(WindowEvent e){

		da.repaint() ;
	}

	public void windowIconified(WindowEvent e){

		da.repaint() ;
	}

	public void windowDeiconified(WindowEvent e){

		da.repaint() ;
	}

	public void windowClosed(WindowEvent e){


	}
//

	public void actionPerformed( ActionEvent ae){
		String arg = (String) ae.getActionCommand();
		Object obj = ae.getSource();

//
		  if(arg=="Evolve "){
				System.out.print("Network modified: "+networkModified+"; Network saved: "+networksaved+"\n");
				boolean perform=true;
				
				for (int i=0;i<nd.dg.edges;i++){
					nd.dg.link_info[i][7]=0;
				}
				nd.dg.updateLinkInfo();
				
				
				if (networkModified && !networksaved)
				{
					//////////////Remind people to save before evolve
					int option = JOptionPane.showConfirmDialog(menuframe,"You may not retrive your editing work next time.\nDo you want to proceed without saving the network?","Network modified but not saved!",JOptionPane.YES_NO_OPTION);
						if (option == JOptionPane.NO_OPTION){
							vp.save.setEnabled(true);
							perform=false;
						}
						else if (option == JOptionPane.YES_OPTION){
						}
				}

				if(perform){
					//////////Evolve without save
					dp.showStatus.setText("Running...");
					da.dp.evolve .setEnabled( false);
					//vp.setEnabled( false);
					evolved = false;

					da.currentYear = 1;

			///running...	
					nd.NetworkDynamix(url, vp.variables);
					
					da.repaint();
					da.dp.setVisible(true);
			/////////////			
			
					da.dp.evolve .setEnabled( false);
					//vp.setEnabled(true);
					vp.network.select(0);
					
					vp.landuse.setEnabled(false);
					vp.edit.setEnabled(false);
					
					
					
					if(networkModified&&!networksaved)vp.save.setEnabled(true);
					
					
					
					vp.restore.setEnabled(false);
					
					vp.sp_coeff.sb.setEnabled(false);
					vp.sp_peakratio.sb.setEnabled(false);
					vp.sp_scaling.sb.setEnabled(false);
					vp.sp_triprate.sb.setEnabled(false);
					vp.sp_vot.sb.setEnabled(false);

					evolved = true;	
			
					da.dp.evolve.setEnabled(true);
					da.dp.statistics .setEnabled( true);
					da.dp.whichAttribute.setEnabled(true) ;
					//da.dp.whichAttribute.select(0);
					da.dp.scale .setEnabled( true);
					dp.showStatus.setText("End of travel demand forecasting. Please select a network to start next experiment.");
					da.repaint();						

				}
		  }

		else if(obj== change){

			boolean attribute1EditSuccess=true;
			boolean attribute2EditSuccess=true;
		  	String msg;
		  	int numLane =-1;
		  	float toll=-1;
		  	int numWorkers=-1;
		  	int numJobs=-1;

		  	
			if(linkselected!=-1){//change the attributes of a link
				nodeselected=-1;
				try{
					msg=textfieldAttribute2.getText();
					
						
					if (msg == null)
					{
						noticeAttributeEdit.setText("Please specify a new toll");
						attribute2EditSuccess = false;
					}else
					{
				  		toll=Float.parseFloat(msg);
				  		//System.out.print("toll= "+toll);
						if (toll>5 || toll<0)
				 		{
							noticeAttributeEdit.setText("Toll should be 0~5 dollars");
							attribute2EditSuccess = false;
				 		}
					}	
			  }catch (NumberFormatException e){
				  noticeAttributeEdit.setText("Toll should be a float");
				  attribute2EditSuccess = false;
			  }
			  
				try{
					msg=textfieldAttribute1.getText();
					if (msg == null)
					{
						noticeAttributeEdit.setText("Please specify number of lanes");
						attribute1EditSuccess = false;
					}else
					{
				  		numLane=Integer.parseInt(msg);
						if (numLane>8 || numLane<1)
				 		{
							noticeAttributeEdit.setText("# lanes should be 1~8");
							attribute1EditSuccess = false;
				 		}
					}	
			  }catch (NumberFormatException e){
				  noticeAttributeEdit.setText("#lanes should be interger");
				  attribute1EditSuccess = false;
			 }

			  
			 if(attribute1EditSuccess && attribute2EditSuccess){
				 nd.dg.link_info[linkselected][6]=1200*numLane;
				 nd.dg.link_info[linkselected][5]=nd.dg.ffs(numLane);
				 
				 int oppolink=nd.dg.oppolink[linkselected]-1;
				 nd.dg.link_info[oppolink][6]=1200*numLane;				 
				 nd.dg.link_info[oppolink][5]=nd.dg.ffs(numLane);
				 
				 nd.dg.link_info[linkselected][8]=toll;
				
				 nd.dg.updateLinkInfo();
				 linkselected=-1;
				 networksaved=false;
				 
				 networkModified=false;
				 a: for (int i=0;i<nd.dg.edges;i++){
					 	//System.out.print(i+"\t"+Math.abs(nd.dg.link_info[i][6]-nd.dg.originalLinkInfo[i][6])+"\n");
						if(Math.abs(nd.dg.link_info[i][6]-nd.dg.originalLinkInfo[i][6])>100 || Math.abs(nd.dg.link_info[i][8]-nd.dg.originalLinkInfo[i][8])>1.0E-6){
							networkModified=true;
							break a;
						}
	 
				 }

				 for (int i=0;i<nd.dg.vertices;i++){
					 for (int j=0;j<nd.dg.NoofLinks(i+1);j++){
						 int linkid=nd.dg.linkID[i][j];
						 nd.dg.Capacity[i].replace(j, (float)nd.dg.link_info[linkid-1][6]);
					 }
				 }
				 fAttributeEdit.setVisible(false);

			 }
			} 
			
			else if (nodeselected!=-1){//change the attributes of a node
				linkselected=-1;
				try{
					msg=textfieldAttribute2.getText();
					if (msg == null)
					{
						noticeAttributeEdit.setText("Please specify number of jobs");
						attribute2EditSuccess = false;
					}else
					{
				  		numJobs=Integer.parseInt(msg);
				  		//System.out.print("toll= "+toll);
						if (numJobs>10000 || numJobs<0)
				 		{
							noticeAttributeEdit.setText("# workers should be 0~10,000");
							attribute2EditSuccess = false;
				 		}
					}	
			  }catch (NumberFormatException e){
				  noticeAttributeEdit.setText("# jobs should be an integer");
				  attribute2EditSuccess = false;
			  }
			  
				try{
					msg=textfieldAttribute1.getText();
					if (msg == null)
					{
						noticeAttributeEdit.setText("Please specify number of wokers");
					  attribute1EditSuccess = false;
					}else
					{
				  		numWorkers=Integer.parseInt(msg);
						if (numWorkers>10000 || numWorkers<0)
				 		{
							noticeAttributeEdit.setText("# workers should be 0~10,000");
							attribute1EditSuccess = false;
				 		}
					}	
			  }catch (NumberFormatException e){
				  noticeAttributeEdit.setText("# workers should be interger");
				  attribute1EditSuccess = false;
			 }

			 System.out.print("workers= "+numWorkers+"\n");
			  
			 if(attribute1EditSuccess && attribute2EditSuccess){
				 nd.dg.node_info[nodeselected][3]=numWorkers;
				 nd.dg.node_info[nodeselected][4]=numJobs;
				
				 nodeselected=-1;
				 
				 networksaved=false;
				 
				 networkModified=false;
				 b: for (int i=0;i<nd.dg.vertices;i++){
					 	//System.out.print(i+"\t"+Math.abs(nd.dg.link_info[i][6]-nd.dg.originalLinkInfo[i][6])+"\n");
						if(Math.abs(nd.dg.node_info[i][3]-nd.dg.originalNodeInfo[i][3])>0 || Math.abs(nd.dg.node_info[i][4]-nd.dg.originalNodeInfo[i][4])>0){
							networkModified=true;
							break b;
						}
	 
				 }
				 fAttributeEdit.setVisible(false);

				 //vp.landuse.setEnabled(false);
			 } 
			}

			 
			 vp.edit.setEnabled(true);
			 vp.save.setEnabled(true);
			 da.setEnabled(true);
			 da.repaint();
		}


		
		else if(obj==cancel){
			linkselected=-1;
			nodeselected=-1;
			fAttributeEdit.setVisible(false);
			vp.edit.setEnabled(false);
			vp.save.setEnabled(false);
			da.setEnabled(true);
			da.repaint();
		}

		else if(arg=="Quit"){
			menuframe.dispose() ;
		}

		else if(arg=="Instructions"){
			//url=getClass().getResource(currentInputFile);
			try {
			helpurl=new URL(getCodeBase(),"SAND1.0HelpFile.htm"); }

			catch (MalformedURLException e) {
			System.out.println("Bad URL:" + helpurl);
			}

			getAppletContext().showDocument(helpurl,"_blank");
		}

		else if(arg=="About SAND1.0"){
			//System.out.print("hereh!\n");
			JOptionPane jp=new JOptionPane();
			jp.showMessageDialog(null, "Simulator and Analyst of Network Design (SAND1.0) is developed by\n Feng Xie and powered by NEXUS research group.","About SAND1.0",JOptionPane.INFORMATION_MESSAGE);
			//js.setPreferredSize(300,300);
			//js.set
			//js.setVisible(true);
//			try {
//				helpurl=new URL(url,"HelpFileSAND1.0.htm"); }
//
//			catch (MalformedURLException e) {
//			System.out.println("Bad URL:" + helpurl);
//			}
//
//			getAppletContext().showDocument(helpurl,"_blank");
		}

			if(arg=="Save Statistics to File"){
				FileDialog savefile=new FileDialog(fStat,"Save Statistics...",FileDialog.SAVE);
				savefile.setVisible(true) ;

				FileOutputStream out= null;
				File saveS= new File(savefile.getDirectory(),savefile.getFile()  );
				
				boolean success=false;		
				if (saveS.exists())
					{
						int option = JOptionPane.showConfirmDialog(null,"File exists, overwrite it?","Warning",JOptionPane.YES_NO_OPTION);
						if (option == JOptionPane.YES_OPTION)
						{
							success = true;							
						}else{
							success = false;
						}
					}
				else
					{
						success = true;
					}
				if (success){
					try{
						out= new FileOutputStream(saveS);
					}catch(Exception e) {
						System.out.println("Unable to open file");
						return;
					}
					PrintStream psOut=new PrintStream(out);
					psOut.print(stat.getText());//
				}
			}

			if(arg=="Close"){
				fStat.dispose()  ;
			}

// Command Evolve
		if(obj.equals(dp.evolve )){
			System.out.print("Network modified: "+networkModified+"; Network saved: "+networksaved+"\n");
			boolean perform=true;
			
			for (int i=0;i<nd.dg.edges;i++){
				nd.dg.link_info[i][7]=0;
			}
			nd.dg.updateLinkInfo();
			
			
			if (networkModified && !networksaved)
			{
				//////////////Remind people to save before evolve
				int option = JOptionPane.showConfirmDialog(menuframe,"You may not retrive your editing work next time.\nDo you want to proceed without saving the network?","Network modified but not saved!",JOptionPane.YES_NO_OPTION);
					if (option == JOptionPane.NO_OPTION){
						vp.save.setEnabled(true);
						perform=false;
					}
					else if (option == JOptionPane.YES_OPTION){
					}
			}

			if(perform){
				//////////Evolve without save
				dp.showStatus.setText("Running...");
				da.dp.evolve .setEnabled( false);
				//vp.setEnabled( false);
				evolved = false;

				da.currentYear = 1;

		///running...	
				nd.NetworkDynamix(url, vp.variables);
				
				da.repaint();
				da.dp.setVisible(true);
		/////////////			
		
				da.dp.evolve .setEnabled( false);
				//vp.setEnabled(true);
				vp.network.select(0);
				
				vp.landuse.setEnabled(false);
				vp.edit.setEnabled(false);
				
				
				
				if(networkModified&&!networksaved)vp.save.setEnabled(true);
				
				
				
				vp.restore.setEnabled(false);
				
				vp.sp_coeff.sb.setEnabled(false);
				vp.sp_peakratio.sb.setEnabled(false);
				vp.sp_scaling.sb.setEnabled(false);
				vp.sp_triprate.sb.setEnabled(false);
				vp.sp_vot.sb.setEnabled(false);

				evolved = true;	
		
				da.dp.evolve.setEnabled(true);
				da.dp.statistics .setEnabled( true);
				da.dp.whichAttribute.setEnabled(true) ;
				//da.dp.whichAttribute.select(0);
				da.dp.scale .setEnabled( true);
				dp.showStatus.setText("End of travel demand forecasting. Please select a network to start next experiment.");
				da.repaint();						

			}
		}

//Command Statistics
		if(obj.equals(dp.statistics)){
			//JOptionPane.showMessageDialog(menuframe,"The Moe's Results: avgSpeed="+nd.avgSpeed+"; avgVolume="+nd.avgVolume+"; vkt="+nd.vkt+"; vht="+nd.vht);
			stat.setText( "");

			stat.append(new String("\n\n---Network Summary---\n\n"));

			stat.append(new String("Item Description\tValue\n\n"));

			stat.append(new String("0.  Network Type       \t"+getnetwork +"\n"));
			stat.append(new String("1.  Land use Distribution     \t"+vp.landuse.getSelectedItem() +"\n"));
			stat.append(new String("3.  Travel Demand Model\t"+"\n"));
			stat.append(new String("3.1 Auto Trip Rate            \t"+Formatter.format(vp.sp_triprate.value()) +"\n"));
			stat.append(new String("3.2 Value of Time             \t"+Formatter.format(vp.sp_vot.value()) +"\n"));
			stat.append(new String("3.3 Friction Factor           \t"+Formatter.format(vp.sp_coeff.value())+"\n"));
			stat.append(new String("3.4 Scaling Factor            \t"+Formatter.format(vp.sp_scaling.value())+"\n"));
			stat.append(new String("3.5 Peak Traffic Ratio        \t"+Formatter.format(vp.sp_peakratio.value())+"\n"));

			stat.append(new String("\n---MOEs Results---\n\n"));

			stat.append(new String("  MOE       Value\n\n"));

			stat.setFont(new Font("Times New Roman",Font.PLAIN|Font.ROMAN_BASELINE |Font.BOLD ,12));
			stat.append(new String("Trips generated in peak hour\t\t"+(int)nd.peakTripsProduced +"\n"));
			stat.append(new String("Average trip length (km)\t\t"+Formatter.format(nd.aver_trip_length)  +"\n"));
			stat.append(new String("Average trip duration (min)\t\t"+Formatter.format(nd.aver_trip_time*60)  +"\n"));
			stat.append(new String("Daily vehicle.kilometers of travel\t\t"+Formatter.format(nd.daily_vkt) +"\n"));
			stat.append(new String("Daily vehicle.hour of travel\t\t"+Formatter.format(nd.daily_vht) +"\n"));
			stat.append(new String("Accessibility to jobs\t\t"+Formatter.format(nd.access_jobs) +"\n"));
			stat.append(new String("Total lane.kilometers\t\t"+Formatter.format(nd.totallanekilo) +"\n"));
			stat.append(new String("Lane.kilometers added\t\t"+Formatter.format(nd.lanekiloadded)  +"\n"));
			//stat.append(new String("Daily toll revenue ($)\t\t"+Formatter.format(nd.daily_toll)  +"\n"));

			stat.setVisible( true);
			fStat.setVisible( true);

		}


	}

	public void itemStateChanged( ItemEvent ie) {
		String arg = (String) ie.getItem();
		Object obj = ie.getSource();
		if (obj.equals(dp.whichAttribute)){
				
				if(dp.scale .getSelectedItem() =="Absolute"){
					if(arg.equals( "Lanes")){
						int a=1;
						drawAttributes = 0;
						da.dp.unit.setText("");
						da.dp.bluefor.setText(Integer.toString(a));
						da.dp.greenfor.setText(Integer.toString(2*a));
						da.dp.yellowfor.setText(Integer.toString(3*a));
						da.dp.orangefor.setText(Integer.toString(4*a));
						da.dp.redfor.setText(Integer.toString(5*a)+"~");
						da.repaint();
					}
					else if(arg.equals( "Volume")){
						int a=1200;
						drawAttributes = 1;
						dp.unit.setText("");
						dp.bluefor.setText("0~" + a);
						dp.greenfor.setText(a +"~" + 2*a);
						dp.yellowfor.setText(2*a+"~" + 3*a);
						dp.orangefor.setText(3*a +"~"+ 4*a);
						dp.redfor.setText(4*a +"~"+ "  ");
						da.repaint();
					}

					else if(arg.equals( "V/C Ratio")){
						double a0=0.80;
						double a1=0.1;
						drawAttributes = 2;
						dp.unit.setText("");
						dp.bluefor.setText(0+"~" + Formatter.format(a0));
						dp.greenfor.setText(Formatter.format(a0) +"~" + Formatter.format(a0+a1));
						dp.yellowfor.setText(Formatter.format(a0+a1)+"~" + Formatter.format(a0+2*a1));
						dp.orangefor.setText(Formatter.format(a0+2*a1) +"~"+ Formatter.format(a0+3*a1));
						dp.redfor.setText(Formatter.format(a0+3*a1) +"~"+ "  ");
						da.repaint();
					}

				}

				else{
					if(arg.equals( "Lanes")){
						drawAttributes = 0;
					}
					else if (arg.equals( "Volume")){
						drawAttributes = 1;
					}
					else if (arg.equals( "V/C Ratio")){
						drawAttributes = 2;
					}

					dp.unit.setText("");
					da.repaint();

				}
		}

		else if (obj.equals(dp.scale)){
			if(arg.equals( "Relative")){
				dp.unit.setText("");
				dp.bluefor.setText("Lowest");
				dp.greenfor.setText("Lower");
				dp.yellowfor.setText("Middle");
				dp.orangefor.setText("Higher");
				dp.redfor.setText("Highest");
				da.repaint() ;
			}
			else if(arg.equals( "Absolute")){
				if(dp.whichAttribute.getSelectedIndex()==0){
					int a=1;
					drawAttributes = 0;
					da.dp.unit.setText("");
					da.dp.bluefor.setText(Integer.toString(a));
					da.dp.greenfor.setText(Integer.toString(2*a));
					da.dp.yellowfor.setText(Integer.toString(3*a));
					da.dp.orangefor.setText(Integer.toString(4*a));
					da.dp.redfor.setText(Integer.toString(5*a)+"~");
					da.repaint();
				}
				else if(dp.whichAttribute.getSelectedIndex()==1){
					int a=1200;
					drawAttributes = 1;
					dp.unit.setText("");
					dp.bluefor.setText("0~" + a);
					dp.greenfor.setText(a +"~" + 2*a);
					dp.yellowfor.setText(2*a+"~" + 3*a);
					dp.orangefor.setText(3*a +"~"+ 4*a);
					dp.redfor.setText(4*a +"~"+ "  ");
					da.repaint();
				}

				else if(dp.whichAttribute.getSelectedIndex()==2){
					double a0=0.80;
					double a1=0.1;
					drawAttributes = 2;
					dp.unit.setText("");
					dp.bluefor.setText(0+"~" + Formatter.format(a0));
					dp.greenfor.setText(Formatter.format(a0) +"~" + Formatter.format(a0+a1));
					dp.yellowfor.setText(Formatter.format(a0+a1)+"~" + Formatter.format(a0+2*a1));
					dp.orangefor.setText(Formatter.format(a0+2*a1) +"~"+ Formatter.format(a0+3*a1));
					dp.redfor.setText(Formatter.format(a0+3*a1) +"~"+ "  ");
					da.repaint();
				}
			}



		}
	}


///	total 23 variable are allocated to get the parameters of models
/// some of them are 'visible' in the interface
/// the others are 'invisible' and are fixed by default
/// this method is used to give the values of some 'invisible' variables
	public void writeVariables(){
	//total 23 variables in vp.variables[]

//from pull-down boxes
	//0
	if(vp.landuse.getSelectedItem() .equals(vp.landuse)){

		vp.variables[0]=vp.landuse.getSelectedIndex();

	}


///from scroll bars
	
//	triprate=vars[1];
//	vot=vars[2];
//	coeff=vars[3];
//	theta=vars[4];
//	peakRatio=vars[5];

	vp.variables[1]=(float)vp.sp_triprate.value();
	vp.variables[2]=(float)vp.sp_vot.value();
	vp.variables[3]=(float)vp.sp_coeff.value()*60;
	vp.variables[4]=(float)vp.sp_scaling.value()*60;
	vp.variables[5]=(float)vp.sp_peakratio.value();
	
	nd.dg.landusetype=(int)vp.variables[0];
	nd.dg.triprate=vp.variables[1];
	nd.dg.vot=(double)vp.variables[2];
	nd.dg.coeff=(double)vp.variables[3];
	nd.dg.theta=(double)vp.variables[4];
	nd.dg.peakratio=(double)vp.variables[5];
	}

	
	public void rewriteLandUse(){
		
		int landusetype=(int)vp.landuse.getSelectedIndex();
		float aver=nd.aver;
		double downtown_x0=-1;
		double downtown_y0=-1;
		int downtownnodeID=-1;

		if (landusetype==0){
			for (int i=0;i<nd.dg.vertices;i++){
				nd.dg.node_info[i][3]=nd.dg.node_info[i][4]=aver;
			}
			
			System.out.print("\tUniform distributed workers and jobs allocated.\n");
		}
		else if (landusetype==1){
			float randoms[]=new float[2000];
			randoms=nd.dg.randomNumbers(url);
			double total1=0;
			double total2=0;
			int index=0;
			for (int i=0;i<nd.dg.vertices;i++){
				nd.dg.node_info[i][3] = randoms[index]*aver;
				total1+=nd.dg.node_info[i][3];
				index++;
				nd.dg.node_info[i][4] = randoms[index]*aver;  
				total2+=nd.dg.node_info[i][4];
				index++;
				
			}
			
			double factor1=aver*nd.dg.vertices/total1;
			double factor2=aver*nd.dg.vertices/total2;
			for (int i=0;i<nd.dg.vertices;i++){
				nd.dg.node_info[i][3] *=factor1; 
				nd.dg.node_info[i][4] *=factor2; 
			}

			System.out.print("\tRandomly distributed workers and jobs allocated.\n");
		}
		else if(landusetype==2){
			double decline_jobs=0.12,decline_workers=0.06;
			//later the two coefficients should be related to scale
			downtownnodeID=42;
			
			if(nd.dg.vertices==56){
				downtown_x0=nd.dg.XCoordinate(downtownnodeID);
				
				downtown_y0=nd.dg.YCoordinate(downtownnodeID);

			}
			else{
				downtown_x0=(nd.dg.maxX()+nd.dg.minX())/2;
				
				downtown_y0=(nd.dg.maxY()+nd.dg.minY())/2;

			}
			
			double total1=0,total2=0;
			for (int i=0;i<nd.dg.vertices;i++){
				double distance=0.2*Math.sqrt(Math.pow(nd.dg.XCoordinate(i+1)-downtown_x0, 2)+Math.pow(nd.dg.YCoordinate(i+1)-downtown_y0, 2));
				nd.dg.node_info[i][4] = Math.exp(-decline_jobs*distance); 
				total1+=nd.dg.node_info[i][4];
				nd.dg.node_info[i][3] = Math.exp(decline_workers*distance); 
				total2+=nd.dg.node_info[i][3];
			}
			
			double factor1=aver*nd.dg.vertices/total1;
			double factor2=aver*nd.dg.vertices/total2;
			
			for (int i=0;i<nd.dg.vertices;i++){
				nd.dg.node_info[i][4] *=factor1; 
				nd.dg.node_info[i][3] *=factor2; 
			}
			System.out.print("\tBell-shaped distributed workers and jobs allocated.\n");
		}

	}
	
	class DrawPanel extends Panel {

		Demo sd;

		Panel legend=new Panel();
		Panel button=new Panel();;
		Panel status=new Panel();;
///////////////////////////////////////////////
		Choice whichAttribute = new Choice ();
		Choice scale = new Choice ();
		//Button help=new Button("Help");
		Label blank=new Label("    ");
		Button evolve = new Button("Evolve");

		Button statistics=new Button("Statistics");

////////////////////////////////////////////////

		Label unit=new Label("");

		Label blue=new Label("    ");
		Label green=new Label("    ");
		Label yellow=new Label("    ");
		Label orange=new Label("    ");
		Label red=new Label("    ");

		Label bluefor=new Label("            ");
		Label greenfor=new Label("            ");
		Label yellowfor=new Label("            ");
		Label orangefor=new Label("            ");
		Label redfor=new Label("            ");

////////////////////////////////////////////////
		Label showStatus=new Label("");


		public DrawPanel( Demo sd) {
			showStatus.setFont(new Font("",Font.BOLD,12));
			this.sd = sd;
			setLayout(new BorderLayout());

//			button panel
			whichAttribute.addItem("Lanes");
			whichAttribute.addItem("Volume");
			whichAttribute.addItem("V/C Ratio");
			whichAttribute.select(0);
			drawAttributes=0;
			whichAttribute.addItemListener(this.sd);

			
			scale.addItem("Absolute");
			scale.addItem("Relative");
			scale.select(0);
			scale.addItemListener(this.sd);



			evolve.addActionListener(this.sd);
			statistics.addActionListener( this.sd);


			evolve.setEnabled(false);
			statistics.setEnabled(false);
			scale.setEnabled( false);
			whichAttribute.setEnabled(false) ;



			button.add(evolve);
			button.add(blank);
			button.add(scale);
			button.add(whichAttribute);
			button.add(new Label("   "));
			button.add(statistics);


			add(button,"South");

//			legend panel
			legend.setLayout( new GridLayout(1,11));


			blue.setBackground(new Color(60, 100, 250));
			legend.add(blue);
			legend.add(bluefor);

			green.setBackground(new Color(8, 140, 14));
			legend.add(green);
			legend.add(greenfor);

			yellow.setBackground(Color.YELLOW );
			legend.add(yellow);
			legend.add(yellowfor);

			orange.setBackground(new Color(250, 125, 0));
			legend.add(orange);
			legend.add(orangefor);


			red.setBackground(new Color(200, 20, 20));
			legend.add(red);
			legend.add(redfor);


			if (scale.getSelectedItem() =="Absolute"){
				int a=0;
				if( whichAttribute.getSelectedIndex() ==0){
					a=1;
					drawAttributes = 0;
					
					drawAttributes = 0;
					unit.setText("");
					bluefor.setText(Integer.toString(a));
					greenfor.setText(Integer.toString(2*a));
					yellowfor.setText(Integer.toString(3*a));
					orangefor.setText(Integer.toString(4*a));
					redfor.setText(Integer.toString(5*a)+"~");

				}
				else if( whichAttribute.getSelectedIndex() ==1){
					a=1200;
					drawAttributes = 1;
					unit.setText("");
					bluefor.setText("0~" + a);
					greenfor.setText(a +"~" + 2*a);
					yellowfor.setText(2*a+"~" + 3*a);
					orangefor.setText(3*a +"~"+ 4*a);
					redfor.setText(4*a +"~"+ "  ");

				}

				else if( whichAttribute.getSelectedIndex() ==2){
					double a0=0.80;
					double a1=0.1;
					drawAttributes = 2;
					unit.setText("");
					bluefor.setText(0+"~" + Formatter.format(a0));
					greenfor.setText(Formatter.format(a0) +"~" + Formatter.format(a0+a1));
					yellowfor.setText(Formatter.format(a0+a1)+"~" + Formatter.format(a0+2*a1));
					orangefor.setText(Formatter.format(a0+2*a1) +"~"+ Formatter.format(a0+3*a1));
					redfor.setText(Formatter.format(a0+3*a1) +"~"+ "  ");
				}
				repaint();

			}
			else{

				unit.setText("");
				bluefor.setText("Lowest");
				greenfor.setText("Lower");
				yellowfor.setText("Middle");
				orangefor.setText("Higher");
				redfor.setText("Highest");
				repaint() ;

			}



			add(legend,"North");
		



//			status	panel
			status.setLayout( new GridLayout(1,1));
			status.add(showStatus);
			add(status,"Center");


		}
	}

	class DrawArea extends Panel implements MouseListener {


		DrawPanel dp;
		Demo dm;
//		Panel editButtonPanel = new Panel();

		int Scale;	// Scale of magnification or diminision; scale=dim/Max
		int Trans;	// translation
		int dim;      // size of the DrawArea,, which is equal to the number of pixes of the draw area
		int radius;   //Radius of circle that represents a node
		Dimension d;  //Current Dimension of the DrawArea (dynamic variable)
		Dimension sd;
		int Max;   // Maximum number of cells
//		boolean mouseclicked;
		int mouseX;
		int mouseY;
		
		Frame fPopup;
		boolean popupShow;
		
		boolean shift;
		
		int n;
		int currentYear = 0;

		float c1,c2,c3,c4; //used to decide which color to use

		public DrawArea(DrawPanel dp) {
			addMouseListener(this);
			this.dp = dp;
			this.dm = dm;
			
			setLayout(new BorderLayout() );
			add("South", dp );

		}


		void setMapVariables() {

			Max =5;
			int maxX=-1;
			int maxY=-1;
			for (int i=0;i<nd.dg.vertices;i++)
			{
				if (nd.dg.node_info[i][1]>maxX)
				{
					maxX=(int)nd.dg.node_info[i][1];
				}
				if (nd.dg.node_info[i][2]>maxY)
				{
					maxY=(int)nd.dg.node_info[i][2];
				}
			}
			if (maxX+4>Max)
			{
				Max=maxX+4;
			}
			if (maxY+4>Max)
			{
				Max = maxY+4;
			}
			
		//System.out.println("Max:"+Max);
		sd = getToolkit().getScreenSize();
		//System.out.print(sd.width +"\t"+sd.height);
		d=getSize() ;

		//System.out.println(" Dimension of the DrawArea: width =  "+d.width + "  height = " + d.height );

		dim = (int)    (      (d.width<d.height) ? (0.90*d.width) : (0.90*d.height)        );

		//System.out.println("dim = "+ dim);

		if(Max != 0){
			Scale = (int)(dim/Max);
		} else {
			System.out.println("From DrawArea class Max variable is 0. Erorr!!!!!");
			Scale = 2;
		}
		if(Scale == 0)
			Scale = 1;

		Trans = (int) (0.05*dim);

		radius = (int) (Scale);

		if(radius == 0)
			radius = 1;
			//System.out.println("Trans = "+Trans+";  radius = "+ radius);
			//System.out.println("End of setScale()!!!!!");

		}


//// network will be drawn for the current year
		private void drawLinks_Speed(Graphics g) {

			float min, max;


//read speed/volume data into the matrix f
			FloatStack  f[] = null;
			drawAttributes=da.dp.whichAttribute.getSelectedIndex();
			if(evolved) {
				if(  drawAttributes==0) f=nd.Capacity;
				else if (drawAttributes==2)
					f = nd.VCR[currentYear];
				else if (drawAttributes==1){
					f = nd.Volume[currentYear];
				}
			} 
			else{
				f = nd.dg.Capacity;
				//?
			}

				
				
			
			float temp = 0;
			min = (float)1.0E10;
			max =  -1 ;
			for(int i=0; i<nd.dg.Vertices(); i++) {
				for(int j=0; j<nd.dg.NoofLinks(i+1); j++) {
					temp = f[i].access(j);

					if( max < temp)
						max = temp;
					if( min > temp)
						min = temp;
				}
			}
			//System.out.println ("max="+max+"; min="+min+"\n");


			int xcoord[] = new int[5];
			int ycoord[] = new int[5];
			float factor;

			for(int i =0; i<nd.dg.Vertices(); i++) {
				for(int j=0; j<nd.dg.NoofLinks(i+1); j++) {
					factor  = (float)(0.5*f[i].access(j) );
					int startx, starty, endx, endy;
					startx = Trans+(int)(Scale*2) + (int)((nd.dg.XCoordinate(i+1)-1)*Scale);
					starty =   Trans- (int)(Scale*2)+ (int)(Scale*Max) - (int)((nd.dg.YCoordinate(i+1)-1)*Scale);
					int k = nd.dg.EndNodeNumbers(i+1, j+1);
					endx = Trans+(int)(Scale*2) + (int)((nd.dg.XCoordinate(k)-1)*Scale);
					endy =  Trans - (int)(Scale*2)+(int) (Scale* Max) - (int)((nd.dg.YCoordinate(k)-1)*Scale);

					int linkid=nd.dg.linkID[i][j];
					int numLanes=(int)nd.dg.link_info[linkid-1][6]/1200;
					
					if(numLanes == 1)
					{
						factor = (float)(0.30*Scale);						
					}else if(numLanes == 2)
					{
						factor = (float)(0.50*Scale);
					}else if(numLanes == 3)
					{
						factor = (float)(0.75*Scale);
					}else if(numLanes >= 4)
					{
						factor = (float)(1.0*Scale);
					}else
					{
						factor = (float)(0.5*Scale);
					}
	
				if (dp.scale .getSelectedItem() =="Absolute"){
					///absolute scale
				    //if(drawAttributes==0){c1=1;c2=2;c3=3;c4=4;}
					if(drawAttributes==2)
						{c1=(float)0.8;c2=(float)0.9;c3=(float)1.0;c4=(float)1.1;}
					else
						{c1=1200;c2=2400;c3=3600;c4=4800;}

					if(nd.dg.linkID[i][j]==linkselected+1){
						g.setColor(Color.MAGENTA);  /////Blue
					}
					else if( f[i].access( j ) <=  c1  ) {
						g.setColor(new Color(60, 100, 250) );  /////Blue
						//g.setColor(new Color(150, 150, 150) );
						//factor = (float) (0.5*Scale);
						//count1++;
					}
					else if ( f[i].access( j ) <=  c2  ) {
						g.setColor(new Color(8, 140, 14) );   ////Green
						//g.setColor(new Color( 115, 115, 115) );
						//factor = (float) (0.75*Scale);
						//count2++;
					}
					else if ( f[i].access( j ) <=  c3  ) {
						g.setColor(Color.yellow);    ////// Yellow
						//g.setColor(new Color(70, 70, 70) );
						//factor = (float) (Scale);
						//count3++;
					}
					else if ( f[i].access( j ) <=  c4  ) {
						g.setColor(new Color(250, 125, 0));    ////// Oringe
						//g.setColor(new Color(70, 70, 70) );
						//factor = (float) (Scale);
						//count3++;
					}
					else {
						g.setColor(new Color (200, 20, 20) );   //// Red
						//g.setColor(new Color(25, 25, 25) );
						//factor = (float) (1.25*Scale);
						//count4++;
					}

				}
				else{
					////relative scale
							float step = (max-min)/5;
							if(nd.dg.linkID[i][j]==linkselected+1){
								g.setColor(Color.MAGENTA);  
							}

							else if( f[i].access( j ) <=  min+step  ) {
							   g.setColor(new Color(60, 100, 250) );  /////Blue
							   //g.setColor(new Color(150, 150, 150) );sc
							   //factor = (float) (0.5*Scale);
							   //count1++;
						   }
						   else if ( f[i].access( j ) <=  min+2*step  ) {
							   g.setColor(new Color(8, 140, 14) );   ////Green
							   //g.setColor(new Color( 115, 115, 115) );
							   //factor = (float) (0.75*Scale);
							   //count2++;
						   }
						   else if ( f[i].access( j ) <=  min+3*step  ) {
							   g.setColor(Color.yellow);    ////// Yellow
							   //g.setColor(new Color(70, 70, 70) );
							   //factor = (float) (Scale);
							   //count3++;
						   }
						   else if ( f[i].access( j ) <=  min+4*step  ) {
							   g.setColor(new Color(250, 125, 0));    ////// Oringe
							   //g.setColor(new Color(70, 70, 70) );
							   //factor = (float) (Scale);
							   //count3++;
						   }
						   else {
							   g.setColor(new Color (200, 20, 20) );   //// Red
							   //g.setColor(new Color(25, 25, 25) );
							   //factor = (float) (1.25*Scale);
							   //count4++;
						   }

				}


					int xerror, yerror;
					int x = endx - startx;
					int y = endy - starty;


					xerror = (int) (factor*y/Math.sqrt(x*x+y*y));
					yerror = (int)(-factor*x/Math.sqrt(x*x+y*y));

					int endxadd = endx+xerror, startxadd = startx+xerror;
					int endyadd = endy+yerror, startyadd = starty+yerror;

					xcoord[0] = startx-1;
					xcoord[1] = endx-1;
					xcoord[2] = endxadd;
					xcoord[3] = startxadd;
					xcoord[4] = startx-1;

					ycoord[0] = starty-1;
					ycoord[1] = endy-1;
					ycoord[2] = endyadd;
					ycoord[3] = startyadd;
					ycoord[4] = starty-1;


					g.fillPolygon(xcoord, ycoord, 5);
					g.setColor(Color.white);
					g.drawLine(startx, starty, endx, endy);

				}
			}

			//System.out.println("Current Year = "+currentYear +"*****Count = "+count1 + "  " + count2+ "  " + count3+ "  " + count4);

		}


		private void paintCells(Graphics g) {
			//int noOfLines;
			float sizeofcell;
			int sizeOfGrid;

			g.setColor(new Color(220, 220, 220) );

			sizeofcell = (Scale);
			sizeOfGrid = (int) (Scale * (Max));
			for(int i=1; i<=Max+1; i++) {
				g.drawLine(Trans, sizeOfGrid+(int)(Trans-(i-1)*sizeofcell), Trans+sizeOfGrid, sizeOfGrid+(int) (Trans-(i-1)*sizeofcell) );   /// draw lines parallel to x-axis
				g.drawLine((int)(Trans+(i-1)*sizeofcell),  Trans,  (int)(Trans+(i-1)*sizeofcell),  sizeOfGrid+Trans);
			}

		}

		private void paintDG(Graphics g) {
			////  Draw Speed boxes
			g.setColor(Color.black);
			drawLinks_Speed(g);

			///// Draw Nodes
			for(int i = 0; i< nd.dg.Vertices(); i++) {
				
				if(i==nodeselected)g.setColor(Color.MAGENTA);
				else g.setColor(Color.black);
				//if(i+1==nd.ca.downtownnodeID)radius*=2;
				int newx, newy;
				newx = (int)(Scale*2)+Trans + (int)((nd.dg.XCoordinate(i+1)-1)*Scale);
				newy  = (int)(Scale*Max)-(int)(Scale*2) - (int)((nd.dg.YCoordinate(i+1)-1)*Scale) + Trans;
				g.fillOval(newx-(int)(radius/2) , newy-(int)(radius/2), radius, radius);
			}

		}



		public void paint(Graphics g) {

			if  (graphRead) {
				paintCells(g);
				paintDG(g);
			}

		}

		//Reaction to Mouse Action
		  public void mouseClicked(MouseEvent me)
		  {
		  }
	
		  public void mouseEntered(MouseEvent me)
		  {
		  }
	
		  public void mouseExited (MouseEvent me)
			  {
			  }
		
		  public void mousePressed (MouseEvent me){
//			System.out.println("/////////////////////////Component:"+me.getComponent().getName());
//			System.out.println("/////////////////////////Parent:"+me.getComponent().getParent().getName());
//			System.out.println("/////////////////////////Location:"+me.getComponent().getLocation().getX()+" "+me.getComponent().getLocation().getY());
				setMapVariables();
				int pointerX = me.getX();
				int pointerY = me.getY();
				boolean pointerNode = false;
				boolean pointerLink = false;
				int nodeID = -1;
				int linkOID = -1, linkDID = -1;
				int linkID = -1;
				
				///////Check if click on a Node;
				for (int i=0;i<nd.dg.vertices;i++)
				{
					int newx, newy;
					newx = (int)(Scale*2)+Trans + (int)((nd.dg.node_info[i][1]-1)*Scale);
					newy  = (int)(Scale*Max)-(int)(Scale*2) - (int)((nd.dg.node_info[i][2]-1)*Scale) + Trans;
					if ((Math.abs(pointerX-newx)<radius) && (Math.abs(pointerY-newy)<radius) )
					{
						nodeID = i;
						pointerNode = true;
						break;
					}	
				}
				///////Find if click on a Link;
				if (pointerNode == false)
				{
					int startID,endID;
					int startx, starty, endx, endy;
					float factor;
					int xerror, yerror;
					int x,y;
					int startxadd,endxadd,startyadd,endyadd;
					int xcoord[] = new int[5];
					int ycoord[] = new int[5];
					int bStartX,bStartY,bStartXAdd,bStartYAdd,bEndX,bEndY,bEndXAdd,bEndYAdd;
					int tempYLow,tempYAbove,tempXLow,tempXAbove;
		
					for (int i=0;i<nd.dg.Edges();i++)
					{
						startID = (int)nd.dg.link_info[i][2];
						endID = (int)nd.dg.link_info[i][1];
						startx = Trans+(int)(Scale*2) + (int)((nd.dg.node_info[startID-1][1]-1)*Scale);
						starty =   Trans- (int)(Scale*2)+ (int)(Scale*Max) - (int)((nd.dg.node_info[startID-1][2]-1)*Scale);
						endx = Trans+(int)(Scale*2) + (int)((nd.dg.node_info[endID-1][1]-1)*Scale);
						endy =  Trans - (int)(Scale*2)+(int) (Scale* Max) - (int)((nd.dg.node_info[endID-1][2]-1)*Scale);
						
						int numLanes=(int)nd.dg.link_info[i][6]/1200;
						if(numLanes == 1)
						{
							factor = (float)(0.25*Scale);						
						}else if(numLanes == 2)
						{
							factor = (float)(0.50*Scale);
						}else if(numLanes == 3)
						{
							factor = (float)(0.75*Scale);
						}else if(numLanes >= 4)
						{
							factor = (float)(1.0*Scale);
						}else
						{
							factor = (float)(0.5*Scale);
						}
			
						x = endx - startx;
						y = endy - starty;
						xerror = (int) (-factor*y/Math.sqrt(x*x+y*y));
						yerror = (int)(factor*x/Math.sqrt(x*x+y*y));
						endxadd = endx+xerror;
						startxadd = startx+xerror;
						endyadd = endy+yerror;
						startyadd = starty+yerror;
			
						xcoord[0] = startx-1;
						xcoord[1] = endx-1;
						xcoord[2] = endxadd;
						xcoord[3] = startxadd;
						xcoord[4] = startx-1;

						ycoord[0] = starty-1;
						ycoord[1] = endy-1;
						ycoord[2] = endyadd;
						ycoord[3] = startyadd;
						ycoord[4] = starty-1;
			
						bStartX = startx+(int)(radius*x/Math.sqrt(x*x+y*y));
						bEndX = endx-(int)(radius*x/Math.sqrt(x*x+y*y));
						bStartXAdd = startxadd+(int)(radius*x/Math.sqrt(x*x+y*y));
						bEndXAdd = endxadd-(int)(radius*x/Math.sqrt(x*x+y*y));
						bStartY = starty+(int)(radius*y/Math.sqrt(x*x+y*y));
						bEndY = endy-(int)(radius*y/Math.sqrt(x*x+y*y));
						bStartYAdd = startyadd+(int)(radius*y/Math.sqrt(x*x+y*y));
						bEndYAdd = endyadd-(int)(radius*y/Math.sqrt(x*x+y*y));
			
						if (startx==endx)
						{
							if (((bEndY-pointerY)*(pointerY-bStartY)>0) && ((bStartXAdd-pointerX)*(bStartX-pointerX)<0))
							{
								linkOID = startID;
								linkDID = endID;
								linkID = i;
								pointerLink = true;
								//System.out.println("///////////////////////////////////right click// get one///");
								break;
							}
						}else if (starty == endy)
						{
							if (((bStartYAdd-pointerY)*(pointerY-bStartY)>0) && ((bStartX-pointerX)*(bEndX-pointerX)<0))
							{
								linkOID = startID;
								linkDID = endID;
								linkID = i;
								pointerLink = true;
								//System.out.println("///////////////////////////////////right click// get one///");
								break;
							}
						}else
						{
							tempYLow = bStartY + (pointerX-bStartX)*(bEndY-bStartY)/(bEndX-bStartX);
							tempYAbove = bStartYAdd + (pointerX-bStartXAdd)*(bEndYAdd-bStartYAdd)/(bEndXAdd-bStartXAdd);
							tempXLow = bStartX + (pointerY-bStartY)*(bEndX-bStartX)/(bEndY-bStartY);
							tempXAbove = bStartXAdd + (pointerY-bStartYAdd)*(bEndXAdd-bStartXAdd)/(bEndYAdd-bStartYAdd);
							if (((tempYLow-pointerY)*(tempYAbove-pointerY)<0) && ((tempXLow-pointerX)*(tempXAbove-pointerX)<0) && ((bStartX-pointerX)*(bEndXAdd-pointerX)<0))
							{
								linkOID = startID;
								linkDID = endID;
								linkID = i;
								pointerLink = true;
								//System.out.println("///////////////////////////////////right click// get one///");
								break;
							}
						}
					}
				}

			  
			  if (me.getButton() == MouseEvent.BUTTON3){
				//System.out.println("//////////////////////////////Enter Right Clicked");
				////////////PopupMenu
				if (pointerNode)
				{
					PopupMenu mRight;
					MenuItem mIRight;
					mRight = new PopupMenu();
					try{
						mIRight = new MenuItem("Node "+(int)nd.dg.node_info[nodeID][0]);
						mRight.add(mIRight);
						mIRight = new MenuItem("X: "+Formatter.format(nd.dg.node_info[nodeID][1]));
						mRight.add(mIRight);
						mIRight = new MenuItem("Y: "+Formatter.format(nd.dg.node_info[nodeID][2]));
						mRight.add(mIRight);
						mIRight = new MenuItem("Number of Workers: "+(int)nd.dg.node_info[nodeID][3]);
						mRight.add(mIRight);
						mIRight = new MenuItem("Number of Jobs: "+(int)nd.dg.node_info[nodeID][4]);
						mRight.add(mIRight);
						if (evolved)
						{
							mIRight = new MenuItem("Trip Production: "+(int)nd.tgen.TrafficProducedataNode[nodeID]);
							mRight.add(mIRight);
							mIRight = new MenuItem("Trip Attraction: "+(int)nd.tgen.TrafficAttractedtoaNode[nodeID]);
							mRight.add(mIRight);
						}
					}catch (NullPointerException npe)
					{
						System.out.println("////////////////////NullpointerException node not found");
					}
//					System.out.println("/////////////////////////Component:"+me.getComponent().getName());
					da.add(mRight);
					mRight.show(me.getComponent(),me.getX(),me.getY());
				}
				else if (pointerLink){
					PopupMenu mRight;
					MenuItem mIRight;
					mRight = new PopupMenu();
					try{
						mIRight = new MenuItem("Link "+(int)nd.dg.link_info[linkID][0]);
						mRight.add(mIRight);
						mIRight = new MenuItem("Origin Node: "+(int)nd.dg.link_info[linkID][1]);
						mRight.add(mIRight);
						mIRight = new MenuItem("Destination Node: "+(int)nd.dg.link_info[linkID][2]);
						mRight.add(mIRight);
						mIRight = new MenuItem("Number of Lanes:"+(int)nd.dg.link_info[linkID][6]/1200);
						mRight.add(mIRight);
						mIRight = new MenuItem("Capacity: "+(int)nd.dg.link_info[linkID][6]+" (veh/h)");
						mRight.add(mIRight);
						mIRight = new MenuItem("Length: "+Formatter.format(nd.dg.link_info[linkID][4])+" (km)");
						mRight.add(mIRight);
						mIRight = new MenuItem("Free Flow Speed: "+(int)nd.dg.link_info[linkID][5]+" (km/h)");
						mRight.add(mIRight);
						mIRight = new MenuItem("Free Flow Travel Time: "+Formatter.format((float)(60*nd.dg.link_info[linkID][4]/nd.dg.link_info[linkID][5]))+"(mins)");
						mRight.add(mIRight);
						
						mIRight = new MenuItem("Toll: "+Formatter.format(nd.dg.link_info[linkID][8])+" (dollar)");
						mRight.add(mIRight);

						if (evolved)
						{
							mIRight = new MenuItem("Traffic Flow: "+(int)nd.dg.link_info[linkID][7]+"(veh/h)");
							mRight.add(mIRight);
							mIRight = new MenuItem("Volume Capacity Ratio: "+Formatter.format((float)nd.dg.link_info[linkID][7]/nd.dg.link_info[linkID][6]));
							mRight.add(mIRight);
							mIRight = new MenuItem("Congest Travel Time: "+Formatter.format((float)nd.dg.link_info[linkID][9]*60)+" (mins)");
							mRight.add(mIRight);
							mIRight = new MenuItem("Generalized Travel Time: "+Formatter.format((float)nd.dg.link_info[linkID][10]*60)+" (mins)");
							mRight.add(mIRight);

						}
					}catch (NullPointerException npe)
					{
						System.out.println("////////////////////NullpointerException node not found");
					}
//					System.out.println("/////////////////////////Component:"+me.getComponent().getName());
					da.add(mRight);
					mRight.show(me.getComponent(),me.getX(),me.getY());
				}
			  }
				//////////////End of rightclick;
			else if (me.getButton() == MouseEvent.BUTTON1){
					//System.out.println("//////////////////////////////Enter Left Clicked");
					if (pointerNode){
						linkselected=-1;
						nodeselected=nodeID;
						//if(vp.network.getSelectedIndex()!=0)
						vp.edit.setEnabled(true);
					}
					////////////PopupMenu
					else if (pointerLink){
						nodeselected=-1;
						linkselected=linkID;
						//if(vp.network.getSelectedIndex()!=0)
						vp.edit.setEnabled(true);
					}
					else{
						linkselected=-1;
						nodeselected=-1;
						vp.edit.setEnabled(false);
						//vp.save.setEnabled(false);
					}	
					da.repaint();
											

			 }

			
		  }
		
		  public void mouseReleased (MouseEvent me){
		  }

	}


///scrollPanel is used to define the scroll bars embeded in the variablePanel
	class ScrollPanel extends Panel implements AdjustmentListener{

		public double value;
		double maxvalue;
		double minvalue;
		int x;
		int y;
		Label lvalue=new Label("");
		JScrollBar sb=new JScrollBar(JScrollBar.HORIZONTAL,0,1,0,101);

		public ScrollPanel(double minvalue, double maxvalue,double defaultvalue){

		setLayout(new GridBagLayout());
		value =defaultvalue;
		this.maxvalue =maxvalue;
		this.minvalue=minvalue;
		lvalue=new Label(Double.toString(defaultvalue));
		
		sb.setValue ((int)Math.round(100*(defaultvalue-minvalue)/(maxvalue-minvalue)));

		sb.addAdjustmentListener( this);
		}


		public void adjustmentValueChanged(AdjustmentEvent ame){
		Object obj=ame.getSource() ;
		int arg=ame.getAdjustmentType() ;

			if(obj.equals(this.sb)){
				if(arg==AdjustmentEvent.TRACK){
					value=minvalue+(maxvalue-minvalue)*(double)sb.getValue()/100.0;
				}
				else if(arg==AdjustmentEvent.UNIT_INCREMENT){
					value+=(double)(maxvalue-minvalue) /100.0;
					}
				else if(arg==AdjustmentEvent.UNIT_DECREMENT){
					value-=(double)(maxvalue-minvalue) /100.0;
					}
				else if(arg==AdjustmentEvent.BLOCK_INCREMENT){
					value+=(double)(maxvalue-minvalue) /10.0;
					}
				else if(arg==AdjustmentEvent.BLOCK_DECREMENT){
					value-=(double)(maxvalue-minvalue) /10.0;
					}
			}


	/////update corresponding vp.variables[]...			
			value=Math.round(value*100.0)/100.0;
			this.repaint() ;
			lvalue.setText(Double.toString( value));

			writeVariables();

		}

		public float value(){
			return (float)value;
		}

	}


	class VariablesPanel extends Panel implements ActionListener, ItemListener  {

		float variables[] = new float[23];
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints constraints=new GridBagConstraints();
		Label temp,temp2;

		Panel help=new Panel();
		Button forhelp=new Button();
		Button restore=new Button();
		Button edit=new Button();
		Button save=new Button();
		
		Choice network=new Choice();

		Choice landuse=new Choice();


		//ScrollPanel v6,v13,v10,v14,v15,v17,v18,v19,v20,v99,v100;
		ScrollPanel sp_triprate, sp_vot, sp_coeff,sp_scaling, sp_peakratio;
		//// Constructor
		public VariablesPanel() {

			defaultVars();

			//setSize(250,1000);
			setLayout(gbl);

			help.setLayout(new FlowLayout() );
			forhelp=new Button("?");
			forhelp.addActionListener( this);
			forhelp.setFont( new Font("",Font.BOLD ,11));

			help.add(temp=new Label("--Please click"));
			temp.setAlignment( Label.RIGHT );
			temp.setFont(new Font("",Font.PLAIN|Font.ITALIC|Font.BOLD ,11));
			temp.setForeground( Color.black );
			help.add(forhelp);
			help.add(temp2=new Label("for HELP!--"));
			temp2.setFont(new Font("",Font.PLAIN|Font.ITALIC|Font.BOLD ,11));
			temp2.setForeground( Color.black  );

			restore=new Button("Restore");
			restore.addActionListener( this);

			edit=new Button("Edit");
			edit.addActionListener( this);
			edit.setEnabled(false);
			
			save=new Button("Save");
			save.addActionListener( this);
			save.setEnabled(false);
			
			constraints.weightx =1.0;
			constraints.weighty=1.0;

			constraints.anchor=GridBagConstraints.WEST;
			constraints.fill=GridBagConstraints.HORIZONTAL ;
			//addComponent(0,0,4,1,help);

			addComponent(0,1,2,1,temp=new Label("  0. Network Type"));
			temp.setFont(new Font("",Font.BOLD,13));

		//network choice
			network.addItem("[ Select a network below ]");
			network.addItem("5X5 Grid Network");
			network.addItem("10X10 Grid Network");
			network.addItem("River Network");
			network.addItem("River Network with a Closed Bridge");
			network.addItem("Load from File...");
			

			addComponent(2,1,2,1,network);
			network.addItemListener( this);
	
			addComponent(0,2,2,1,temp=new Label("  1. Land Use Distribution"));
			temp.setFont(new Font("",Font.BOLD,13));
			landuse.addItem("Uniform");
			landuse.addItem("Prespecified Random");
			landuse.addItem("Downtown");

			addComponent(2,2,2,1,landuse);
			landuse.select("Uniform");
			landuse.addItemListener(this);

	

			addComponent(0,3,4,1,temp=new Label("  2. Network Editing"));
			temp.setFont(new Font("",Font.BOLD,13));

			//v13=new ScrollPanel(0.5,1.5,1,13);
			addComponent(0,4,1,1,new Label("     2.1 Edit link/node properties"));
			addComponent(2,4,1,1,edit);

			addComponent(0,5,1,1,new Label("     2.2 Save edited network"));
			addComponent(2,5,1,1,save);



		
			constraints.fill=GridBagConstraints.HORIZONTAL ;
			constraints.anchor=GridBagConstraints.WEST;
			addComponent(0,6,2,1,temp=new Label("  3. Travel Demand Model"));
			temp.setFont(new Font("",Font.BOLD,13));
			addComponent(2,6,2,1,temp=new Label(""));

			sp_triprate=new ScrollPanel(0.5,3,1);
			addComponent(0,7,1,1,temp=new Label("     3.1 Peak hour trip rate"));
			addComponent(2,7,2,1,sp_triprate.sb);
			addComponent(1,7,1,1,sp_triprate.lvalue );
			sp_triprate.lvalue .setAlignment( Label.RIGHT );

			sp_vot=new ScrollPanel(5,15,10);
			addComponent(0,8,1,1,new Label("     3.2 Value of time ($/veh.hr)"));
			addComponent(2,8,2,1,sp_vot.sb);
			addComponent(1,8,1,1,sp_vot.lvalue);
			sp_vot.lvalue .setAlignment( Label.RIGHT );

			sp_coeff=new ScrollPanel(0.02,0.10,0.05);
			addComponent(0,9,1,1,new Label("     3.3 Friction factor (/min)"));
			addComponent(2,9,2,1,sp_coeff.sb);
			addComponent(1,9,1,1,sp_coeff.lvalue);
			sp_coeff.lvalue .setAlignment( Label.RIGHT );

			sp_scaling=new ScrollPanel(0.1,1,0.2);
			addComponent(0,10,1,1,new Label("     3.4 Scaling factor (/min)"));
			addComponent(2,10,2,1,sp_scaling.sb);
			addComponent(1,10,1,1,sp_scaling.lvalue);
			sp_scaling.lvalue .setAlignment( Label.RIGHT );

			sp_peakratio=new ScrollPanel(0.1,0.2,0.15);
			addComponent(0,11,1,1,new Label("     3.5 Peak hour traffic ratio"));
			addComponent(2,11,2,1,sp_peakratio.sb);
			addComponent(1,11,1,1,sp_peakratio.lvalue);
			sp_peakratio.lvalue .setAlignment( Label.RIGHT );

			addComponent(0,12,1,1, temp=new Label("  4. Retore Global Variables"));
			temp.setFont(new Font("",Font.BOLD,13));
			addComponent(2,12,1,1,restore);
			addComponent(3,12,1,1,temp=new Label(""));
			addComponent(4,12,1,1,temp=new Label(""));
			
			addComponent(1,13,1,1,temp=new Label(""));
			addComponent(2,13,1,1,temp=new Label(""));
			addComponent(3,13,1,1,temp=new Label(""));
			addComponent(4,13,1,1,temp=new Label(""));

			addComponent(1,14,1,1,temp=new Label(""));
			addComponent(2,14,1,1,temp=new Label(""));
			addComponent(3,14,1,1,temp=new Label(""));
			addComponent(4,14,1,1,temp=new Label(""));

//			constraints.fill=GridBagConstraints.NONE;
//			constraints.anchor=GridBagConstraints.EAST;		
//			addComponent(0,24,1,1,restore);
			
			//addComponent(0,23,2,1,new Label(""));
			//addComponent(2,23,2,1,new Label(""));

			//addComponent(0,23,2,1,new Label(""));
			//addComponent(2,23,2,1,restore);

			//addComponent(0,25,2,1,new Label(""));
			//addComponent(2,25,2,1,new Label(""));



		}

		public void addComponent(int x, int y, int w, int h, Component c)
		{
			constraints.gridx=x;
			constraints.gridy=y;
			constraints.gridwidth=w;
			constraints.gridheight=h;

			gbl.setConstraints( c,constraints);

			add(c);
		}



		void defaultVars() {

			variables[0] = (float) 0;	//land use type
			variables[1] = (float) 1;	//trip rate
			variables[2] = (float) 10;  ///vot
			variables[3] = (float) 0.05;	//coeff
			variables[4] = (float) 0.2;	//theta
			variables[5] = (float) 0.15;   //// peak ratio
			
			
			
			variables[6] = (float) 1.0;		//volue of time
			variables[7] = (float) 1.0;		//tax rate
			variables[8] = (float) 1.0;		//length rate
			variables[9] = (float) 0.0;		//speed rate
			variables[10] = (float) 0.01;	//friction factor
			variables[11] = (float) 1;	//symmetry?
			variables[12] = (float) 1;	//avg speed?
			variables[13] = (float) 1.0; //tax rate(toll rate)
			variables[14] = (float) 1.0; //length
			variables[15] = (float) 0;	//speed
			variables[16] = (float) 365;	//cost rate
			variables[17] = (float) 1.0;	//length coefficient
			variables[18] = (float) 0.75;	//flow coefficient
			variables[19] = (float) 0.75;	// speed coefficient
			variables[20] = (float) 1.0;	//speed reduction factor
			variables[21] = (float) 0;	//X
			variables[22] = (float) 20;	//time period

		}




		public void actionPerformed( ActionEvent ae) {
			String arg=(String) ae.getActionCommand();
			Object obj = ae.getSource();
			
			if(obj==vp.save){
				//da.setEnabled(false);
				
				JFileChooser filesave = new JFileChooser();
				filesave.setVisible(true);
				int returnVal = filesave.showSaveDialog(this);
				if (returnVal == JFileChooser.APPROVE_OPTION)
				{
					File chosenFile = filesave.getSelectedFile();
					String savedFileName = chosenFile.getName();
					boolean success=false;
					if (chosenFile.exists())
					{
						int option1 = JOptionPane.showConfirmDialog(null,"File exists, overwrite it?","Warning",JOptionPane.YES_NO_OPTION);
						if (option1 == JOptionPane.YES_OPTION)
						{
							success = true;							
						}else
						{
							success = false;
						}
					}else
					{
						success = true;
					}
//					
					if (success)
					{
						nd.dg.savefile(chosenFile);
						networksaved=true;
						//da.setEnabled(true);

					}
				}
				
			}

			else if(obj==vp.edit){
				if(linkselected!=-1){
					id.setText("Link ID:");
					attribute1.setText("Number of lanes:");
					attribute2.setText("Toll in dollar:");
					
					selectedid.setText(""+(linkselected+1));
					textfieldAttribute1.setText(""+(int)(nd.dg.link_info[linkselected][6]/1200));
					textfieldAttribute2.setText(""+nd.dg.link_info[linkselected][8]);

				}
				else if(nodeselected!=-1){
					id.setText("Node ID:");
					attribute1.setText("Number of workers:");
					attribute2.setText("Number of jobs:");
					
					selectedid.setText(""+(nodeselected+1));
					textfieldAttribute1.setText(""+(int)(nd.dg.node_info[nodeselected][3]));
					textfieldAttribute2.setText(""+(int)(nd.dg.node_info[nodeselected][4]));

				}

				  
				fAttributeEdit.setVisible(true);
				da.setEnabled(false);
			}


			else if(obj==restore){
				
				vp.landuse.select(0);vp.variables[0]=0;
				
				vp.sp_triprate.value=vp.variables[1]=1;
				vp.sp_triprate.lvalue.setText (Double.toString(1));
				vp.sp_triprate.sb.setValue ((int)Math.round(100*(1-sp_triprate.minvalue)/(sp_triprate.maxvalue-sp_triprate.minvalue)));
				
				 
				
				vp.sp_vot.value=vp.variables[2]=10;
				vp.sp_vot.lvalue.setText (Double.toString(10));
				vp.sp_vot.sb.setValue ((int)Math.round(100*(10-sp_vot.minvalue)/(sp_vot.maxvalue-sp_vot.minvalue)));

				vp.sp_coeff.value=vp.variables[3]=(float)0.05;
				vp.sp_coeff.lvalue.setText (Double.toString(0.05));
				vp.sp_coeff.sb.setValue ((int)Math.round(100*(0.05-sp_coeff.minvalue)/(sp_coeff.maxvalue-sp_coeff.minvalue)));
				//System.out.print(sp_coeff.minvalue+"\t"+sp_coeff.maxvalue+"!!\n");
				
				vp.sp_scaling.value=vp.variables[4]=(float)0.2;
				vp.sp_scaling.lvalue.setText (Double.toString(0.2));
				vp.sp_scaling.sb.setValue ((int)Math.round(100*(0.2-sp_scaling.minvalue)/(sp_scaling.maxvalue-sp_scaling.minvalue)));

				vp.sp_peakratio.value=vp.variables[5]=(float)0.15;
				vp.sp_peakratio.lvalue.setText (Double.toString(0.15));
				vp.sp_peakratio.sb.setValue ((int)Math.round(100*(0.15-sp_peakratio.minvalue)/(sp_peakratio.maxvalue-sp_peakratio.minvalue)));

				vp.repaint();
	//reset right-hand panel	
				da.dp.scale.select( "Absolute");		
				//da.dp.whichAttribute.select (0);
				
				int a=1;
				drawAttributes = 0;
				da.dp.unit.setText("");
				da.dp.bluefor.setText(Integer.toString(a));
				da.dp.greenfor.setText(Integer.toString(2*a));
				da.dp.yellowfor.setText(Integer.toString(3*a));
				da.dp.orangefor.setText(Integer.toString(4*a));
				da.dp.redfor.setText(Integer.toString(5*a)+"~");
				da.repaint();
			
				writeVariables();


			}
		}

		public void itemStateChanged( ItemEvent ie) {
			String arg=(String) ie.getItem();
			Object obj=ie.getSource();

			//reset right-hand panel	
			da.dp.scale.select( "Absolute");		
			da.dp.whichAttribute.select (0);

			int a=1;
			drawAttributes = 0;
			da.dp.unit.setText("");
			da.dp.bluefor.setText(Integer.toString(a));
			da.dp.greenfor.setText(Integer.toString(2*a));
			da.dp.yellowfor.setText(Integer.toString(3*a));
			da.dp.orangefor.setText(Integer.toString(4*a));
			da.dp.redfor.setText(Integer.toString(5*a)+"~");
			
			//all variables in the right-hand panel, except "evolve" are set disabled			
			//da.dp.statistics .setEnabled( false);
			//da.dp.whichAttribute.setEnabled(false) ;
			//da.dp.scale .setEnabled( false);
			//da.dp.evolve .setEnabled( true);


		/////Network
			if(obj.equals(vp.network)){

				
				if(vp.network.getSelectedIndex()==0){
					linkselected=-1;
					vp.sp_coeff.sb.setEnabled(false);
					vp.sp_peakratio.sb.setEnabled(false);
					vp.sp_scaling.sb.setEnabled(false);
					vp.sp_triprate.sb.setEnabled(false);
					vp.sp_vot.sb.setEnabled(false);
					vp.restore.setEnabled(false);
					vp.landuse.setEnabled(false);
					dp.evolve.setEnabled(false) ;
					da.dp.statistics .setEnabled( false);
					da.dp.whichAttribute.setEnabled(false) ;

					//da.dp.statistics .setEnabled(true);

				}
				else{
					boolean redraw=true;
					if(arg.equals("10X10 Grid Network")){
						dp.showStatus.setText("10X10 Grid Network loaded...");
						currentInputFile = "Grid10.txt";
						getnetwork="10X10 Grid Network" ;
						url = getClass().getResource(currentInputFile);
						try {
							nd = new NetworkDynamics( variables,url, currentInputFile);
						} catch (IOException e) {
						}
						networkModified = false;
						networksaved=false;

						da.setMapVariables();
						graphRead = true;
						evolved = false;
						da.currentYear = 0;
						dp.evolve.setEnabled(true) ;
						da.repaint();
					}
					else if(arg.equals("5X5 Grid Network")){
						dp.showStatus.setText("5X5 Grid Network loaded...");
						currentInputFile = "Grid5.txt";
						getnetwork="5X5 Grid Network" ;
						url = getClass().getResource(currentInputFile);
					try {
							nd = new NetworkDynamics( variables, url, currentInputFile);
						} catch (IOException e) {
						}
						networkModified = false;
						networksaved=false;

						da.setMapVariables();
						graphRead = true;
						evolved = false;
						da.currentYear = 0;
						dp.evolve.setEnabled(true) ;
						da.repaint();
					}

					else if(arg.equals("River Network")){
						dp.showStatus.setText("A network accoss river loaded...");
						currentInputFile = "River.txt";
						getnetwork="River Network" ;
						url = getClass().getResource(currentInputFile);
						try {
							nd = new NetworkDynamics( variables,url, currentInputFile);
						} catch (IOException e) {
						}
						
						networkModified = false;
						networksaved=false;

						da.setMapVariables();
						graphRead = true;
						evolved = false;
						da.currentYear = 0;
						dp.evolve.setEnabled(true) ;
						da.repaint();
					}
					
					else if(arg.equals("River Network with a Closed Bridge")){
						dp.showStatus.setText("A network across river with one bridge shut down loaded...");
						currentInputFile = "River1.txt";
						getnetwork="River Network with a Closed Bridge" ;
						url = getClass().getResource(currentInputFile);
						try {
							nd = new NetworkDynamics( variables,url, currentInputFile);
						} catch (IOException e) {
						}
						networkModified = false;
						networksaved=false;

						da.setMapVariables();
						graphRead = true;
						evolved = false;
						da.currentYear = 0;
						dp.evolve.setEnabled(true) ;
						da.repaint();
					}
					else if (arg.equals("Load from File...")){
						vp.landuse.setEnabled(false);
						FileDialog loadfile=new FileDialog(menuframe,"Load network from a specified file...",FileDialog.LOAD);
						loadfile.setVisible(true);
						
						File chosenFile;
						if(loadfile.getFile()==null){
							redraw=false;
							if(currentInputFile=="Grid5.txt")vp.network.select(1);
							else if(currentInputFile=="Grid10.txt")vp.network.select(2);
							else if(currentInputFile=="River.txt")vp.network.select(3);
							else if(currentInputFile=="River1.txt")vp.network.select(4);

						}
						else{
							chosenFile = new File(loadfile.getDirectory(),loadfile.getFile()  );
							String loadFileName = chosenFile.getName();
							dp.showStatus.setText("Network from "+loadFileName+" loaded...");
							
							try {
								url = chosenFile.toURL();
							}catch (MalformedURLException me)
							{
								System.out.println("URL error");
								return;
							}
							
							currentInputFile = loadFileName;
							//url=getClass().getResource(currentInputFile);
							
							getnetwork="Loaded Network" ;
							
							try {
								nd = new NetworkDynamics( variables,url, currentInputFile);
							} catch (IOException e) {
								dp.showStatus.setText("Loading File Error!");
								return;
							}

							networkModified = false;
							networksaved=false;
							da.setMapVariables();
							graphRead = true;
							evolved = false;
							da.currentYear = 0;
							dp.evolve.setEnabled(true) ;
							da.repaint();

						}

					}
					if(redraw){
						linkselected=-1;
						vp.sp_coeff.sb.setEnabled(true);
						vp.sp_peakratio.sb.setEnabled(true);
						vp.sp_scaling.sb.setEnabled(true);
						vp.sp_triprate.sb.setEnabled(true);
						vp.sp_vot.sb.setEnabled(true);
						vp.restore.setEnabled(true);
						vp.landuse.setEnabled(true);
						if(arg.equals("Load from File..."))vp.landuse.setEnabled(false);
						da.setEnabled(true);
						dp.evolve.setEnabled(true) ;
						dp.statistics .setEnabled(false);
						da.dp.whichAttribute.setEnabled(false) ;
						da.dp.scale.setEnabled(false);
						
						rewriteLandUse();
					}
				
				}
			
			}	
			else{

				if(obj.equals(vp.landuse)){

					variables[0]=vp.landuse.getSelectedIndex();
					if (variables[0]==0)dp.showStatus.setText("Each centroid is assigned with 1000 jobs and 1000 workers.");
					else if (variables[0]==1)dp.showStatus.setText("Each centroid is randomly assigned with 0~2000 jobs and 0~2000 workers.");
					else if (variables[0]==2){
						if (vp.network.getSelectedIndex()==1){
							dp.showStatus.setText("Jobs and workers are assigned subject to prespecified bell-shape distributions. Downtown is located on the network center.");
						}
						else if (vp.network.getSelectedIndex()==2){
							dp.showStatus.setText("Jobs and workers are assigned to prespecified bell-shape distributions. Downtown is located on the network center.");
						}
						else if (vp.network.getSelectedIndex()==3 ||vp.network.getSelectedIndex()==4){
							dp.showStatus.setText("Jobs and workers are assigned to prespecified bell-shape distributions. Downtown is located on Node 42 north of the river.");
						}
						else if (vp.network.getSelectedIndex()==5){
							if (nd.dg.vertices==56)dp.showStatus.setText("Jobs and workers are assigned to prespecified bell-shape distributions. Downtown is located on Node 42 north of the river.");
							else dp.showStatus.setText("Jobs and workers are assigned to prespecified bell-shape distributions. Downtown is located on the network center.");
						}

					}
					rewriteLandUse();
				}



	////any changes in any pull-down boxes other than the network pull-down will also rapaint the network
			
			writeVariables();
			
		 }//end of else
			

		}///End of public void ()

	}
	////// End of class VariablesPanel



}

///////  End of Demo Class


