import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class Documento {
    private final String nomeArquivo;
    private final HashTable tabelaFrequencias;

    // Lista pré-definida de stop words para o português (Descrição do Problema - Pág. 2)
    private static final Set<String> STOP_WORDS_PADRAO = Set.of(
        "a", "o", "e", "de", "do", "da", "em", "para", "com", "um", "uma",
        "os", "as", "que", "é", "são", "na", "no", "por", "dos", "das",
        "se", "como", "mais", "mas", "ao"
    );

    public Documento(String caminhoArquivo, Set<String> stopWords, int tipoHash) throws IOException {
        Path path = Path.of(caminhoArquivo);
        this.nomeArquivo = path.getFileName().toString();

        // Capacidade 1009 (número primo) para reduzir colisões nas funções de dispersão
        this.tabelaFrequencias = new HashTable(1009, tipoHash);

        processarArquivo(path, stopWords);
    }

    /**
     * Realiza a normalização obrigatória do texto e popula a Tabela Hash:
     * 1. Converter todo o texto para letras minúsculas.
     * 2. Remover toda pontuação e caracteres não-alfanuméricos.
     * 3. Realizar a tokenização (separação em palavras).
     * 4. Remover stop words e contabilizar a frequência de cada palavra única.
     */
    private void processarArquivo(Path caminho, Set<String> stopWords) throws IOException {
        String conteudo = Files.readString(caminho, StandardCharsets.UTF_8);

        conteudo = conteudo.toLowerCase(Locale.ROOT);
        conteudo = conteudo.replaceAll("[^\\p{L}\\p{N}\\s]", " ");

        String[] tokens = conteudo.split("\\s+");

        for (String token : tokens) {
            if (!token.isEmpty() && !stopWords.contains(token)) {
                int frequenciaAtual = tabelaFrequencias.get(token);
                tabelaFrequencias.put(token, frequenciaAtual + 1);
            }
        }
    }

    /**
     * Carrega stop_word.txt se estiver no diretório de execução; caso contrário,
     * retorna a lista pré-definida para o português garantindo execução autônoma.
     */
    public static Set<String> obterStopWords() throws IOException {
        File arquivo = new File("stop_word.txt");
        if (arquivo.exists() && arquivo.isFile()) {
            Set<String> stopWords = new HashSet<>();
            List<String> linhas = Files.readAllLines(arquivo.toPath(), StandardCharsets.UTF_8);
            for (String linha : linhas) {
                String palavra = linha.trim().toLowerCase(Locale.ROOT);
                if (!palavra.isEmpty()) {
                    stopWords.add(palavra);
                }
            }
            return stopWords;
        }
        return STOP_WORDS_PADRAO;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public HashTable getTabelaFrequencias() {
        return tabelaFrequencias;
    }
}