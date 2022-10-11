package Proyecto;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.Action;
import javax.swing.ImageIcon;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.swing.JTextField;

@SuppressWarnings("serial")
public class Interfaz extends JFrame {

	private JPanel contentPane;
	private final Action action = new SwingAction();
	private DataCube d = new DataCube();
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Interfaz frame = new Interfaz();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Crea el frame
	 * Interfaz gráfica
	 */
	public Interfaz() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 697, 502);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JButton btnNewButton = new JButton("Cargar cubo");
		btnNewButton.setAction(action);
		btnNewButton.setBounds(0, 44, 131, 23);
		contentPane.add(btnNewButton);

		/*
		 * Accion del boton de cargar los endmembers
		 */
		
		JButton btnNewButton_1 = new JButton("Cargar Endmembers");
		btnNewButton_1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cargarEndmembers();
			}
		});
		btnNewButton_1.setBounds(0, 115, 131, 23);
		contentPane.add(btnNewButton_1);
		
		Longitud = new JTextField();
		Longitud.setBounds(411, 422, 86, 20);
		contentPane.add(Longitud);
		Longitud.setColumns(10);
		
		/*
		 * Muestra una imagen de una de las bandas de la imagen
		 */
		
		JButton btnNewButton_3 = new JButton("Mostrar banda");
		btnNewButton_3.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				d.mostrarBandas(Longitud.getText());
				JFrame frame = new JFrame();
				frame.getContentPane().setLayout(new FlowLayout());
				frame.getContentPane().add(new JLabel(new ImageIcon(d.getImg())));
				frame.pack();
				frame.setVisible(true);
			}
		});
		
		x = new JTextField();
		x.setColumns(10);
		x.setBounds(141, 422, 44, 20);
		contentPane.add(x);
		btnNewButton_3.setBounds(525, 421, 111, 23);
		contentPane.add(btnNewButton_3);
		
		JButton btnNewButton_2 = new JButton("Montar cubo");
		btnNewButton_2.setBounds(0, 191, 131, 23);
		contentPane.add(btnNewButton_2);

		JPanel panel = new JPanel();
		panel.setBounds(141, 44, 238, 367);
		contentPane.add(panel);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(411, 56, 225, 355);
		contentPane.add(scrollPane);

		textArea = new javax.swing.JTextArea();
		textArea.setColumns(20);
		textArea.setRows(5);
		scrollPane.setViewportView(textArea);

		JLabel lblNewLabel_1 = new JLabel("Longitudes de onda");
		lblNewLabel_1.setBounds(411, 21, 225, 14);
		contentPane.add(lblNewLabel_1);

		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 636, 21);
		contentPane.add(menuBar);

		JMenu menu_2 = new JMenu("Opciones adicionales");
		menuBar.add(menu_2);

		JMenuItem menuItem_6 = new JMenuItem("Minutias");
		menu_2.add(menuItem_6);

		JMenuItem menuItem_7 = new JMenuItem("Angulo");
		menu_2.add(menuItem_7);

		JMenuItem menuItem_8 = new JMenuItem("Auto");
		menu_2.add(menuItem_8);
		
		y = new JTextField();
		y.setColumns(10);
		y.setBounds(215, 422, 43, 20);
		contentPane.add(y);
		
		JLabel lblNewLabel_1_1 = new JLabel("x:");
		lblNewLabel_1_1.setBounds(121, 425, 10, 14);
		contentPane.add(lblNewLabel_1_1);
		
		JLabel lblNewLabel_1_1_1 = new JLabel("y:");
		lblNewLabel_1_1_1.setBounds(197, 425, 18, 14);
		contentPane.add(lblNewLabel_1_1_1);
		
		JButton btnNewButton_3_1 = new JButton("Mostrar gr\u00E1fico");
		btnNewButton_3_1.setBounds(268, 421, 111, 23);
		
		/*
		 * Crea una gráfica de los valores de reflectancia del pixel que le indiques
		 */
		
		btnNewButton_3_1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				Grafica p=new Grafica();
			}
		});
		contentPane.add(btnNewButton_3_1);
	}
	
	/*
	 * Lee el fichero y lo inserta en la posición adecuada dentro del cubo
	 */

	public void cargarEndmembers() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.showOpenDialog(fileChooser);
		FileInputStream fis = null;
		DataInputStream entrada = null;
		double a=0;
		String protocolo = d.getInterleave();
		byte[] bytes;
		byte primero;
		int aux=0;
		int i=0;
		int j=0;
		int banda=0;
		int tipo=-1;
		long inicio = 0,fin=0,fin2=0,fin3=0;
		d.setMenor(999999);
		try {
			String ruta = fileChooser.getSelectedFile().getAbsolutePath();
			fis = new FileInputStream(ruta);
			entrada = new DataInputStream(fis);

			inicio = System.currentTimeMillis();	//Toma inicial de tiempo
			
			tipo=d.getData_type();
			while (true) {
				primero=entrada.readByte();
				switch (tipo) {				//Depende del tipo de datos, realizamos una lectura u otra
				case 1:						//1 byte Unsigned integer
					bytes = new byte[1];
					bytes[0] = primero;
					a = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).get();
					break;
				case 2:						//2 bytes Signed integer
					bytes = new byte[2];
					bytes[0]= primero;
					bytes[1]= entrada.readByte();
					a = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getShort();
					break;
				case 3:						//4 bytes Signed Integer
					bytes = new byte[4];
					bytes[0]= primero;
					bytes[1]= entrada.readByte();
					bytes[2]= entrada.readByte();
					bytes[3]= entrada.readByte();
					a = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getLong();
					break;
				case 4:						//4 bytes Float
					bytes = new byte[4];
					bytes[0]= primero;
					bytes[1]= entrada.readByte();
					bytes[2]= entrada.readByte();
					bytes[3]= entrada.readByte();
					a = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
					break;
				case 5:						//8 bytes double
					bytes = new byte[8];
					bytes[0]= primero;
					bytes[1]= entrada.readByte();
					bytes[2]= entrada.readByte();
					bytes[3]= entrada.readByte();
					bytes[4]= entrada.readByte();
					bytes[5]= entrada.readByte();
					bytes[6]= entrada.readByte();
					bytes[7]= entrada.readByte();
					a = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getDouble();
					break;
				case 12:
					bytes = new byte[2];
					bytes[0]= primero;
					bytes[1]= entrada.readByte();
					a = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
					break;
				case 13:
					//a = entrada.read();
					break;
				case 14:
					a = entrada.readLong();
					break;
				case 15:
					//a = entrada.readun();
					break;
				default:
					System.out.println("Tipo no encontrado");
					break;
				}
				
				if(a>d.getMayor()) {
					d.setMayor(a);
				}else if (a<d.getMenor()){
					d.setMenor(a);
				}
				
				switch(protocolo) {			//Organizar el cubo en función de su formato
				case "bsq":
					if (j==d.getSamples()) {
						i++;
						j=0;
						if(i == d.getLines()) {
							i=0;
							if(banda<d.getBands()-1) {
								banda++;
							}
						}
					}
					break;
				case "bil":
					if (j == d.getSamples()) {
						j=0;
						banda++;
						if (banda==d.getBands()) {
							banda=0;
							if(i<d.getLines()-1) {
								i++;
							}
						}
					}
					break;
				case "bip":
					banda = aux%d.getBands();
					aux++;
					if (banda != 0) {
						j--;
					}
					if (j == d.getSamples()) {
						j=0;
						if(i<d.getLines()-1) 
							i++;
					}
					break;
				default:
					System.out.println("Esquema de bandas desconocido");
					break;
				}
				d.setCubo(i, j, banda, a);
				j++;
			}
			} catch (FileNotFoundException e) {
				System.out.println(e.getMessage());
			} catch (EOFException e) {
				System.out.println("Fin de fichero");
			} catch (IOException e) {
				System.out.println(e.getMessage());
			} finally {							//Termina la lectura del fichero
				try {
					if (fis != null) {
						fis.close();
					}
					if (entrada != null) {
						entrada.close();
					}
				} catch (IOException e) {
					System.out.println(e.getMessage());                                                               
				}
			}
		fin = System.currentTimeMillis();
        double tiempo = (double) ((fin - inicio)/1000);		//Tiempo de lectura
        
        System.out.println("Tiempo de lectura" + tiempo +" segundos");
        textArea.append("Tiempo de lectura" + tiempo +" segundos");
        
		d.normalizar();
		fin2 = System.currentTimeMillis();
		double tiempo2 = (double) ((fin2 - fin)/1000);		//Tiempo de normalización
		
        System.out.println("Tiempo de normalización" + tiempo2 +" segundos");
        textArea.append("Tiempo de normalización" + tiempo2 +" segundos");
		
