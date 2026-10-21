/*
 * =========================================================
 * api.js: AS TELAS CHAMAM A API DO MEDVITA POR AQUI
 * =========================================================
 * Responsável: Leonardo Martins (Sprint 2)
 *
 * Como usar numa página:
 *   1. No <head>, as duas tags do token CSRF (o CSRF continua ligado):
 *        <meta name="_csrf" th:content="${_csrf.token}" />
 *        <meta name="_csrf_header" th:content="${_csrf.headerName}" />
 *   2. Antes do </body>:
 *        <script th:src="@{/js/api.js}"></script>
 *        <script> ...o script da página... </script>
 *
 * Funções:
 *   api.get(url)            api.post(url, corpo)
 *   api.put(url, corpo)     api.delete(url)
 *
 * Todas devolvem uma Promise com o JSON da resposta (ou null quando a
 * resposta vem vazia, como no 204 do DELETE). Se o servidor responder com
 * erro, lançam um Error com a mensagem que o GlobalExceptionHandler mandou
 * no {"message": "..."}. Exemplo:
 *
 *   try {
 *     await api.post('/profissionais', corpo);
 *   } catch (erro) {
 *     mostrarMensagem(erro.message);   // ex.: "Já existe um usuário com este e-mail"
 *   }
 */
const api = (() => {
  function cabecalhos() {
    const headers = { 'Content-Type': 'application/json', Accept: 'application/json' };
    const token = document.querySelector('meta[name="_csrf"]');
    const nomeCabecalho = document.querySelector('meta[name="_csrf_header"]');
    if (token && nomeCabecalho) {
      headers[nomeCabecalho.content] = token.content;
    }
    return headers;
  }

  async function chamar(metodo, url, corpo) {
    const resposta = await fetch(url, {
      method: metodo,
      headers: cabecalhos(),
      body: corpo === undefined ? undefined : JSON.stringify(corpo),
    });

    const texto = await resposta.text();
    let dados = null;
    let ehJson = true;
    if (texto) {
      try {
        dados = JSON.parse(texto);
      } catch (e) {
        ehJson = false;
      }
    }

    if (!resposta.ok) {
      throw new Error((dados && dados.message) || 'Erro inesperado');
    }
    if (!ehJson) {
      // deu certo mas não veio JSON: em geral a sessão expirou e veio a página de login
      throw new Error('Resposta inesperada do servidor. Recarregue a página e entre de novo.');
    }
    return dados;
  }

  return {
    get: (url) => chamar('GET', url),
    post: (url, corpo) => chamar('POST', url, corpo),
    put: (url, corpo) => chamar('PUT', url, corpo),
    delete: (url) => chamar('DELETE', url),
  };
})();
