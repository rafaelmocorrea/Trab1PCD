public class Serial {

    private int[][] grid;
    private int[][] newgrid;
    private int N;
    private static final int VIVO = 1;
    private static final int MORTO = 0;
    private static final int HIGH = 1;
    private static final int NORMAL = 0;

    public Serial (int n){
        N = n;
        grid = new int[N][N];
        newgrid = new int[N][N];
        Utilitarios.glider(grid);
        Utilitarios.pentomino(grid);
    }

    public void executar(int iteracoes,int tipo) {
        int cont_aux;
        System.out.println("Condicao inicial: "+Utilitarios.somaMatriz(grid,N));

        for (int i = 0; i < iteracoes; i++){
            for (int k = 0; k < N; k++){
                for (int l = 0; l < N; l++){
                    cont_aux = Utilitarios.getNeighbors(grid,k,l,N);
                    if (grid[k][l] == VIVO){
                        if (cont_aux < 2 || cont_aux >= 4){
                            newgrid[k][l] = MORTO;
                        } else {
                            newgrid[k][l] = VIVO;
                        }
                    } else {
                        if (tipo != HIGH){
                            if (cont_aux == 3){
                                newgrid[k][l] = VIVO;
                            } else {
                                newgrid[k][l] = MORTO;
                            }
                        } else {
                            if (cont_aux == 3 || cont_aux == 6){
                                newgrid[k][l] = VIVO;
                            } else {
                                newgrid[k][l] = MORTO;
                            }
                        }
                    }
                }
            }
            Utilitarios.copiaMatriz(grid,newgrid,N);
            if (i+1 == 2000)
                System.out.println("Geracao "+(i+1)+": "+Utilitarios.somaMatriz(grid,N));

        }
    }

    public static void main(String[] args) {
        Serial normal = new Serial(2048);
        normal.executar(2000,HIGH);
    }
}
