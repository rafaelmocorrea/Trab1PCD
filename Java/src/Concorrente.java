public class Concorrente implements Runnable {

    private int[][] grid;
    private int[][] newgrid;
    private int N, inicio_vetor, fim_vetor, tipo;
    private static final int VIVO = 1;
    private static final int MORTO = 0;
    private static final int HIGH = 1;
    private static final int NORMAL = 0;

    public Concorrente(int[][] grid_atual, int[][] grid_futuro,int n, int i, int j, int t){
        N = n;
        inicio_vetor = i;
        fim_vetor = j;
        grid = grid_atual;
        newgrid = grid_futuro;
        tipo = t;
    }

    @Override
    public void run() {
        int cont_aux;
        for (int k = inicio_vetor; k < fim_vetor; k++){
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
    }

    public static void main(String[] args) {

        int N = 2048, iteracoes = 2000, aux;
        int[][] grid = new int[N][N];
        int[][] newgrid = new int[N][N];
        int num_threads = 4;
        int tipo = HIGH;
        long total_inicial, total_final, laco_inicial = 0, laco_final = 0;

        total_inicial = System.currentTimeMillis();

        Utilitarios.glider(grid);
        Utilitarios.pentomino(grid);
        System.out.println("Condicao inicial: "+Utilitarios.somaMatriz(grid,N));
        for (int i = 0; i < iteracoes; i++){

            if (i == 0)
                laco_inicial = System.currentTimeMillis();

            aux = 0;
            Concorrente[] threads = new Concorrente[num_threads];
            Thread[] arraythreads = new Thread[num_threads];
            for (int j = 0; j < num_threads; j++){
                if (j != num_threads - 1) {
                    threads[j] = new Concorrente(grid,newgrid,N,aux,(aux + (N/num_threads)),tipo);
                    aux = (N/num_threads) + 1;
                } else {
                    threads[j] = new Concorrente(grid,newgrid,N,aux,N,tipo);
                }
                arraythreads[j] = new Thread(threads[j]);
                arraythreads[j].start();
            }
            for (int k = 0; k < num_threads; k++){
                try {
                    arraythreads[k].join();
                } catch (InterruptedException e) {
                    System.out.println("Erro no join");
                    e.printStackTrace();
                }
            }
            Utilitarios.copiaMatriz(grid,newgrid,N);

            if (i == 0)
                laco_final = System.currentTimeMillis();

            if (i+1 == 2000)
                System.out.println("Geracao "+(i+1)+": "+Utilitarios.somaMatriz(grid,N));
        }
        total_final = System.currentTimeMillis();
        System.out.println("Tempo de um laco: "+(laco_final-laco_inicial)+"ms\nTempo total: "+(total_final-total_inicial)+"ms");
    }
}
