import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;


public class ShootingMoveKey extends JFrame{
	public static int whetherClear = 2;
	public ShootingMoveKey(){
		whetherClear = 2;
		setSize(800,500);
		setTitle("Game Example");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		MyJPanel myJPanel = new MyJPanel();
		Container c = getContentPane();
		c.add(myJPanel);
		setVisible(true);
	}
	public static void main(String[] args) throws Exception{
		BufferedReader reader =  new BufferedReader(new InputStreamReader(System.in));
		String line;
		String apiUrl = "http://localhost:3000/users/create_java";
		String getPost;
		while(true){
			new ShootingMoveKey();
			//ゲームが終わるまで待機
			while(whetherClear == 2){
				Thread.sleep(1000);
			}

			if(whetherClear == 1){
				System.out.println("==Game Clear==");
				System.out.println("Do you want save record? y/n");
				line = reader.readLine();
				if(line.equals("y")){
					JsonData jsonData = new JsonData();
					jsonData.InputJson(reader);
					String sendData = jsonData.MakeJson();
					getPost = callPost(apiUrl,sendData);
				}
			}else{
				System.out.println("==Game End ==");
			}


			System.out.println("Play Again? y/n");
			line = reader.readLine();
			if(!(line.equals("y"))){
				System.out.println("Thank you for playing!!");
				break;
			}else{
				System.out.println("Continue game after 3sec.");
				Thread.sleep(3000);
			}
		}
	}

	public static class JsonData{
		String name;
		String password;
		String time;
		String line;

		void InputJson(BufferedReader reader) throws Exception{
			System.out.print("Input your name: ");
			line = reader.readLine();
			this.name = line;
			System.out.print("Input your password: ");
			line = reader.readLine();
			this.password = line;
			System.out.print("Input your time: ");
			line = reader.readLine();
			this.time = line;
		}
		String MakeJson(){
			String sendData = "{\"name\":\""+name+"\" , \"password\":\""+password+"\" , \"time\":"+time+"}";
			System.out.println("Create data below one.");
			System.out.println(sendData);
			return sendData;
		}
	}



