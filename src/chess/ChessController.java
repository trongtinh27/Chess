package chess;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Executors;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class ChessController implements ChessDelegate, ActionListener, Runnable {
	
	private ChessModel chessModel = new ChessModel();
	private ChessView chessBoardPanel;
	private JButton resetBtn;
	private JButton serverBtn;
	private JButton clientBtn;
	
	
	public ChessController() {
		
		chessModel.reset();
		
		var frame = new JFrame("Chess");
		frame.setSize(600, 600 );
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setLayout(new BorderLayout());
		
		chessBoardPanel = new ChessView(this);
		frame.add(chessBoardPanel, BorderLayout.CENTER);
		
		var buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		resetBtn = new JButton("Reset");
		resetBtn.addActionListener(this);
		buttonsPanel.add(resetBtn);
		serverBtn = new JButton("Listen");
		serverBtn.addActionListener(this);
		buttonsPanel.add(serverBtn);
		clientBtn = new JButton("Connect");
		clientBtn.addActionListener(this);
		buttonsPanel.add(clientBtn);
		frame.add(buttonsPanel, BorderLayout.PAGE_END);
		
		frame.setVisible(true);
	}
	
	public static void main(String[] args) {
		new ChessController();
	}

	@Override
	public ChessPiece pieceAt(int col, int row) {
		return chessModel.pieceAt(col, row);
	}

	@Override
	public void movePiece(int fromCol, int fromRow, int toCol, int toRow) {
		chessModel.movePiece(fromCol, fromRow, toCol, toRow);
		chessBoardPanel.repaint();
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == resetBtn) {
			chessModel.reset();
			chessBoardPanel.repaint();
		}
		if(e.getSource() == serverBtn) {
			var pool = Executors.newFixedThreadPool(1);
			pool.execute(this);
		}
		if(e.getSource() == clientBtn) {
			System.out.println("connect (for socket clinet clicked");
			try (var socket = new Socket("localhost", 50000)){
				var in = new Scanner(socket.getInputStream());
				var moveStr = in.nextLine();
				System.out.println("from server : " + moveStr);
				var moveStrArr = moveStr.split(",");
				var fromCol = Integer.parseInt(moveStrArr[0]);
				var fromRow = Integer.parseInt(moveStrArr[1]);
				var toCol = Integer.parseInt(moveStrArr[2]);
				var toRow = Integer.parseInt(moveStrArr[3]);
				
				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						chessModel.movePiece(fromCol, fromRow, toCol, toRow);
						chessBoardPanel.repaint();
						
					}
				});
				
			}  catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
	}
	
	@Override 
	public void run() {
		try (var listener = new ServerSocket(50000)){
			System.out.println("server is listening to port 50000");
			while(true) {
				try(var socket = listener.accept()) {
					var out = new PrintWriter(socket.getOutputStream(), true);
					out.println("0,1,0,3");
					System.out.println("sending a move to client");
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
