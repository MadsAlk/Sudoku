package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Scanner;
import java.util.Stack;

import com.jfoenix.controls.JFXButton;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class SampleController {
	/*
	 * _______________________________-FXML
	 * ATTRIBUTES-________________________________________________
	 */

	@FXML
	private GridPane gridPane;

	@FXML
	private Label lbl;

	@FXML
	private ImageView img;

	@FXML
	private JFXButton btn_solve;

	@FXML
	private JFXButton btn_read;

	@FXML
	private JFXButton btn_check;

	/*
	 * _______________________________-NONFXML
	 * ATTRIBUTES-___________________________________________
	 */
	static Stack<Cell> stack = new Stack<Cell>();
	final static int DIMENTION = 9;
	static int[][] B = new int[DIMENTION][DIMENTION];
	static ArrayList<BitSet> Row = new ArrayList<BitSet>(DIMENTION);
	static ArrayList<BitSet> Col = new ArrayList<BitSet>(DIMENTION);
	static ArrayList<BitSet> Grid = new ArrayList<BitSet>(DIMENTION);
	static BitSet Given = new BitSet(81);
	static ArrayList<StackPane> SP = new ArrayList<StackPane>();
	static ArrayList<TextField> TF = new ArrayList<TextField>();
	static int flag = 0;

	/*
	 * ________________________________-FXML
	 * METHODS-_________________________________________________
	 */

	@FXML
	void SolveSudoku(ActionEvent event) {
		if (flag == 1 || Solve() == false) {
			img.setVisible(true);
			lbl.setVisible(true);
			for (int i = 0; i < DIMENTION; i++) {
				for (int j = 0; j < DIMENTION; j++) {
					styleCell(SP.get((DIMENTION * i + j)), i, j, 3);
				}

			}

		} else {
			for (int i = 0; i < DIMENTION; i++) {
				for (int j = 0; j < DIMENTION; j++) {
					// make text fields
					TF.get(DIMENTION * i + j).setText(String.valueOf(B[i][j]));

				}

			}

		}

	}

	@FXML
	void ReadFromInterface(ActionEvent event) {// reading from interface
		flag = 0;
		int temp = 0;
		stack.clear();
		for (int i = 0; i < DIMENTION; i++) {
			Row.get(i).and(new BitSet());
			Col.get(i).and(new BitSet());
			Grid.get(i).and(new BitSet());
		}
		for (int i = 0; i < DIMENTION; i++) {
			for (int j = 0; j < DIMENTION; j++) {
				if (TF.get(DIMENTION * i + j).getText().equals(""))
					B[i][j] = 0;
				else
					B[i][j] = Integer.valueOf(TF.get(DIMENTION * i + j).getText());
				temp = B[i][j];
				// push empty cells into stack
				if (temp == 0) {
					stack.push(new Cell(i, j));
				}
				// add non empty cells into sets, to check validity faster.
				else {
					if (isSolvable(i, j, temp) == false) {// if the initial board is wrong
						flag = 1;
					}

				}
			}
		}

	}

	@FXML
	void Check(ActionEvent event) {// checking a new cell
		for (int i = 0; i < DIMENTION; i++) {
			Row.get(i).and(new BitSet());
			Col.get(i).and(new BitSet());
			Grid.get(i).and(new BitSet());
		}
		for (int i = 0; i < DIMENTION; i++) {
			for (int j = 0; j < DIMENTION; j++) {
				if (Given.get(DIMENTION * i + j) == true) {
					isSolvable(i, j, B[i][j]);
				}
			}

		}

		for (int i = 0; i < DIMENTION; i++) {
			for (int j = 0; j < DIMENTION; j++) {

				if (Given.get(DIMENTION * i + j) == false && B[i][j] != 0 && isSolvable(i, j, B[i][j]) == false) {
					styleCell(SP.get((DIMENTION * i + j)), i, j, 3);
				} else if (Given.get(DIMENTION * i + j) == false && B[i][j] != 0) {
					styleCell(SP.get((DIMENTION * i + j)), i, j, 2);
				} else if (Given.get(DIMENTION * i + j) == false) {
					styleCell(SP.get((DIMENTION * i + j)), i, j, 0);
				}
			}

		}
	}

	/*
	 * ______________________________-NONFXML
	 * METHODS-________________________________________________
	 */

	public void initialize() throws FileNotFoundException {
		// initialize bitsets for checking availability
		for (int i = 0; i < DIMENTION; i++) {
			Row.add(new BitSet(DIMENTION + 1));
			Col.add(new BitSet(DIMENTION + 1));
			Grid.add(new BitSet(DIMENTION + 1));
		}

		// initialize cells
		for (int i = 0; i < DIMENTION; i++) {
			for (int j = 0; j < DIMENTION; j++) {
				TextField tf = new TextField();
				tf.setPrefWidth(5);
				tf.setAlignment(Pos.CENTER);
				StackPane sp = new StackPane();
				sp.getChildren().add(tf);
				styleCell(sp, i, j, 0);
				gridPane.add(sp, j, i);
				SP.add(sp);
				TF.add(tf);

			}
		}

		EventHandler eh = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				btn_read.fire();
				btn_check.fire();
			}
		};

		// textfield listener
		for (int i = 0; i < 81; i++) {
			TF.get(i).setOnAction(eh);
		}

		// read file
		read();

	}

	public static void read() throws FileNotFoundException {
		File file = new File("input.txt");
		Scanner sc = new Scanner(file);
		sc.useDelimiter("[,\\{\\}]");
		String str = "";
		int temp = 0;

		for (int i = 0; i < DIMENTION; i++) {
			for (int j = 0; j < DIMENTION; j++) {
				if (sc.hasNext()) {
					do {
						str = sc.next().trim();
					} while (str.matches("-?\\d+") == false);
					temp = Integer.valueOf(str);
					B[i][j] = temp;
					// push empty cells into stack
					if (temp == 0) {
						stack.push(new Cell(i, j));
					}
					// add non empty cells into sets, to check validity faster.
					else {
						if (isSolvable(i, j, temp) == false) {// if the initial board is wrong
							flag = 1;
						}
						Given.flip(DIMENTION * i + j);
						styleCell(SP.get((DIMENTION * i + j)), i, j, 1);

						TF.get(DIMENTION * i + j).setText(String.valueOf(B[i][j]));
						TF.get(DIMENTION * i + j).setEditable(false);

					}
				} else
					System.out.println("not enough numbers!");

			}
		}

	}

	// backtrack
	public static boolean Solve() {
		Cell current = null;
		if (stack.isEmpty())
			return true;
		else
			current = stack.pop();
		styleCell(SP.get((DIMENTION * current.getRow() + current.getCol())), current.getRow(), current.getCol(), 2);

		for (int i = 1; i <= DIMENTION; i++) {
			if (isValid(current, i)) {
				// add the number to the board
				B[current.getRow()][current.getCol()] = i;
				// add the number to the row col and 3x3 sets.
				Row.get(current.getRow()).flip(i);
				Col.get(current.getCol()).flip(i);
				Grid.get(getGridNum(current.getRow(), current.getCol())).flip(i);
				// solve next
				if (Solve())
					return true;
				// reset
				B[current.getRow()][current.getCol()] = 0;
				// add the number to the row col and grid sets.
				Row.get(current.getRow()).flip(i);
				Col.get(current.getCol()).flip(i);
				Grid.get(getGridNum(current.getRow(), current.getCol())).flip(i);
			}
		}
		stack.push(current);
		return false;
	}

	public static boolean isValid(Cell current, int num) {
		// check if the number exists in this row
		if (Row.get(current.getRow()).get(num) == true)
			return false;
		// check if it exists in this column
		if (Col.get(current.getCol()).get(num) == true)
			return false;
		// check the 3x3 grid
		if (Grid.get(getGridNum(current.getRow(), current.getCol())).get(num) == true)
			return false;
		// if non of them have it, then the number is valid.
		return true;
	}

	public static boolean isSolvable(int i, int j, int temp) {
		if (Row.get(i).get(temp) || Col.get(j).get(temp) || Grid.get(getGridNum(i, j)).get(temp))
			return false;
		Row.get(i).flip(temp);
		Col.get(j).flip(temp);
		Grid.get(getGridNum(i, j)).flip(temp);
		return true;
	}

	public static int getGridNum(int i, int j) {
		j /= 3;
		i /= 3;
		return (i * 3) + j;
	}

	// 0 white _ 1 yellow _ 2 green _ 3 red
	public static void styleCell(StackPane sp, int i, int j, int type) {
		String color = "";
		if (type == 0)
			color = "ffffff";
		else if (type == 1)
			color = "feffa5";
		else if (type == 2)
			color = "bdff8f";
		else
			color = "ffa48f";

		if (j == 3 || j == 6)
			sp.setStyle(
					"-fx-border-style: solid solid solid solid; -fx-border-width: 1 1 1 4; -fx-border-color: black; -fx-background-color: #"
							+ color + ";");
		else if (i == 3 || i == 6)
			sp.setStyle(
					"-fx-border-style: solid solid solid solid; -fx-border-width: 4 1 1 1; -fx-border-color: black; -fx-background-color: #"
							+ color + ";");
		else
			sp.setStyle(
					"-fx-border-style: solid solid solid solid; -fx-border-width: 1; -fx-border-color: black; -fx-background-color: #"
							+ color + ";");
		if (i == 3 && j == 3)
			sp.setStyle(
					"-fx-border-style: solid solid solid solid; -fx-border-width: 4 1 1 4; -fx-border-color: black; -fx-background-color: #"
							+ color + ";");
		if (i == 3 && j == 6)
			sp.setStyle(
					"-fx-border-style: solid solid solid solid; -fx-border-width: 4 1 1 4; -fx-border-color: black; -fx-background-color: #"
							+ color + ";");
		if (i == 6 && j == 3)
			sp.setStyle(
					"-fx-border-style: solid solid solid solid; -fx-border-width: 4 1 1 4; -fx-border-color: black; -fx-background-color: #"
							+ color + ";");
		if (i == 6 && j == 6)
			sp.setStyle(
					"-fx-border-style: solid solid solid solid; -fx-border-width: 4 1 1 4; -fx-border-color: black; -fx-background-color: #"
							+ color + ";");
	}

}
