package MinesweeperGame;

import java.util.ArrayList;
import java.util.Scanner;

public class MineSweeper {
    private static int[][] hidden_array;
    private static String[][] Display;
    private static String wrong_mark = "\u001B[33m";
    private static String unopened = "\u001B[0m";
    private static String bomb = "\u001B[31m";
    private static String opened = "\u001B[34m";
    private static String marked = "\u001B[32m";
    private static String MARK_SYMBOL = "   *";
    private static String BOMB_SYMBOL = "   @";
    private static String SPACING = "%4s";
    private static ArrayList<Integer> bombLocations = new ArrayList<>();
    private static ArrayList<Integer> bombFreeLocations = new ArrayList<>();
    private static int count = 0;
    private static int size;
    private static int no_of_bombs;
    private static int no_of_bombmark;
    private static int BOMB = 9;   // set bomb value = 9 in hidden array
    private static boolean not_yet_win = true;

    private static void initializeArray() {
        hidden_array = new int[size][size];
        Display = new String[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                Display[i][j] = unopened + String.format(SPACING, (i * size) + j);
            }
        }
    }

    public static void start_game() {
        System.out.println("Select the Difficulty level");
        System.out.println("1. Easy\n2. Medium\n3. Hard ");
        Scanner level = new Scanner(System.in);
        int difficulty_level = level.nextInt();
        if (difficulty_level == 1) {
            size = 9;
            no_of_bombs = 10;
            no_of_bombmark = 10;
        } else if (difficulty_level == 2) {
            size = 15;
            no_of_bombs = 35;
            no_of_bombmark = 35;
        } else if (difficulty_level == 3) {
            size = 23;
            no_of_bombs = 70;
            no_of_bombmark = 70;
        } else {
            System.out.println(" Invalid Input");
            start_game();
        }
        initializeArray();
        printDisplay();
        firstInput();
        printDisplay();
        while (count < (size * size) - no_of_bombs && not_yet_win) {
            getInput();
            System.out.println("\033[H\033[2J");
            System.out.flush();
            printDisplay();
            if (count == (size * size) - no_of_bombs) {
                reveal_bombs();
            }
        }
    }

    private static void firstInput() {
        Scanner inp = new Scanner(System.in);
        System.out.println("Enter the location :");
        int inp_number = inp.nextInt();
        bombFreeLocations.add(inp_number);
        int[] x_axis = {0, 0, -1, -1, -1, 1, 1, 1};
        int[] y_axis = {-1, 1, -1, 0, 1, -1, 0, 1};
        int row = inp_number / size;
        int col = inp_number % size;
        for (int i = 0; i < 8; i++) {
            int x = row + x_axis[i];
            int y = col + y_axis[i];
            if (x >= 0 && y >= 0 && x < size && y < size) {
                bombFreeLocations.add(x * size + y);
            }
        }
        setBomb();
        set_display(row, col);
    }


    private static void setBomb() {
        for (int i = 0; i < no_of_bombs; i++) {
            int bombIndex = (int) (Math.random() * (size * size));
            while (bombLocations.contains(bombIndex) || bombFreeLocations.contains(bombIndex)) {
                bombIndex = (bombIndex + 1) % (size * size);

            }
            int bomb_row = bombIndex / size;
            int bomb_col = bombIndex % size;
            bombLocations.add(bombIndex);
            hidden_array[bomb_row][bomb_col] = BOMB;
            valueAssign(hidden_array, bomb_row, bomb_col);
        }
    }

    private static void valueAssign(int[][] arr, int row, int col) {
        int[] x_axis = {0, 0, -1, -1, -1, 1, 1, 1};
        int[] y_axis = {-1, 1, -1, 0, 1, -1, 0, 1};
        for (int i = 0; i < 8; i++) {
            int x = row + x_axis[i];
            int y = col + y_axis[i];
            if (x >= 0 && y >= 0 && x < size && y < size && arr[x][y] != BOMB) {
                arr[x][y] += 1;
            }
        }
    }

    private static void getInput() {
        Scanner inp = new Scanner(System.in);
        System.out.println("1.To mark (or) remove mark " + no_of_bombmark);
        System.out.println("2. To Mine");
        int mark_or_mine = inp.nextInt();
        System.out.println("Enter the location :");
        int inp_number = inp.nextInt();
        int row = inp_number / size;
        int col = inp_number % size;
        bombFreeLocations.add(inp_number);
        if (mark_or_mine == 2) {
            set_display(row, col);
        } else if (mark_or_mine == 1) {
            if (no_of_bombmark > 0 && Display[row][col].equals(unopened + String.format(SPACING, (row * size) + col))) {
                Display[row][col] = marked + String.format(SPACING, (row * size) + col) + unopened;
                no_of_bombmark--;
            } else if (Display[row][col].equals(marked + String.format(SPACING, (row * size) + col) + unopened)) {
                Display[row][col] = unopened + String.format(SPACING, (row * size) + col);
                no_of_bombmark++;
            }
            printDisplay();
        } else {
            System.out.println("Invalid Input \nEnter '1' or '2'");
            getInput();
        }
    }


    private static void set_display(int row, int col) {
        if (row >= 0 && col >= 0 && row < size && col < size) {
            if (Display[row][col].equals(unopened + String.format(SPACING, (row * size) + col))) {
                String val = get_value(row, col);
                Display[row][col] = val;
                count += 1;
                if (val.equals(opened + "   0" + unopened)) {
                    ifZero(row, col);
                } else if (val.equals(bomb + BOMB_SYMBOL + unopened)) {
                    System.out.println("bomb revealed....");
                    System.out.println();
                    not_yet_win = false;
                    reveal_bombs();
                    System.out.println("Game Ends");
                }
            }
        } else {
            System.out.println("Invalid Input\nEnter values between 0 and" + size + ".");
            getInput();
        }
    }

    private static String get_value(int i, int j) {
        if (hidden_array[i][j] == BOMB) {
            return bomb + BOMB_SYMBOL + unopened;
        } else {
            return opened + String.format(SPACING, hidden_array[i][j]) + unopened;
        }
    }

    private static void ifZero(int row, int col) {
        int[] x_axis = {0, 0, -1, -1, -1, 1, 1, 1};
        int[] y_axis = {-1, 1, -1, 0, 1, -1, 0, 1};
        for (int i = 0; i < 8; i++) {
            int x = row + x_axis[i];
            int y = col + y_axis[i];
            if (x >= 0 && y >= 0 && x < size && y < size) {
                set_display(x, y);
            }
        }
    }

    private static void printDisplay() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print(Display[i][j]);
            }
            System.out.println();
        }
    }

    private static void reveal_bombs() {
        if (!not_yet_win) {
            for (int each_bomb : bombLocations) {
                int row = each_bomb / size;
                int col = each_bomb % size;
                if (!Display[row][col].equals(marked + String.format(SPACING, (row * size) + col) + unopened)) {
                    Display[row][col] = bomb + BOMB_SYMBOL + unopened;
                }
            }
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (Display[i][j].equals(marked + String.format(SPACING, (i * size) + j) + unopened)) {
                        if (!bombLocations.contains((i * size) + j)) {
                            Display[i][j] = wrong_mark + "   X" + unopened;
                        }
                    }
                }
            }
        } else {
            System.out.println("You Win");

        }
    }
}
