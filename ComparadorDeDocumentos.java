import java.util.List;

public class ComparadorDeDocumentos {

    /**
     * Recebe dois objetos Documento e retorna a Similaridade de Cosseno entre eles.
     * Fórmula: cos(theta) = (A . B) / (||A|| * ||B||)
     */
    public static double calcularCosseno(Documento docA, Documento docB) {
        HashTable tabelaA = docA.getTabelaFrequencias();
        HashTable tabelaB = docB.getTabelaFrequencias();

        List<String> palavrasA = tabelaA.getPalavras();
        List<String> palavrasB = tabelaB.getPalavras();

        if (palavrasA.isEmpty() || palavrasB.isEmpty()) {
            return 0.0;
        }

        double produtoEscalar = 0.0;
        double somaQuadradosA = 0.0;
        double somaQuadradosB = 0.0;

        for (String palavra : palavrasA) {
            int freqA = tabelaA.get(palavra);
            int freqB = tabelaB.get(palavra);
            produtoEscalar += (double) freqA * freqB;
            somaQuadradosA += (double) freqA * freqA;
        }

        for (String palavra : palavrasB) {
            int freqB = tabelaB.get(palavra);
            somaQuadradosB += (double) freqB * freqB;
        }

        double denominador = Math.sqrt(somaQuadradosA) * Math.sqrt(somaQuadradosB);
        if (denominador == 0.0) {
            return 0.0;
        }

        return produtoEscalar / denominador;
    }
}