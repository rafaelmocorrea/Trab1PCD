public abstract class Utilitarios {
    public static int getNeighbors(int [][] grid, int i, int j, int n){
        int count = 0;
        int i_local, j_local;
        for (int k = -1; k < 2; k++){
            for (int l = -1; l < 2; l++){
                i_local = i + k;
                j_local = j + l;
                if (i_local < 0){ // infinito pela esquerda
                    i_local = n - 1;
                } else if (i_local == n){
                    i_local = 0; // infinito pela direita
                }
                if (j_local < 0){
                    j_local = n - 1; // infinito por cima
                } else if (j_local == n){
                    j_local = 0; // infinito por baixo
                }
                if (grid[i_local][j_local] > 0 && (i_local != i || j_local != j)){
                    count++;
                }
            }
        }

        return count;
    }

    public static void imprimeMatriz(int[][] matriz, int n){
        for (int i = 0; i < n; i++){
            for (int j = 0; j < n; j++){
                System.out.printf(matriz[i][j]+" ");
            }
            System.out.printf("\n");
        }
    }

    public static int somaMatriz(int[][] grid, int n){
        int count = 0;

        for (int i = 0; i < n; i++){
            for (int j = 0; j < n; j++){
                count += grid[i][j];
            }
        }

        return count;
    }

    public static void glider(int[][] grid) {
        int lin = 1, col = 1;
        grid[lin+1][col+2] = 1;
        grid[lin+2][col  ] = 1;
        grid[lin  ][col+1] = 1;
        grid[lin+2][col+1] = 1;
        grid[lin+2][col+2] = 1;
    }

    public static void pentomino(int[][] grid) {
        int lin = 10, col = 30;
        grid[lin  ][col+1] = 1;
        grid[lin  ][col+2] = 1;
        grid[lin+1][col  ] = 1;
        grid[lin+1][col+1] = 1;
        grid[lin+2][col+1] = 1;
    }

    public static void copiaMatriz(int[][] destino, int[][] origem, int n) {
        for (int i = 0; i < n; i++){
            for (int j = 0; j < n; j++){
                destino[i][j] = origem[i][j];
            }
        }
    }
}