//		try {
//			d.escribir();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		fin3 = System.currentTimeMillis();
//        double tiempo3 = (double) ((fin3 - fin2)/1000);
//        
//        System.out.println("Tiempo de escritura" + tiempo3 +" segundos");
//        textArea.append("Tiempo de escritura" + tiempo3 +" segundos");
		}


	private class SwingAction extends AbstractAction {
		public SwingAction() {
			putValue(NAME, "Cargar cubo");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}
		
		/*
		 * Carga y muestra toda la información sobre el cubo
		 */

		public void actionPerformed(ActionEvent e) {
			d.cargar();
			d.mostrarCubo();
			ArrayList<String> longitudes = d.getLongitudes();

			for (int i = 0; i < longitudes.size(); i++) {
				String longitud = longitudes.get(i);
				textArea.append(longitud + "\n");
				if (!longitud.equals("}")) {
					System.out.print(longitud + " ");
					if (!d.getUnits().equals("Unknown"))
						System.out.println(d.getUnits());
				}
					System.out.println();
			}
		}
	}
	
	public class Grafica extends Frame {
	    public Grafica() {
	    	this.setTitle("Gráfica");
	        this.setSize(d.getBands() + 50, 200);
	        this.setVisible(true);
	    }

		/*
		 * Metodo encargado de pintar la gráfica
		 */
	    
	    @Override
	    public void paint(Graphics g) {
	        g.setColor(Color.BLACK);
	        g.drawLine(25, 150, 25+d.getBands(), 150);		//Horizontal
	        g.drawLine(25, 50, 25, 150);					//Vertical
	        g.setColor(Color.GREEN);
	        g.drawLine(20, 50, 30, 50);						//1 Vertical
	        g.drawLine(20, 100, 30, 100);					//0.5 Vertical
	        g.drawLine(25+d.getBands()/2, 145, 25+d.getBands()/2, 155);
	        g.drawLine(25+d.getBands(), 145, 25+d.getBands(), 155);
	        
	        g.setColor(Color.RED);
	        double aux, aux2;
	        for (int i=0; i<d.getBands()-1; i++) {
	        	aux=d.getcubo(Integer.parseInt(x.getText()), Integer.parseInt(y.getText()), i);
	        	aux=(1-aux)*100;
	        	aux=aux+50;
	        	
	        	aux2=d.getcubo(Integer.parseInt(x.getText()), Integer.parseInt(y.getText()), i+1);
	        	aux2=(1-aux2)*100;
	        	aux2=aux2+50;
	        	g.drawLine(25+i, (int)aux, 26+i, (int)aux2);
	        }
	    }
	}

	private javax.swing.JTextArea textArea;
	private JTextField Longitud;
	private JTextField x;
	private JTextField y;
}
