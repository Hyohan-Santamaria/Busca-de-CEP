import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Busca {
    @SerializedName("cep")
    private String cep;

    @SerializedName("logradouro")
    private String logradouro;

    @SerializedName("bairro")
    private String bairro;

    @SerializedName("localidade")
    private String localidade;

    @SerializedName("uf")
    private String uf;

    public String getCep() { return cep; }
    public String getLogradouro() { return logradouro; }
    public String getBairro() { return bairro; }
    public String getLocalidade() { return localidade; }
    public String getUf() { return uf; }
}

public class Main {
    public static void main(String[] args) {
        Scanner leitura = new Scanner(System.in);
        String busca = "";

        List<Busca> buscas = new ArrayList<>();
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        while (!busca.equalsIgnoreCase("sair")) {
            System.out.println("Digite o CEP que deseja buscar ou escreva { sair }");
            busca = leitura.nextLine();

            if (busca.equalsIgnoreCase("sair")) {
                break;
            }

            // Remove o hífen do CEP, se existir
            busca = busca.replace("-", "").trim();

            // Verifica se o CEP tem exatamente 8 números
            if (busca.length() != 8 || !busca.matches("\\d{8}")) {
                System.out.println("CEP inválido! Certifique-se de digitar apenas números.");
                continue;
            }

            String url = "https://viacep.com.br/ws/" + busca + "/json/";

            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                Busca resultado = gson.fromJson(response.body(), Busca.class);

                if (resultado.getCep() == null) {
                    System.out.println("CEP inválido ou não encontrado.");
                    continue;
                }

                buscas.add(resultado);

                System.out.println("CEP: " + resultado.getCep());
                System.out.println("Logradouro: " + resultado.getLogradouro());
                System.out.println("Bairro: " + resultado.getBairro());
                System.out.println("Cidade: " + resultado.getLocalidade());
                System.out.println("Estado: " + resultado.getUf());
                System.out.println("-----------------------------");

            } catch (IOException | InterruptedException e) {
                System.out.println("Erro ao buscar o CEP: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("Houve erro de argumento na busca, verifique o endereço.");
            }
        }

        try (FileWriter escrita = new FileWriter("CEP.json")) {
            escrita.write(gson.toJson(buscas));
            System.out.println("Dados salvos em CEP.json");
        } catch (IOException e) {
            System.out.println("Erro ao salvar o JSON: " + e.getMessage());
        }

        System.out.println("O programa finalizou corretamente.");
    }
}
