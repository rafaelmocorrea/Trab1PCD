#include <stdio.h>
#include <stdlib.h>
#include <omp.h>
#include <time.h>
#include <sys/timeb.h>

#define VIVO 1
#define MORTO 0
#define HIGH 1
#define NORMAL 0

void imprime_matriz(int** matriz, int n){
    for (int i = 0; i < n; i++){
        for (int j = 0; j < n; j++){
            printf("%d ", matriz[i][j]);
        }
        printf("\n");
    }
}

int getNeighbors(int** grid, int i, int j, int n){
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

int somaMatriz(int **grid, int n){
    int count = 0;

    for (int i = 0; i < n; i++){
        for (int j = 0; j < n; j++){
            count += grid[i][j];
        }
    }

    return count;
}

void glider(int **grid){
    int lin = 1, col = 1;
    grid[lin+1][col+2] = 1;
    grid[lin+2][col  ] = 1;
    grid[lin  ][col+1] = 1;
    grid[lin+2][col+1] = 1;
    grid[lin+2][col+2] = 1;

}

void pentomino(int **grid){
    int lin = 10, col = 30;
    grid[lin  ][col+1] = 1;
    grid[lin  ][col+2] = 1;
    grid[lin+1][col  ] = 1;
    grid[lin+1][col+1] = 1;
    grid[lin+2][col+1] = 1;

}

void copia_matriz(int **destino, int **origem, int n){
    for (int i = 0; i < n; i++){
        for (int j = 0; j < n; j++){
            destino[i][j] = origem[i][j];
        }
    }
}

int main(){

    int **grid;             //grid atual
    int **newgrid;          //proximo grid
    int N = 2048;           //NxN
    int iteracoes = 100;    //Numero de iteracoes
    int cont_aux;           //Contador auxiliar
    int tipo = HIGH;        //Tipo do jogo
    int i, j, k, l;         //Auxiliares dos lacos
    long diff_laco_atual, diff_paralelo, diff_total;    //Diferenca do tempo em ms
    long media_laco;
    struct timeb fim_laco, inicio_laco, fim_prog, inicio_prog, fim_paralelo, inicio_paralelo;

    ftime(&inicio_prog);

    printf("Numero de processadores: %d\n",omp_get_num_procs());

    grid = calloc(N, sizeof(int *));
    newgrid = calloc(N, sizeof(int *));

    for (j = 0; j < N; j++){
        grid[j] = calloc(N,sizeof(int));
        newgrid[j] = calloc(N,sizeof(int));
    }

    glider(grid);
    pentomino(grid);

    printf("Condicao inicial: %d",somaMatriz(grid,N));

    ftime(&inicio_paralelo);
    for (i = 0; i < iteracoes; i++){
        ftime(&inicio_laco);
        #pragma omp parallel num_threads(1)
            {
            #pragma omp for private(k,l,cont_aux) collapse(2)
            for (k = 0; k < N; k++){
                for (l = 0; l < N; l++){
                    cont_aux = getNeighbors(grid,k,l,N);
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
        copia_matriz(grid,newgrid,N);
        if (i+1 == iteracoes)
            printf("\nGeracao %d: %d",i+1,somaMatriz(grid,N));
        ftime(&fim_laco);
        diff_laco_atual = (long) (1000.0 * (fim_laco.time-inicio_laco.time)+(fim_laco.millitm-inicio_laco.millitm));
        if (i == 0)
            media_laco = diff_laco_atual;
        media_laco = (media_laco + diff_laco_atual)/2;
    }
    ftime(&fim_paralelo);  
    ftime(&fim_prog);
    diff_paralelo = (long) (1000.0 * (fim_paralelo.time-inicio_paralelo.time)+(fim_paralelo.millitm-inicio_paralelo.millitm));
    diff_total = (long) (1000.0 * (fim_prog.time-inicio_prog.time)+(fim_prog.millitm-inicio_prog.millitm));
    printf("\nTempo total: %ums", diff_total);
    printf("\nMedia dos lacos: %ums",media_laco);
    printf("\nTempo paralelo: %ums",diff_paralelo);

    free(grid);
    free(newgrid);

    return 0;
}