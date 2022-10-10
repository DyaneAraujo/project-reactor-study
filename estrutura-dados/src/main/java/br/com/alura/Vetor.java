package br.com.alura;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;

public class Vetor {

    //armazenamento sequencial, O(N), quanto maior, mais demora para processar

    private Aluno[] alunos = new Aluno[100];
    private int totalDeAlunos = 0;

    public void adiciona(Aluno aluno) {
        this.alunos[totalDeAlunos] = aluno;
        totalDeAlunos++;
    }

    public void adiciona(int posicao, Aluno aluno) {

        if(!posicaoValida(posicao)){
            throw new IllegalArgumentException("Posição Inválida");
        }

        //pega a última posição, e percorre ate a posição que deseja
        for (int i = totalDeAlunos - 1; i >= posicao; i-=1) {
            alunos[i+1] = alunos[i]; //empurra o aluno para direita
        }

        this.alunos[posicao] = aluno;
        totalDeAlunos++;
    }

    private boolean posicaoValida(int posicao){
        return posicao >= 0 && posicao <= totalDeAlunos;
    }

    private boolean posicaoOcupada(int posicao) {
        return posicao >= 0 && posicao < totalDeAlunos;
    }

    public Aluno pega(int posicao) {
        if (posicaoOcupada(posicao)) {
            throw new IllegalArgumentException("Posição Inválida");
        }
        return alunos[posicao];
    }

    public void remove(int posicao) {
        //começa a partir da posição e vai até o final da lista -1
        for(int i = posicao; i < totalDeAlunos; i++){
            alunos[i] = alunos[i+1]; //empurra o aluno para esquerda
        }

        //this.alunos[totalDeAlunos] = null;
        //o último aluno precisa ser null

        totalDeAlunos--;
    }

    public boolean contem(Aluno aluno) {
        for (int i = 0; i < totalDeAlunos; i++) {
            if (aluno.equals(alunos[i]))
                return true;
        }
        return false;
    }

    public int tamanho() {
        return totalDeAlunos;
    }

    public String toString() {
        return Arrays.toString(alunos); //chama o toString de todos alunos
    }

}
