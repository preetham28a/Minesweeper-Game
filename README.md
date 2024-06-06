# Minesweeper-Game
In Minefield, users are attempting to reveal every square of a given field while avoiding randomly
placed mines. Each square has a value (1-8), that tells the user how many mines surround it in a
3x3 tile. A flag allows users to skip over squares they believe to be mines. Hit a mine, and the
game is over. If you reveal all squares and place flags on all the mines, you win. For our version,
you will be playing on a field, which is a two-dimensional array that will contain all the relevant
information for the game.
revealZeroes() and revealMines(). These methods allow the user to reveal all surrounding zeros on the field and the first mine(s) found given two starting coordinates, respectively. In Minesweeper, when the
user chooses their starting coordinates, the game reveals a large enough area to give enough
information to start solving the field. Additionally, if a user chooses a square with a ’0’ value in
it, meaning there are no mines, the field also reveals all surrounding zeroes.

Minesweeper relies on 3x3 tiles. In each of the cells within this 3x3 tile, a value is given based on
the number of mines surrounding it.

Below is an example:
1. 0 0 0 0 0
0 1 1 1 0
0 1 * 1 0
0 1 1 1 0
0 0 0 0 0
2. 0 0 1 1 1
0 1 2 * 1
0 1 * 2 1
0 1 1 1 0
0 0 0 0 0
