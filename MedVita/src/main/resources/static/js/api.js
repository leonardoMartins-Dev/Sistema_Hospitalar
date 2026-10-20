/* ==========================================================================
   API.JS - FUNÇÕES PARA AS TELAS CHAMAREM A API (Sprint 2)
   Responsável: Leonardo Gonzaga

   COMO USAR NUMA PÁGINA
   1. No <head>, as duas tags do CSRF (o Spring Security exige o token em
      todo POST/PUT/DELETE):
        <meta name="_csrf" th:content="${_csrf.token}"/>
        <meta name="_csrf_header" th:content="${_csrf.headerName}"/>
   2. Antes do script da página:
        <script th:src="@{/js/api.js}"></script>
   3. No script da página:
        const consultas = await api.get("/consultas/minhas");
        await api.post("/consultas/emergencias", { pacienteId: 8, motivo: "...", prioridade: "ALTA" });
        await api.put("/consultas/5/concluir", { observacoes: "..." });
        await api.delete("/profissionais/3");

   ERROS
   Se a resposta não for ok (400, 404, 409, 500...), a função lança um Error
   com a mensagem que veio do GlobalExceptionHandler ({"message": "..."}).
   Use try/catch e mostre erro.message na tela.

   FUNÇÕES DE AJUDA
   api.mostrarMensagem(elemento, texto, tipo)  caixa de aviso ("sucesso" ou "erro")
   api.escapar(texto)                          use ao montar HTML com dados digitados
   api.formatarData("2026-11-20")              -> "20/11/2026"
   api.formatarHora("07:00:00")                -> "07:00"
   ========================================================================== */

const api = (function () {
  // LÊ O TOKEN CSRF DAS DUAS <meta> DA PÁGINA
  function cabecalhos() {
    const headers = { "Content-Type": "application/json" };

    const token = document.querySelector('meta[name="_csrf"]');
    const header = document.querySelector('meta[name="_csrf_header"]');

    if (token && header) {
      headers[header.getAttribute("content")] = token.getAttribute("content");
    }

    return headers;
  }

  // FAZ A REQUISIÇÃO E TRATA A RESPOSTA
  async function requisicao(metodo, url, corpo) {
    const opcoes = {
      method: metodo,
      headers: cabecalhos(),
      credentials: "same-origin",
    };

    if (corpo !== undefined) {
      opcoes.body = JSON.stringify(corpo);
    }

    const resposta = await fetch(url, opcoes);

    if (!resposta.ok) {
      let mensagem = "Erro " + resposta.status;

      try {
        const erro = await resposta.json();
        if (erro && erro.message) {
          mensagem = erro.message;
        }
      } catch (e) {
        // a resposta não era JSON: fica a mensagem com o código
      }

      throw new Error(mensagem);
    }

    // 204 (sem conteúdo) ou corpo vazio
    if (resposta.status === 204) {
      return null;
    }

    const texto = await resposta.text();
    return texto ? JSON.parse(texto) : null;
  }

  // CAIXA DE MENSAGEM (sucesso ou erro) DENTRO DA PÁGINA
  function mostrarMensagem(elemento, texto, tipo) {
    if (!elemento) return;

    elemento.textContent = texto;
    elemento.className = "alert " + (tipo === "sucesso" ? "alert-success" : "alert-danger");
    elemento.hidden = false;
  }

  // EVITA QUE UM TEXTO DIGITADO VIRE HTML AO MONTAR AS TABELAS
  function escapar(texto) {
    if (texto === null || texto === undefined) return "";
    return String(texto)
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;")
      .replace(/'/g, "&#39;");
  }

  // "2026-11-20" -> "20/11/2026"
  function formatarData(data) {
    if (!data) return "--";
    const partes = String(data).split("-");
    return partes[2] + "/" + partes[1] + "/" + partes[0];
  }

  // "07:00:00" -> "07:00"
  function formatarHora(hora) {
    if (!hora) return "--";
    return String(hora).substring(0, 5);
  }

  // "2026-11-20T07:15:30" -> "20/11/2026 07:15"
  function formatarDataHora(dataHora) {
    if (!dataHora) return "--";
    const [data, hora] = String(dataHora).split("T");
    return formatarData(data) + " " + formatarHora(hora);
  }

  return {
    get: (url) => requisicao("GET", url),
    post: (url, corpo) => requisicao("POST", url, corpo),
    put: (url, corpo) => requisicao("PUT", url, corpo),
    delete: (url) => requisicao("DELETE", url),
    mostrarMensagem,
    escapar,
    formatarData,
    formatarHora,
    formatarDataHora,
  };
})();
