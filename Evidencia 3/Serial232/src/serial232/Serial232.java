/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serial232;

/**
 *
 * @author Angel
 */


import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;

import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JComboBox;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import java.awt.event.InputMethodListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.InputMethodEvent;
import java.awt.Color;
import java.awt.SystemColor;
import javax.swing.UIManager;

public class Serial232 extends JFrame {

	private JPanel contentPane;
	static String[] puertosActuales;
	
	public static JTextField data2Send;
	static JComboBox puertos;
	static JButton conectarBtn;
	static JButton sendBtn;
	static JTextArea receivedData;
	static String buffer;
	static SerialPort puerto;
	static int mascara;
	private JScrollPane scrollPane;
	private static JButton detenerBtn;
	static String seleccionPuerto;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Serial232 frame = new Serial232();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		listarPuertos();
	}

    public static void listarPuertos() {
		 puertosActuales = SerialPortList.getPortNames();        
		if (puertosActuales.length == 0) {
		    receivedData.append("No existen puertos serie disponibles en el sistema");
		    try {
		        System.in.read();
		    	} 
		    catch (IOException e) {}
		}		
	}	
	
	public static void iniciarComunicación () throws SerialPortException{
		
		conectarBtn.setEnabled(false);
		puertos.setEnabled(false);
		sendBtn.setEnabled(true);
		data2Send.setEnabled(true);
		detenerBtn.setEnabled(true);
		receivedData.setEnabled(true);
		receivedData.append("Conexión Inicializada en puerto " + seleccionPuerto + "\n");
		
		seleccionPuerto = (puertos.getSelectedItem()).toString();
		puerto = new SerialPort(seleccionPuerto); 
		puerto.openPort(); 
		puerto.setParams(9600, 8, 1, 0);
		mascara = SerialPort.MASK_RXCHAR; 
		puerto.setEventsMask(mascara);	
		puerto.addEventListener(new EntradaSerial()); 

	}
	
    static class EntradaSerial implements SerialPortEventListener {
    	
    	static String buffer;
        public void serialEvent(SerialPortEvent event) { 
           if(event.isRXCHAR()){ 
        	   try {
                            buffer = puerto.readString(); 
                            receivedData.append(("< : " +  buffer));
        	   		}
               catch (SerialPortException ex) {
                        System.out.println(ex); 
                    }   
        	}
        }       
    }

	
	public static void enviarDatos() throws SerialPortException{
		
		String datos = data2Send.getText();
		puerto.writeString(datos);
		receivedData.append("> : " + datos + "\n");
	}
	
	public static void cerrarPuerto() throws SerialPortException{
	
		conectarBtn.setEnabled(true);
		puertos.setEnabled(true);
		sendBtn.setEnabled(false);
		data2Send.setEnabled(false);
		detenerBtn.setEnabled(false);
		receivedData.setEnabled(false);
		
		puerto.closePort();
	}
	
	
	
	public Serial232() {
		setResizable(false);
		setTitle("  >>>>  Comunicación por puerto RS232  <<<<");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(238, 110, 115));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		puertos = new JComboBox(puertosActuales);
		puertos.setBounds(78, 20, 131, 24);
		contentPane.add(puertos);
		
		JLabel lblPuerto = new JLabel("Puerto :");
		lblPuerto.setForeground(SystemColor.controlLtHighlight);
		lblPuerto.setBounds(12, 25, 70, 15);
		contentPane.add(lblPuerto);
		
		conectarBtn = new JButton("Conectar");
		conectarBtn.setForeground(new Color(255, 255, 255));
		conectarBtn.setBackground(new Color(34, 139, 34));
		conectarBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					iniciarComunicación();
				} catch (SerialPortException e1) {
					e1.printStackTrace();
				}
			}
		});
		conectarBtn.setBounds(235, 20, 117, 25);
		contentPane.add(conectarBtn);
		
		sendBtn = new JButton("Enviar");
		sendBtn.setEnabled(false);
		sendBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					enviarDatos();
				} catch (SerialPortException e1) {
					e1.printStackTrace();
				}
			}
		});
		sendBtn.setBounds(319, 220, 117, 25);
		contentPane.add(sendBtn);
		
		data2Send = new JTextField();
		data2Send.setBounds(24, 222, 283, 22);
		data2Send.setEnabled(false);
		contentPane.add(data2Send);
		data2Send.setColumns(10);
		
		JLabel lblElRecovecoDe = new JLabel("Comunicación Serial");
		lblElRecovecoDe.setForeground(Color.WHITE);
		lblElRecovecoDe.setFont(new Font("Dialog", Font.BOLD, 10));
		lblElRecovecoDe.setBounds(150, 256, 200, 15);
		contentPane.add(lblElRecovecoDe);
		
		scrollPane = new JScrollPane();
		scrollPane.getVerticalScrollBar().addAdjustmentListener((new AdjustmentListener() {

			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
	            e.getAdjustable().setValue(e.getAdjustable().getMaximum());  				
			}  

		}));
		scrollPane.setBounds(22, 56, 283, 143);
		contentPane.add(scrollPane);
		
		receivedData = new JTextArea();
		scrollPane.setViewportView(receivedData);
		receivedData.setEditable(false);
		receivedData.setEnabled(false);
		
		detenerBtn = new JButton("Detener");
		detenerBtn.setBackground(new Color(209, 31, 93));
		detenerBtn.setForeground(new Color(255, 255, 255));
		detenerBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					cerrarPuerto();
				} catch (SerialPortException e1) {
					e1.printStackTrace();
				}
			}
		});
		detenerBtn.setEnabled(false);
		detenerBtn.setBounds(319, 87, 117, 74);
		contentPane.add(detenerBtn);
		


	}
}
