import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Documento {
    private String nomeArquivo;
    private HashTable tabelaFrequencias;

    public Documento(String caminhoArquivo, Set<String> stopWords, int tipoHash) throws IOException {
        Path path = Path.of(caminhoArquivo);
        this.nomeArquivo = path.getFileName().toString();
        
        // 1009 é um número primo, recomendado para reduzir colisões iniciais
        this.tabelaFrequencias = new HashTable(1009, tipoHash); 
        
        processarArquivo(path, stopWords);
    }

    private void processarArquivo(Path caminho, Set<String> stopWords) throws IOException {
        String conteudo = Files.readString(caminho);
        
        // Normalização
        conteudo = conteudo.toLowerCase();
        conteudo = conteudo.replaceAll("[^\\p{L}\\p{N}\\s]", "");

        // Tokenização
        String[] tokens = conteudo.split("\\s+");

        for (String token : tokens) {
            if (!token.isEmpty() && !stopWords.contains(token)) {
                tabelaFrequencias.put(token);
            }
        }
    }

    public static Set<String> carregarStopWords(String caminhoStopWords) throws IOException {
        Set<String> stopWords = new HashSet<>();
        List<String> linhas = Files.readAllLines(Path.of(caminhoStopWords));
        
        for (String linha : linhas) {
            stopWords.add(linha.trim().toLowerCase());
        }
        return stopWords;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public HashTable getTabelaFrequencias() {
        return tabelaFrequencias;
    }
}