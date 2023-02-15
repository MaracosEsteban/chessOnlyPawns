//this class reprsents a game
class Game//Establecer las posicion inicial de los peones
    (private var whitePlayer: String, private var blackPlayer: String) {

    // defino la clase que contendrá la informacion de cado casilla
    enum class BS(val carImp: Char) {  //BS means "Box State"
        FREE(' '), WHITE_PAWN('W'), BLACK_PAWN('B')
    }

    private val regex = "[a-h][1-8][a-h][1-8]".toRegex()
    var keepGoing = true
    private var turn: Boolean = true // true means it's white's turn
    var validImput = true
    private var errorMesage = "Invalid Input"
    private var currentMove = ""
    private var lastWhiteMove = ""
    private var lastBlackMove = ""

    private var ChB = mutableListOf(
        //ChB means "Chess Board"
        mutableListOf(),
        mutableListOf(),
        mutableListOf(),
        mutableListOf(),
        mutableListOf(),
        mutableListOf(),
        mutableListOf(),
        mutableListOf<BS>(),
    )


    init {
        for (n in 0..7) {
            when (n) {
                1 -> {
                    ChB[n].addAll(
                        mutableListOf( BS.BLACK_PAWN,BS.BLACK_PAWN,BS.BLACK_PAWN,BS.BLACK_PAWN,BS.BLACK_PAWN,BS.BLACK_PAWN,BS.BLACK_PAWN,BS.BLACK_PAWN)
                    )
                }
                6 -> {
                    ChB[n].addAll(mutableListOf(BS.WHITE_PAWN,BS.WHITE_PAWN,BS.WHITE_PAWN,BS.WHITE_PAWN,BS.WHITE_PAWN,BS.WHITE_PAWN,BS.WHITE_PAWN,BS.WHITE_PAWN )

                    )
                }
                else -> {
                    ChB[n].addAll(mutableListOf(BS.FREE, BS.FREE, BS.FREE, BS.FREE, BS.FREE, BS.FREE, BS.FREE, BS.FREE))
                }
            }
        }
    }

    fun PrintTurn() {
        println(
            if (turn) {
                "$whitePlayer's turn:"
            }
            else {
                "$blackPlayer's turn:"
            }
        )
    }

    private fun toCoord(car: Char): Int {
        return when (car) {
            'h', '1' -> 7
            'g', '2' -> 6
            'f', '3' -> 5
            'e', '4' -> 4
            'd', '5' -> 3
            'c', '6' -> 2
            'b', '7' -> 1
            'a', '8' -> 0
            else -> 100// todo impossible case
        }
    }

    /* calcula el movimiento decorrespondiente al passant para compararlo con la jugada anterior
    recibe dos parámetros tipo boolean
   color => true = blanca
   lado => true = derecha
   devuelve un string que son las cordenadas de la jugada anterior
     */
    private fun calPassant(fil: Int, col: Int, color: Boolean, side: Boolean): String {
        var chRet: String = ""

        if (color) { //here the color si white
            if (side) {// right side
                chRet = (fil - 2).toString() + (col + 1).toString() + fil.toString() + (col + 1).toString()    //ok
            }
            else { // left side
                chRet = (fil - 2).toString() + (col - 1).toString() + fil.toString() + (col - 1).toString()   //ok
            }
        }
        else {  //here the color es black
            if (side) {// right side
                chRet =
                    (fil + 2).toString() + (col + 1).toString() + fil.toString() + (col + 1).toString()    // todo revisar
            }
            else { // left side
                chRet =
                    (fil + 2).toString() + (col - 1).toString() + fil.toString() + (col - 1).toString()    // todo revisar
            }
        }


        return chRet
    }

    private fun movePawn(
        c1: Int, f1: Int, c2: Int, f2: Int
    ): Boolean { //This function validates and executes the move, returning true if the move could be made and false otherwise.
        errorMesage = "Invalid Input"


        val ini = currentMove.substring(0, 2)


        if (turn) // ==================== Aqué el turnos es de las blancas =============================================
        {
            /* 1) si no hay ningun peón blanco en la posición inicial                                                      */
            if (ChB[f1][c1] != BS.WHITE_PAWN) {
                errorMesage = "No white pawn at $ini"
                return false
            }
            /* 2) si la casilla donde quiere mover no está libre a menos que sea un movimiento diagonal permitidos        */
            else if (ChB[f2][c2] != BS.FREE && !((f2 == f1 - 1 && c2 == c1 - 1) or (f2 == f1 - 1 && c2 == c1 + 1))) {
                return false
            }
            /* 3) si avanza mas de una casilla, no partiendo de la posición inicial                                        */
            else if (f1 != 6 && f2 + 1 != f1) {
                return false
            }
            /* 4) si avanza mas de dos casilleros partiedo de la posicion inicial                                          */
            else if (f1 == 6 && f2 + 2 < f1) {
                return false
            }
            /* 5) si no avanza de fila o retrocede                                                                         */
            else if (f2 >= f1) {
                return false
            }
            /* 6)si la columna es distita pero no es uno de los dos movimientos en diagonal permitidos o un passat         */
            else if (c1 != c2 && !(((f2 == f1 - 1 && c2 == c1 - 1) && (((ChB[f2][c2] == BS.BLACK_PAWN)) || (lastBlackMove == (calPassant(
                    f1, c1, true, false
                ))))) or ((f2 == f1 - 1 && c2 == c1 + 1) && (((ChB[f2][c2] == BS.BLACK_PAWN)) || (lastBlackMove == calPassant(
                    f1, c1, true, true
                )))))
            ) {
                return false
            }
            /* 7) Desde la posicion  inicial avanza dos casillero pero el primero está ocupado                             */
            else if ((f1 == 6) && (f2 + 2 == f1) && (ChB[f1 - 1][c1] != BS.FREE)) {
                return false
            }

        }
        else {// ==================== Aqué el turnos es de las negras =============================================
            /* 1) si no hay ningun peón negro en la posición inicial                                                      */
            if (ChB[f1][c1] != BS.BLACK_PAWN) { // si no hay un peón negro en la posicion inicial
                errorMesage = "No black pawn at $ini"
                return false
            }
            /* 2) si la casilla donde quiere mover no está libre a menos que sea un movimiento diagonal permitidos        */
            else if ((ChB[f2][c2] != BS.FREE) && !((f2 == f1 + 1 && c2 == c1 - 1) or (f2 == f1 + 1 && c2 == c1 + 1))) {
                return false
            }
            /* 3) si avanza mas de una casilla, no partiendo de la posición inicial                                        */
            else if (f1 != 1 && f2 != f1 + 1) {
                return false
            }
            /* 4) si avanza mas de dos casilleros partiedo de la posicion inicial                                          */
            else if (f1 == 1 && f2 > f1 + 2) {
                return false
            }
            /* 5) si no avanza de fila o retrocede                                                                         */
            else if (f2 <= f1) {
                return false
            }
            /* 6)si la columna es distita pero no es uno de los dos movimientos en diagonal permitidos o un passat         */
            else if (c1 != c2 && !(((f2 == f1 + 1 && c2 == c1 + 1) && (((ChB[f2][c2] == BS.WHITE_PAWN)) || (lastWhiteMove == (calPassant(
                    f1, c1, false, true
                ))))) or ((f2 == f1 + 1 && c2 == c1 - 1) && (((ChB[f2][c2] == BS.WHITE_PAWN)) || (lastWhiteMove == calPassant(
                    f1, c1, false, false
                )))))
            ) {
                return false
            }
            /* 7) Desde la posicion  inicial avanza dos casillero pero el primero está ocupado                             */
            else if ((f1 == 1) && (f2 == f1 + 2) && (ChB[f1 + 1][c1] != BS.FREE)) {
                return false
            }
        }


        //---------------------------here the play is executed------------------------------------
        ChB[f1][c1] = BS.FREE

        if (turn) // el turno es de las blancas
        {
            lastWhiteMove = "" + f1 + c1 + f2 + c2
            // si la jugado fue un passat eliminar el peón negro
            if (((f2 == f1 - 1 && c2 == c1 - 1) && (lastBlackMove == calPassant(
                    f1, c1, true, false
                ))) || ((f2 == f1 - 1 && c2 == c1 + 1) && (lastBlackMove == calPassant(f1, c1, true, true)))
            ) { //se produjo un passat a la hizquierda o a la derecha
                //elimintar el pón a la hisquierda o derecha
                val rowPawn = lastBlackMove.substring(2, 3).toInt()
                val pawnColumn = lastBlackMove.substring(3, 4).toInt()
                ChB[rowPawn][pawnColumn] = BS.FREE
            }



            ChB[f2][c2] = BS.WHITE_PAWN
        }
        else {// el turno es de las negras
            lastBlackMove = "" + f1 + c1 + f2 + c2
            // si la jugada fue un passat elimina el peón blanco
            if (((f2 == f1 + 1 && c2 == c1 + 1) && (lastWhiteMove == calPassant(
                    f1, c1, false, true
                ))) || ((f2 == f1 + 1 && c2 == c1 - 1) && (lastWhiteMove == calPassant(f1, c1, false, false)))
            ) {
                //elimintar el pón a la derecha o hisquierda
                val rowPawn = lastWhiteMove.substring(2, 3).toInt()
                val pawnColumn = lastWhiteMove.substring(3, 4).toInt()
                ChB[rowPawn][pawnColumn] = BS.FREE
            }
            ChB[f2][c2] = BS.BLACK_PAWN
        }
        return true
    }

    fun Play(move: String) { //Function that receives the next move from the main program
        validImput = true
        currentMove = move
        if (move != "exit" && move != "Exit") {
            if (regex.matches(move)) {
                if (movePawn(toCoord(move[0]), toCoord(move[1]), toCoord(move[2]), toCoord(move[3]))) {
                    turn = !turn
                    checkWinDrawCond()
                }
                else {
                    validImput = false
                    println(errorMesage)
                }
            }
            else {
                validImput = false
                println(errorMesage)
            }
        }
        else {
            println("Bye!")
            keepGoing = false
        }
    }

    fun printBoard() {  //Function that prints the board with the current position of the pawns
        for (x in 0..7) {
            val y: Int = 8 - x
            println("  +---+---+---+---+---+---+---+---+")
            println("$y | ${ChB[x][0].carImp} | ${ChB[x][1].carImp} | ${ChB[x][2].carImp} | ${ChB[x][3].carImp} | ${ChB[x][4].carImp} | ${ChB[x][5].carImp} | ${ChB[x][6].carImp} | ${ChB[x][7].carImp} |")
        }
        println("  +---+---+---+---+---+---+---+---+\n    a   b   c   d   e   f   g   h  ")
        println()
    }

    fun checkWinDrawCond() {
        //----------------------------check that there are no pawns in 1 or 8--------------------------
        for (x in 0..7) {
            if (ChB[7][x] != BS.FREE) { // there is a pawn on the 8th rank, therefore whites won
                printBoard()
                println("Black Wins!")
                println("Bye!")
                this.keepGoing = false
                return
            }
            if (ChB[0][x] != BS.FREE) {   // there is a pawn on rank 1, therefore blacks won
                printBoard()
                println("White Wins!")
                println("Bye!")
                this.keepGoing = false
                return
            }
        }
        // ------------------------------check that there is at least one black and white pawn
        var wFlag = true
        var bFlag = true
        for (x in 0..7) {
            for (y in 0..7) {
                wFlag = if (ChB[x][y] == BS.WHITE_PAWN) {
                    false
                }
                else {
                    wFlag
                }
                bFlag = if (ChB[x][y] == BS.BLACK_PAWN) {
                    false
                }
                else {
                    bFlag
                }
            }
        }
        if (wFlag) {
            printBoard()
            println("Black Wins!")
            println("Bye!")
            this.keepGoing = false
            return
        }
        if (bFlag) {
            printBoard()
            println("White Wins!")
            println("Bye!")
            this.keepGoing = false
            return
        }
        //Stalemate (draw) occurs when a player can't make any valid move on their turn.
        // Stalemate! => el peón no puede avanzar hacia adelante, no puede comeer , tampoco puede tomaar un peon al paso, esta situacion
        // se debe dar par todos los peones del mismo color y tocrle jugar a es color

        // Evalúo si (xy es un peón) y ((puedemover haca aldelant) o (puede comer haci alguna costado) o (se prudujo un passat))

//        private var lastWhiteMove = ""
//        private var lastBlackMove = ""
//

        for (f in 0..7) {
            for (c in 0..7) {
                if (turn) { // juegan las blancas
                    if (c in 1..6) {
                        if ((ChB[f][c] == BS.WHITE_PAWN) && ((ChB[f - 1][c] == BS.FREE) || ((ChB[f - 1][c - 1] == BS.BLACK_PAWN) || (ChB[f - 1][c + 1] == BS.BLACK_PAWN)) || (lastBlackMove == calPassant(
                                f, c, true, false
                            ) || lastBlackMove == calPassant(f, c, true, true)))
                        ) {
                            return
                        }
                    }
                    if (c == 0) {
                        if ((ChB[f][c] == BS.WHITE_PAWN) && ((ChB[f - 1][c] == BS.FREE) || (ChB[f - 1][c + 1] == BS.BLACK_PAWN) || (lastBlackMove == calPassant(
                                f, c, true, false
                            ) || lastBlackMove == calPassant(f, c, true, true)))
                        ) {
                            return
                        }
                    }
                    if (c == 7) {
                        if ((ChB[f][c] == BS.WHITE_PAWN) && ((ChB[f - 1][c] == BS.FREE) || (ChB[f - 1][c - 1] == BS.BLACK_PAWN) || (lastBlackMove == calPassant(
                                f, c, true, false
                            ) || lastBlackMove == calPassant(f, c, true, true)))
                        ) {
                            return
                        }
                    }
                }
                else {           //juegan las negras
                    if (c in 1..6) {
                        if ((ChB[f][c] == BS.BLACK_PAWN) && ((ChB[f + 1][c] == BS.FREE) || ((ChB[f + 1][c + 1] == BS.WHITE_PAWN) || (ChB[f + 1][c - 1] == BS.WHITE_PAWN)) || (lastBlackMove == calPassant(
                                f, c, false, false
                            ) || lastBlackMove == calPassant(f, c, false, true)))
                        ) {
                            return
                        }
                    }
                    if (c == 0) {
                        if ((ChB[f][c] == BS.BLACK_PAWN) && ((ChB[f + 1][c] == BS.FREE) || (ChB[f + 1][c + 1] == BS.WHITE_PAWN) || (lastBlackMove == calPassant(
                                f, c, false, false
                            ) || lastBlackMove == calPassant(f, c, false, true)))
                        ) {
                            return
                        }
                    }
                    if (c == 7) {
                        if ((ChB[f][c] == BS.BLACK_PAWN) && ((ChB[f + 1][c] == BS.FREE) || (ChB[f + 1][c - 1] == BS.WHITE_PAWN) || (lastBlackMove == calPassant(
                                f, c, false, false
                            ) || lastBlackMove == calPassant(f, c, false, true)))
                        ) {
                            return
                        }
                    }
                }
            }
        }
        printBoard()
        println("Stalemate!")
        println("Bye!")
        this.keepGoing = false
        return
    }
}


fun main() {
    println("Pawns-Only Chess")
    println("First Player's name:")
    print("> ")
    val whitePlayer = readln()
    println("Second Player's name:")
    print("> ")
    var input:String=""
    val blackPlayer = readln()
    val game = Game(whitePlayer, blackPlayer)
    game.printBoard()
    while (game.keepGoing) {
        game.PrintTurn()
        print("> ")
        input= readln()
        game.Play(input)
        if (input=="Exit" || input=="exit") return
       // if (game.keepGoing && game.validImput) {
        if (game.validImput && game.keepGoing) {
            game.printBoard()
        }
    }
}