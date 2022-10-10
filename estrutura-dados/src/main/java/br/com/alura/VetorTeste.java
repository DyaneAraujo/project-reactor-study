package br.com.alura;

public class VetorTeste {

    public static void main(String[] args) {
        
        Aluno aluno1 = new Aluno("Dyane");
        Aluno aluno2 = new Aluno("Rafael");

        Vetor lista = new Vetor();

        System.out.println(lista.tamanho());

        lista.adiciona(aluno1);
        lista.adiciona(aluno2);

        System.out.println(lista.tamanho());

        System.out.println(lista);

        System.out.println(lista.contem(aluno2));

        Aluno aluno3 = new Aluno("Guilherme");

        lista.adiciona(1, aluno3);

        System.out.println(lista);

    }
}
