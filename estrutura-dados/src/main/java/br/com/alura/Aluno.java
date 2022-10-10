package br.com.alura;

public class Aluno {

    private String nome;

    public Aluno(final String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    @Override
    public boolean equals(final Object obj) {
        Aluno outro = (Aluno) obj;
        return outro.getNome().equals(this.nome);
    }

    @Override
    public String toString() {
        return this.nome;
    }
}
