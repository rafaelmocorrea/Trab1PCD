public class ConcorrenteTestes implements Runnable {
    private int[][] grid;
    private int[][] newgrid;
    private int N, inicio_vetor, fim_vetor, tipo;
    private static final int VIVO = 1;
    private static final int MORTO = 0;
    private static final int HIGH = 1;
    private static final int NORMAL = 0;

    public ConcorrenteTestes(int[][] grid_atual, int[][] grid_futuro, int n, int i, int j, int t) {
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
        for (int k = inicio_vetor; k < fim_vetor; k++) {
            for (int l = 0; l < N; l++) {
                cont_aux = Utilitarios.getNeighbors(grid, k, l, N);
                if (grid[k][l] == VIVO) {
                    if (cont_aux < 2 || cont_aux >= 4) {
                        newgrid[k][l] = MORTO;
                    } else {
                        newgrid[k][l] = VIVO;
                    }
                } else {
                    if (tipo != HIGH) {
                        if (cont_aux == 3) {
                            newgrid[k][l] = VIVO;
                        } else {
                            newgrid[k][l] = MORTO;
                        }
                    } else {
                        if (cont_aux == 3 || cont_aux == 6) {
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
        int num_threads = 8;
        int tipo = HIGH;
        long total_inicial, total_final, laco_inicial = 0, laco_final = 0, paralelo_inicial = 0, paralelo_final = 0, media_laco = 0;
        long media_medias_laco = 0, media_total = 0, media_paralelo = 0;
        int n_teste = 5;
        for (int m = 0; m < n_teste; m++) {
            System.out.println("Teste "+(m+1));
            media_laco = 0;
            total_inicial = System.currentTimeMillis();
            Utilitarios.glider(grid);
            Utilitarios.pentomino(grid);
//            System.out.println("Condicao inicial: " + Utilitarios.somaMatriz(grid, N));
            paralelo_inicial = System.currentTimeMillis();
            for (int i = 0; i < iteracoes; i++) {
                laco_inicial = System.currentTimeMillis();
                aux = 0;
                ConcorrenteTestes[] threads = new ConcorrenteTestes[num_threads];
                Thread[] arraythreads = new Thread[num_threads];
                for (int j = 0; j < num_threads; j++) {
                    if (j != num_threads - 1) {
                        threads[j] = new ConcorrenteTestes(grid, newgrid, N, aux, (aux + (N / num_threads)), tipo);
                        aux = (N / num_threads) + 1;
                    } else {
                        threads[j] = new ConcorrenteTestes(grid, newgrid, N, aux, N, tipo);
                    }
                    arraythreads[j] = new Thread(threads[j]);
                    arraythreads[j].start();
                }
                for (int k = 0; k < num_threads; k++) {
                    try {
                        arraythreads[k].join();
                    } catch (InterruptedException e) {
                        System.out.println("Erro no join");
                        e.printStackTrace();
                    }
                }
                Utilitarios.copiaMatriz(grid, newgrid, N);

                laco_final = System.currentTimeMillis();
                if (i == 0)
                    media_laco = laco_final - laco_inicial;
                media_laco = (media_laco + (laco_final - laco_inicial)) / 2;

//                if (i + 1 == iteracoes)
//                    System.out.println("Geracao " + (i + 1) + ": " + Utilitarios.somaMatriz(grid, N));
            }
            paralelo_final = System.currentTimeMillis();
            total_final = System.currentTimeMillis();

            if (m == 0) {
                media_medias_laco = media_laco;
                media_paralelo = paralelo_final - paralelo_inicial;
                media_total = total_final - total_inicial;
            }

            media_medias_laco = (media_medias_laco + media_laco)/2;
            media_paralelo = (media_paralelo + (paralelo_final - paralelo_inicial))/2;
            media_total = (media_total + (total_final - total_inicial))/2;

            //System.out.println("Media de lacos: " + media_laco + "ms\nTempo total: " + (total_final - total_inicial) + "ms\nTempo em paralelo: " + (paralelo_final - paralelo_inicial) + "ms");
        }
        System.out.println("Media totais: "+media_total+"ms\nMedia paralelos: "+media_paralelo+"ms\nMedia lacos: "+media_medias_laco+"ms");
    }
}
