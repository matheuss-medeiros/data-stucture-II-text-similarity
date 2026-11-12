//Beatriz de Assis Siqueira, RA:10741570 
//Daniel Arais Motta, RA:10419718 
//Matheus Santos Medeiros, RA:10748040 
//Pedro Araujo Botelho, RA:10738317

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class Main {

    // 1 para hashMultiplicativo (DJB2), 2 para hashPolinomial
    private static final int TIPO_HASH = 1;
    private static final String NOME_HASH = (TIPO_HASH == 1) ? "hashMultiplicativo" : "hashPolinomial";
    private static final String METRICA = "Cosseno";
    private static final String ARQUIVO_SAIDA = "resultado.txt";

    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("Uso: java Main <diretorio_documentos> <limiar> <modo> [argumentos_opcionais]");
            return;
        }

        File diretorio = new File(args[0]);
        if (!diretorio.exists() || !diretorio.isDirectory()) {
            System.err.println("Diretorio invalido!");
            return;
        }

        double limiar;
        try {
            limiar = Double.parseDouble(args[1]);
            if (limiar < 0.0 || limiar > 1.0) {
                System.err.println("O limiar deve estar entre 0.0 e 1.0!");
                return;
            }
        } catch (NumberFormatException e) {
            System.err.println("Insira um limiar valido!");
            return;
        }

        String modo = args[2];
        int k = 0;

        if (modo.equalsIgnoreCase("lista")) {
            if (args.length != 3) {
                System.err.println("Uso no modo lista: java Main <diretorio_documentos> <limiar> lista");
                return;
            }
        } else if (modo.equalsIgnoreCase("topk")) {
            if (args.length < 4) {
                System.err.println("Uso no modo topK: java Main <diretorio_documentos> <limiar> topK <K>");
                return;
            }
            try {
                k = Integer.parseInt(args[3]);
                if (k < 1) {
                    System.err.println("Insira um valor de K valido!");
                    return;
                }
            } catch (NumberFormatException e) {
                System.err.println("Insira um valor de K valido!");
                return;
            }
        } else if (modo.equalsIgnoreCase("busca")) {
            if (args.length < 5) {
                System.err.println("Uso no modo busca: java Main <diretorio_documentos> <limiar> busca <doc1> <doc2>");
                return;
            }
        } else {
            System.err.println("Modo invalido! Use: lista, topK ou busca");
            return;
        }

        try {
            Set<String> stopWords = Documento.obterStopWords();
            StringBuilder sb = new StringBuilder();

            if (modo.equalsIgnoreCase("busca")) {
                File arq1 = new File(diretorio, args[3]);
                File arq2 = new File(diretorio, args[4]);

                if (!arq1.exists() || !arq1.isFile() || !arq2.exists() || !arq2.isFile()) {
                    System.err.println("Um ou ambos os arquivos informados para busca nao existem no diretorio!");
                    return;
                }

                Documento doc1 = new Documento(arq1.getPath(), stopWords, TIPO_HASH);
                Documento doc2 = new Documento(arq2.getPath(), stopWords, TIPO_HASH);
                double similaridade = ComparadorDeDocumentos.calcularCosseno(doc1, doc2);

                // Formato exato da Página 6 para o modo busca
                sb.append("=== VERIFICADOR DE SIMILARIDADE DE TEXTOS ===\n");
                sb.append(String.format("Comparando: %s <-> %s\n", doc1.getNomeArquivo(), doc2.getNomeArquivo()));
                sb.append(String.format(Locale.US, "Similaridade calculada: %.2f\n", similaridade));
                sb.append("Métrica utilizada: ").append(METRICA).append("\n");

            } else {
                File[] todosArquivos = diretorio.listFiles();
                if (todosArquivos == null) {
                    System.err.println("Erro ao ler o diretorio informado.");
                    return;
                }

                List<File> arquivosTxt = new ArrayList<>();
                for (File arq : todosArquivos) {
                    String nome = arq.getName();
                    if (arq.isFile()
                            && nome.toLowerCase(Locale.ROOT).endsWith(".txt")
                            && !nome.equalsIgnoreCase(ARQUIVO_SAIDA)
                            && !nome.equalsIgnoreCase("stop_word.txt")) {
                        arquivosTxt.add(arq);
                    }
                }
                arquivosTxt.sort(Comparator.comparing(File::getName));

                if (arquivosTxt.size() < 2) {
                    System.err.println("Impossivel comparar pares com menos de 2 arquivos na pasta.");
                    return;
                }

                // Instancia os objetos Documento
                List<Documento> documentos = new ArrayList<>(arquivosTxt.size());
                for (File arq : arquivosTxt) {
                    documentos.add(new Documento(arq.getPath(), stopWords, TIPO_HASH));
                }

                // Compara todos os pares e armazena na Árvore AVL
                AVLTree avl = new AVLTree();
                int totalParesComparados = 0;

                for (int i = 0; i < documentos.size(); i++) {
                    Documento docA = documentos.get(i);
                    for (int j = i + 1; j < documentos.size(); j++) {
                        Documento docB = documentos.get(j);
                        double sim = ComparadorDeDocumentos.calcularCosseno(docA, docB);
                        avl.inserir(new Resultado(docA.getNomeArquivo(), docB.getNomeArquivo(), sim));
                        totalParesComparados++;
                    }
                }

                // Formato de saída esperado (Página 6)
                sb.append("=== VERIFICADOR DE SIMILARIDADE DE TEXTOS ===\n");
                sb.append("Total de documentos processados: ").append(documentos.size()).append("\n");
                sb.append("Total de pares comparados: ").append(totalParesComparados).append("\n");
                sb.append("Função hash utilizada: ").append(NOME_HASH).append("\n");
                sb.append("Métrica de similaridade: ").append(METRICA).append("\n\n");

                List<Resultado> resultadosExibidos;
                if (modo.equalsIgnoreCase("lista")) {
                    sb.append(String.format(Locale.US, "Pares com similaridade >= %.2f:\n", limiar));
                    resultadosExibidos = avl.obterAcimaDe(limiar);
                } else {
                    sb.append(String.format(Locale.US, "Pares com similaridade >= %.2f (Top %d):\n", limiar, k));
                    resultadosExibidos = avl.obterTopK(k, limiar);
                }

                for (Resultado r : resultadosExibidos) {
                    sb.append(r.toString()).append("\n");
                }

                sb.append("\nPares com menor similaridade:\n");
                for (Resultado r : avl.obterMenores()) {
                    sb.append(r.toString()).append("\n");
                }
            }

            // Imprime no terminal e grava simultaneamente em resultado.txt
            System.out.print(sb.toString());
            Files.writeString(Path.of(ARQUIVO_SAIDA), sb.toString(), StandardCharsets.UTF_8);

        } catch (IOException e) {
            System.err.println("Erro de leitura ou escrita: " + e.getMessage());
        }
    }
}