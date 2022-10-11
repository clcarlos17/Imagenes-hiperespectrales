package Proyecto;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JFileChooser;

public class DataCube {

	/*
	 * Atributos que tendrá el cubo
	 */
	
	private int samples, lines, bands, header_offset, byte_order;
	private String file_type, interleave, sensor_type, units;
	private int data_type;
	private ArrayList<String> longitudes = new ArrayList<String>();
	private double[][][] cubo;
	private double mayor, menor;
	private BufferedImage img;

	/*
	 * Lee el fichero y lo inserta en la posición adecuada dentro del cubo
	 */
	public void cargar(){
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.showOpenDialog(fileChooser);
		File archivo = null;
		FileReader fr = null;
		BufferedReader br = null;
		String[] parts = null;

		try {
			// Apertura del fichero y creacion de BufferedReader para poder
			// hacer una lectura comoda (disponer del metodo readLine()).
			String ruta = fileChooser.getSelectedFile().getAbsolutePath();
			archivo = new File(ruta);
			fr = new FileReader(archivo);
			br = new BufferedReader(fr);
			int longitudes=0;

			// Lectura del fichero
			String linea;
			while ((linea = br.readLine()) != null) {
				parts = linea.split(" = ");
				if (parts.length > 1)						//Si ha leido mas de 1 operando
					parts[1]=parts[1].replace(" ", "");		//Quitamos los espacios en blanco
				if (parts[0].indexOf("samples")!=-1) {
					setSamples(Integer.parseInt(parts[1]));
				}else if(parts[0].indexOf("lines")!=-1) {
					setLines(Integer.parseInt(parts[1]));
				}else if(parts[0].indexOf("bands")!=-1) {
					longitudes=Integer.parseInt(parts[1]);
					setBands(Integer.parseInt(parts[1]));
				}else if(parts[0].indexOf("header offset")!=-1) {
					setHeader_offset(Integer.parseInt(parts[1]));
				}else if(parts[0].indexOf("file type")!=-1) {
					setFile_type(parts[1]);
				}else if(parts[0].indexOf("data type")!=-1) {
					setData_type(Integer.parseInt(parts[1]));
				}else if(parts[0].indexOf("interleave")!=-1) {
					setInterleave(parts[1]);
				}else if(parts[0].indexOf("sensor type")!=-1) {
					setSensor_type(parts[1]);
				}else if(parts[0].indexOf("byte order")!=-1) {
					setByte_order(Integer.parseInt(parts[1]));
				}else if(parts[0].indexOf("wavelength units")!=-1) {
					setUnits(parts[1]);
				}else if(parts[0].indexOf("wavelength")!=-1){
					while ((linea = br.readLine()) != null && longitudes != 0) {
						longitudes--;
						parts = linea.split(",");
						for(int i=0; i<parts.length; i++) {
							parts[i]=parts[i].replace(" ", "");
							if (!parts[i].equals(""))
								setLongitudes(parts[i]);
						}
					}
				}
			}
			this.cubo = new double[getLines()][getSamples()][this.longitudes.size()];
			this.img=new BufferedImage(getSamples(), getLines(), BufferedImage.TYPE_INT_RGB);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != fr) {
					fr.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
	
	/*
	 * Recorre todo el cubo realizando el cálculo de la normalización
	 */
	
	public void normalizar() {
		double aux=0;
		aux=this.mayor-this.menor;
		
		for (int k=0; k<this.bands; k++) {
			for (int i=0; i<this.lines; i++) {
				for (int j=0; j<this.samples; j++) {
					this.cubo[i][j][k]=this.cubo[i][j][k]-this.menor;
					this.cubo[i][j][k]=this.cubo[i][j][k]/aux;
				}
			}
		}
	}
	
	/*
	 * Método de prueba que se encarga de escribir el cubo en un fichero para poder
	 * comparar sus valores
	 */
	
	public void escribir() throws IOException {
		FileWriter file = new FileWriter("./prueba.txt");
		for (int k=0; k<this.bands; k++) {
			for (int i=0; i<this.lines; i++) {
				for (int j=0; j<this.samples; j++) {
					file.write(Double.toString(getcubo(i, j, k)));
					file.write("|  |");
				}
				file.write("\n");
			}
			file.write("\n");
			file.write("\n");
			file.write("*****"); 
			file.write(getLongitud(k));
			file.write("*****");
			file.write("\n");
			file.write("\n");
		}
		file.close();
	}
	
	public void setCubo(int i, int j, int k, double dato) {
		this.cubo[i][j][k]=dato;
	}
	
	public double getcubo(int i, int j, int k) {
		return this.cubo[i][j][k];
	}
	
	/*
	 * Método al que le indicas una banda, y te devuelve el índide de su
	 * posición en la lista
	 */
	
	public int buscarBanda(String longitud) {
		int index=0;
		String aux=getLongitud(index);
		while (!longitud.equals(aux)) {
			aux=getLongitud(index);
			index++;
		}
		return index;
	}
	
	/*
	 * Metodo que obtiene el valor RGB de un pixel de una banda, y lo inserta
	 * como escala de grises en una imagen para ser representado
	 */
	
	public void mostrarBandas(String banda) {
		int indice=0;
		boolean enc=false;
		for (int i = 0; i < this.longitudes.size() && !enc; i++) {
			if (banda.equals(this.longitudes.get(i))) {
				enc=true;
				indice=i;
			}
		}
		
		for (int i=0; i<this.lines; i++) {
			for (int j=0; j<this.samples; j++) {
				int rgb=((int)(getcubo(i, j, indice)*255)& 0xFF);	//Obtenemos el valor en un rango de [0,255]
				int RGB = (rgb << 16) | (rgb << 8) | rgb;			//Llenamos cada uno de los tres canales del mismo valor para que salga en escala de gris
				this.img.setRGB(j, i,  RGB);
			}
		}
	}


	public int getSamples() {
		return samples;
	}

	public void setSamples(int samples) {
		this.samples = samples;
	}

	public int getLines() {
		return lines;
	}

	public void setLines(int lines) {
		this.lines = lines;
	}

	public int getBands() {
		return bands;
	}

	public void setBands(int bands) {
		this.bands = bands;
	}

	public int getHeader_offset() {
		return header_offset;
	}

	public BufferedImage getImg() {
		return img;
	}
	
	
	public void setHeader_offset(int header_offset) {
		this.header_offset = header_offset;
	}

	public int getByte_order() {
		return byte_order;
	}

	public void setByte_order(int byte_order) {
		this.byte_order = byte_order;
	}

	public String getFile_type() {
		return file_type;
	}

	public void setFile_type(String file_type) {
		this.file_type = file_type;
	}

	public String getInterleave() {
		return interleave;
	}

	public void setInterleave(String interleave) {
		this.interleave = interleave;
	}

	public String getSensor_type() {
		return sensor_type;
	}

	public void setSensor_type(String sensor_type) {
		this.sensor_type = sensor_type;
	}

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public int getData_type() {
		return data_type;
	}

	public void setData_type(int data_type) {
		this.data_type = data_type;
	}
	public ArrayList<String> getLongitudes() {
		return longitudes;
	}
	
	public String getLongitud(int i) {
		return longitudes.get(i);
	}

	public void setLongitudes(String longitud) {
		this.longitudes.add(longitud);
	}
	
	public double getMayor() {
		return mayor;
	}

	public void setMayor(double mayor) {
		this.mayor = mayor;
	}

	public double getMenor() {
		return menor;
	}

	public void setMenor(double menor) {
		this.menor = menor;
	}

	/*
	 * Método que muestra los atributos del cubo
	 */
	
	public void mostrarCubo() {
		System.out.println("Samples = " + this.samples);
		System.out.println("Lines = " + this.lines);
		System.out.println("Bands = " + this.bands);
		System.out.println("header_offset = " + this.header_offset);
		System.out.println("file_type = " + this.file_type);
		System.out.println("data_type = " + this.data_type);
		System.out.println("interleave = " + this.interleave);
		System.out.println("sensor_type = " + this.sensor_type);
		System.out.println("byte_order = " + this.byte_order);
		System.out.println("units = " + this.units);
		System.out.println("wavelength = { ");
	}
	
	public void mostrarLongitudes() {
		for (int i=0; i<longitudes.size(); i++) {
			System.out.println(longitudes.get(i) + this.units);
		}
	}

	public DataCube() {
		super();
	}

	public DataCube(int samples, int lines, int bands, int header_offset, int byte_order, String file_type,
			String interleave, String sensor_type, String units, int data_type) {
		super();
		this.samples = samples;
		this.lines = lines;
		this.bands = bands;
		this.header_offset = header_offset;
		this.byte_order = byte_order;
		this.file_type = file_type;
		this.interleave = interleave;
		this.sensor_type = sensor_type;
		this.units = units;
		this.data_type = data_type;
	}

}
