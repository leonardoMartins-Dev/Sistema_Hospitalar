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
 * Funções de ajuda para as telas (Leonardo Gonzaga, emergência e cirurgia):
 *   api.mostrarMensagem(elemento, texto, tipo)  caixa de aviso ('sucesso' ou 'erro')
 *   api.escapar(texto)                          use ao montar HTML com dados digitados
 *   api.formatarData('2026-11-20')              -> '20/11/2026'
 *   api.formatarHora('07:00:00')                -> '07:00'
 *   api.formatarDataHora('2026-11-20T07:15:30') -> '20/11/2026 07:15'
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

  // caixa de mensagem (sucesso ou erro) dentro da página
  function mostrarMensagem(elemento, texto, tipo) {
    if (!elemento) return;

    elemento.textContent = texto;
    elemento.className = 'alert ' + (tipo === 'sucesso' ? 'alert-success' : 'alert-danger');
    elemento.hidden = false;
  }

  // evita que um texto digitado vire HTML ao montar as tabelas
  function escapar(texto) {
    if (texto === null || texto === undefined) return '';
    return String(texto)
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#39;');
  }

  // '2026-11-20' -> '20/11/2026'
  function formatarData(data) {
    if (!data) return '--';
    const partes = String(data).split('-');
    return partes[2] + '/' + partes[1] + '/' + partes[0];
  }

  // '07:00:00' -> '07:00'
  function formatarHora(hora) {
    if (!hora) return '--';
    return String(hora).substring(0, 5);
  }

  // '2026-11-20T07:15:30' -> '20/11/2026 07:15'
  function formatarDataHora(dataHora) {
    if (!dataHora) return '--';
    const [data, hora] = String(dataHora).split('T');
    return formatarData(data) + ' ' + formatarHora(hora);
  }

  return {
    get: (url) => chamar('GET', url),
    post: (url, corpo) => chamar('POST', url, corpo),
    put: (url, corpo) => chamar('PUT', url, corpo),
    delete: (url) => chamar('DELETE', url),
    mostrarMensagem,
    escapar,
    formatarData,
    formatarHora,
    formatarDataHora,
  };
})();