	public static String callPost(String strPostUrl,String formParam){
		HttpURLConnection con = null;
		StringBuffer result = new StringBuffer();

		try{
			URL url = new URL(strPostUrl);
			con = (HttpURLConnection)url.openConnection();

			con.setDoOutput(true);
			con.setRequestMethod("POST");
			con.setRequestProperty("Accept-Language","jp");
			con.setRequestProperty("Content-Type","application/json; charset=utf-8");
			OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
			out.write(formParam);
			out.close();
			con.connect();
			final int status = con.getResponseCode();
			if(status == HttpURLConnection.HTTP_OK){
				final InputStream in = con.getInputStream();
				String encoding = con.getContentEncoding();
				if(null == encoding){
					encoding = "UTF-8";
				}
				final InputStreamReader inReader = new InputStreamReader(in,encoding);
				final BufferedReader bufReader = new BufferedReader(inReader);
				String line = null;

				while((line = bufReader.readLine()) != null){
					result.append(line);
				}
				bufReader.close();
				inReader.close();
				in.close();
			}else{
				System.out.println(status);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(con != null){
				con.disconnect();
			}
		}
		return result.toString();
	}


	public class MyJPanel extends JPanel
	implements ActionListener, MouseListener,
	MouseMotionListener,KeyListener
	{
		int my_x;
		int player_width,player_height;
		int enemy_width,enemy_height;
		int n;
		int enemy_x[];
		int enemy_y[];
		int enemy_move[];
		int enemy_alive[];
		int num_of_alive;
		int my_missile_x[][],my_missile_y[][];
		int missile_flag;
		int num_of_my_missile;
		int enemy_missile_x[];
		int enemy_missile_y[];
		int enemy_missile_move[];
		int enemy_missile_flag[];
		int flag;
		public static final int MY_Y = 400;
		Image image,image2;
		Timer timer;

		public MyJPanel(){
			my_x = 250;
			missile_flag = 0;
			flag = 0;
			num_of_my_missile = 5;
			int i;
			n = 14;
			num_of_alive = n;
			enemy_x = new int[n];
			enemy_y = new int[n];

			enemy_move = new int[n];
			enemy_alive = new int[n];

			my_missile_x = new int[5][num_of_my_missile];
			my_missile_y = new int[5][num_of_my_missile];

			enemy_missile_x = new int[n];
			enemy_missile_y = new int[n];
			enemy_missile_flag = new int[n];
			enemy_missile_move = new int[n];

			for(i=0;i<7;i++){
				enemy_x[i] = 70*(i+1)-50;
				enemy_y[i] = 50;
			}
			for(i=7;i<n;i++){
				enemy_x[i] = 70*(i-5)-50;
				enemy_y[i] = 100;
 			}
			for(i=0;i<n;i++){
				enemy_alive[i] = 1;
				enemy_move[i] = -10;
			}

			for(i=0;i<n;i++){
				enemy_missile_x[i] = enemy_x[i] + enemy_width/2;
				enemy_missile_y[i] = enemy_y[i] + enemy_height;
				enemy_missile_move[i] = 10 + (i%3);
				enemy_missile_flag[i] = 1;
			}

			ImageIcon icon = new ImageIcon("player.jpg");
			image = icon.getImage();

			ImageIcon icon2 = new ImageIcon("enemy.jpg");
			image2 = icon2.getImage();

			player_width = image.getWidth(this);
			player_height= image.getHeight(this);

			enemy_width = image2.getWidth(this);
			enemy_height = image2.getHeight(this);
			whetherClear = 2;
			setBackground(Color.black);
			setFocusable(true);
			addKeyListener(this);
			addMouseListener(this);
			addMouseMotionListener(this);
			timer = new Timer(50, this);
			timer.start();
		}

		public void swapArray(int x[][],int y[][],int k,int point){
			for(int i=k;i<point;i++){
				for(int j=0;j<5;j++){
					x[i][j] = x[i+1][j];
					y[i][j] = y[i+1][j];
				}
			}
		}

		public void paintComponent(Graphics g){
			super.paintComponent(g);
			g.drawImage(image,my_x,400,this);
			for(int i=0;i<n;i++){
				if(enemy_alive[i] == 1){
					g.drawImage(image2,enemy_x[i],enemy_y[i],this);
				}
			}
			g.setColor(Color.white);
			for(int i=0;i<missile_flag;i++){
				for(int j=0;j<num_of_my_missile;j++){
					g.fillRect(my_missile_x[i][j],my_missile_y[i][j],2,5);
				}
			}
			for(int i=0;i<n;i++){
				if(enemy_missile_flag[i] == 1){
					g.setColor(Color.white);
					g.fillRect(enemy_missile_x[i],enemy_missile_y[i],2,5);
				}
			}
		}


		public void actionPerformed(ActionEvent e){
			Dimension dim = getSize();
			for(int i=0;i<n;i++){
				enemy_x[i] += enemy_move[i];
				if( (enemy_x[i]<0) || (enemy_x[i] > (dim.width - enemy_width) ) ){
					enemy_move[i] = -enemy_move[i];
				}
			}

			for(int i=0;i<n;i++){
				if(enemy_missile_flag[i] == 1){
					enemy_missile_y[i] += enemy_missile_move[i];
					if(enemy_missile_y[i] > 500){
						enemy_missile_flag[i] = 0;
					}
				}else{
					if(enemy_alive[i] == 1){
						enemy_missile_x[i] = enemy_x[i] + enemy_width/2;
						enemy_missile_y[i] = enemy_y[i] + enemy_height;
						enemy_missile_flag[i] = 1;
					}
				}
			}

			for(int i=0;i<n;i++){
				if(((enemy_missile_x[i]+2) >= my_x) && (enemy_missile_x[i] < my_x + player_width) && (enemy_missile_y[i] < (MY_Y + player_height)) && ((enemy_missile_y[i]+ 5) >= MY_Y)){
					//========GameEnd=====
					whetherClear = 0;
					timer.stop();
					dispose();
					return;
				}
			}

			if(missile_flag != 0){
				for(int i=0;i<missile_flag;i++){
					for(int j=0;j<num_of_my_missile;j++){
						my_missile_y[i][j] -= 15;
					}
				}
				for(int i=0;i<missile_flag;i++){
					for(int j=0;j<num_of_my_missile;j++){
						if(0 > my_missile_y[i][j] && flag==0){
									missile_flag --;
									swapArray(my_missile_x,my_missile_y,i,missile_flag);
									flag = 1;
						}
					}
					flag = 0;
				}
				//敵機がミサイルに当たった場合の処理
				for(int i=0;i<n;i++){
					if(enemy_alive[i] == 1){
						for(int k=0;k<missile_flag;k++){
							for(int j=0;j<num_of_my_missile;j++){
								if((enemy_x[i] - 2 <= my_missile_x[k][j]) && (enemy_x[i]+enemy_width > my_missile_x[k][j]) && (enemy_y[i] + enemy_height >= my_missile_y[k][j]) && enemy_y[i] < my_missile_y[k][j]+5){
									if(missile_flag>0 && flag == 0){
										enemy_alive[i] = 0;
										missile_flag --;
										swapArray(my_missile_x,my_missile_y,k,missile_flag);
										num_of_alive --;
										flag = 1;
									}
								}
							}
						}
					}
				}
				if(num_of_alive <= 0){
					//=============Game Clear=================
					whetherClear = 1;
					timer.stop();
					dispose();
					return;
				}
			}
			repaint();
		}
		public void mouseClicked(MouseEvent me)
		{ }
		public void mousePressed(MouseEvent me){

		}
		public void mouseReleased(MouseEvent me)
		{ }
		public void mouseExited(MouseEvent me)
		{ }
		public void mouseEntered(MouseEvent me)
		{ }
		public void mouseMoved(MouseEvent me){
			//my_x = me.getX();
		}
		public void mouseDragged(MouseEvent me)
		{ }
		public void keyPressed(KeyEvent e){
			int key = e.getKeyCode();
			switch(key){
				case KeyEvent.VK_RIGHT:
					if(my_x + 10 > 800){
							my_x = my_x + 10 - 800;
					}else{
							my_x = my_x + 10;
					}
					break;
				case KeyEvent.VK_LEFT:
					if(my_x - 10 < 0){
							my_x = my_x - 10 + 800;
					}else{
							my_x = my_x - 10;
					}
					break;
				case KeyEvent.VK_X:
					if(missile_flag < 5){
						for(int i=0;i<num_of_my_missile;i++){
							my_missile_x[missile_flag][i] = -(i-2)*10 + my_x + player_width / 2;
							my_missile_y[missile_flag][i] = MY_Y;
						}
						missile_flag ++;
					}
					break;
			}
		}
		public void keyReleased(KeyEvent e)
		{ }
		public void keyTyped(KeyEvent e)
		{ }
	}
}
