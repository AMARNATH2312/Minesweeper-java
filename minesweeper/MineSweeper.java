package minesweeper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class MineSweeper {
  public static final String ANSI_RESET = "\u001B[0m";
  public static final String ANSI_RED = "\u001B[31m";
  public static final String ANSI_GREEN = "\u001B[32m";
  public static final String ANSI_BLUE = "\u001B[34m";
  private static Scanner scanner = new Scanner(System.in);
  private static int flags = 0, size = 10, cells = size * size;
  private static int adjacents[][] = { { -size, -1 }, { -size, 0 }, { -size, 1 }, { 0, -1 }, { 0, 1 }, { size, -1 },
      { size, 0 }, { size, 1 } };

  private static ArrayList<Integer> mineIndices = new ArrayList<Integer>();
  private static HashMap<Integer, Integer> mineCounts = new HashMap<Integer, Integer>();
  private static HashMap<Integer, String> toReveal = new HashMap<Integer, String>();
  private static ArrayList<Integer> openedZeros = new ArrayList<Integer>();

  public static void gamePlay() {
    int turns = 0;
    int inputIndex;
    fillMines();
    display();
    System.out.printf("There are %d mines only\n\n", size);
    do {
      turns++;
      System.out.print("Enter the position: ");
      inputIndex = scanner.nextInt();
    } while (inputHandler(inputIndex) > 0 && turns < cells - size);

    if (turns <= cells - size) {
      System.out.println("You win");
    }else{
      System.out.println("You lose");
    }
    scanner.close();
  }

  private static void fillMines() {
    int mineIndex;
    for (int i = 0; i < size; i++) {
      mineIndex = (int) (Math.random() * (cells));
      while (mineIndices.contains(mineIndex)) {
        mineIndex = (mineIndex + 1) % cells;
      }
      mineIndices.add(mineIndex);
      fillAdjacentsWithCount(mineIndex);
    }
  }

  private static void fillAdjacentsWithCount(int mineIndex) {
    for (int[] adjacent : adjacents) {
      int adjacentIndex = mineIndex + adjacent[0] + adjacent[1];
      if (isInBoundary(mineIndex, adjacentIndex)) {
        if (mineCounts.containsKey(adjacentIndex)) {
          mineCounts.replace(adjacentIndex, mineCounts.get(adjacentIndex) + 1);
        } else {
          mineCounts.put(adjacentIndex, 1);
        }
      }
    }
  }

  private static void openAdjacentZeros(int zeroIndex) {
    for (int[] adjacent : adjacents) {
      int adjacentIndex = zeroIndex + adjacent[0] + adjacent[1];
      if (isInBoundary(zeroIndex, adjacentIndex)) {
        toReveal.put(adjacentIndex,
            ANSI_GREEN + String.format("%4d", mineCounts.getOrDefault(adjacentIndex, 0)) + ANSI_RESET);
        openedZeros.add(zeroIndex);
        if (mineCounts.getOrDefault(adjacentIndex, 0) == 0 && !openedZeros.contains(adjacentIndex)) {
          openAdjacentZeros(adjacentIndex);
        }
      }
    }

  }

  private static boolean isInBoundary(int index, int adjacentIndex) {
    return (adjacentIndex >= 0 && adjacentIndex <= 99)
        && (index % size != 0 || (adjacentIndex + 1) % size != 0)
        && ((index + 1) % size != 0 || adjacentIndex % size != 0);
  }

  private static int inputHandler(int inputIndex) {

    if (inputIndex < 0) {
      inputIndex = (inputIndex * -1) - 1;
      if (toReveal.containsKey(inputIndex + cells)) {
        flags--;
        toReveal.remove(inputIndex + cells);
      } else if (flags < size && !toReveal.containsKey(inputIndex)) {
        flags++;
        toReveal.put(inputIndex + cells, ANSI_BLUE + String.format("%4d", inputIndex + 1) + ANSI_RESET);
      }
      display();
      return 1;
    }

    inputIndex--;
    if (mineIndices.contains(inputIndex)) {
      toRevealAllBombs();
      System.out.println("\nOops that's a mine");
      return -1;
    }

    if (mineCounts.get(inputIndex) != null) {
      toReveal.put(inputIndex, ANSI_GREEN + String.format("%4d", mineCounts.get(inputIndex)) + ANSI_RESET);
      display();
      return 1;
    }

    toReveal.put(inputIndex, ANSI_GREEN + String.format("%4s", "0") + ANSI_RESET);
    openAdjacentZeros(inputIndex);
    display();
    return 1;
  }

  private static void toRevealAllBombs() {
    for (int mineIndex : mineIndices) {
      toReveal.put(mineIndex, ANSI_RED + String.format("%4s", "*") + ANSI_RESET);
    }

    display();

  }

  private static void display() {
    System.out.print("\033[H\033[2J");
    System.out.flush();
    System.out.println("");
    int count = 0;
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        if (toReveal.containsKey(count + cells)) {
          System.out.print(toReveal.get(count + cells));
        } else {
          System.out.printf("%4s", toReveal.getOrDefault(count, count + 1 + ""));
        }
        count++;
      }
      System.out.println("\n");
    }
  }
}
