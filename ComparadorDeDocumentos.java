import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ComparadorDeDocumentos {

    public static double calcularCosseno(HashTable tabelaA, HashTable tabelaB) {
        List<String> palavrasA = tabelaA.getPalavras();
        List<String> palavrasB = tabelaB.getPalavras();

        if (palavrasA.isEmpty() || palavrasB.isEmpty()) {
            return 0.0;
        }

        double produtoEscalar = 0.0;
        double somaQuadradosA = 0.0;
        double somaQuadradosB = 0.0;

        for (String palavra : palavrasA) {
            int freqA = tabelaA.buscarFrequencia(palavra);
            int freqB = tabelaB.buscarFrequencia(palavra);
            produtoEscalar += (double) freqA * freqB;
            somaQuadradosA += (double) freqA * freqA;
        }

        for (String palavra : palavrasB) {
            int freqB = tabelaB.buscarFrequencia(palavra);
            somaQuadradosB += (double) freqB * freqB;
        }

        double denominador = Math.sqrt(somaQuadradosA) * Math.sqrt(somaQuadradosB);
        if (denominador == 0.0) {
            return 0.0;
        }

        return produtoEscalar / denominador;
    }

    public static double calcularJaccard(HashTable tabelaA, HashTable tabelaB) {
        List<String> palavrasA = tabelaA.getPalavras();
        List<String> palavrasB = tabelaB.getPalavras();

        if (palavrasA.isEmpty() && palavrasB.isEmpty()) {
            return 1.0;
        }
        if (palavrasA.isEmpty() || palavrasB.isEmpty()) {
            return 0.0;
        }

        Set<String> uniao = new HashSet<>(palavrasA);
        uniao.addAll(palavrasB);

        int intersecao = 0;
        for (String palavra : palavrasA) {
            if (tabelaB.buscarFrequencia(palavra) > 0) {
                intersecao++;
            }
        }

        return (double) intersecao / uniao.size();
    }
}