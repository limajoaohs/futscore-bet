'use strict';

const $ = (s) => document.querySelector(s);
const esc = (v) => String(v ?? '').replace(/[&<>"']/g, (c) => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
const brl = (n) => Number(n).toLocaleString('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
const ROTULO = { CASA: 'Casa', EMPATE: 'Empate', FORA: 'Fora' };

let sessao = lerSessao();
let partidas = [];
let selecao = null; // { partida, palpite, odd }
let aba = 'jogos';

function lerSessao() {
  try { return JSON.parse(localStorage.getItem('sessao')) || null; } catch { return null; }
}
function salvarSessao(s) {
  sessao = s;
  try { s ? localStorage.setItem('sessao', JSON.stringify(s)) : localStorage.removeItem('sessao'); } catch { /* sem storage: sessao so na memoria */ }
}

async function api(caminho, opcoes = {}) {
  const r = await fetch(caminho, { headers: { 'Content-Type': 'application/json' }, ...opcoes });
  const corpo = r.status === 204 ? null : await r.json().catch(() => null);
  if (!r.ok) {
    const msg = corpo?.errors?.length ? corpo.errors.join(' ') : corpo?.message || `Erro ${r.status}`;
    throw new Error(msg);
  }
  return corpo;
}
const credenciais = () => `usuarioId=${sessao.id}&codigoAcesso=${encodeURIComponent(sessao.codigo)}`;

function toast(msg) {
  const t = $('#toast');
  t.textContent = msg;
  t.classList.add('on');
  clearTimeout(toast.timer);
  toast.timer = setTimeout(() => t.classList.remove('on'), 2800);
}

async function atualizarUsuario() {
  if (!sessao) return;
  try {
    const u = await api(`/usuarios/${sessao.id}`);
    salvarSessao({ ...sessao, nome: u.nome, saldo: u.saldo, perfil: u.perfil });
  } catch { salvarSessao(null); }
}

function renderNav() {
  const nav = $('#nav-usuario');
  $('[data-aba="admin"]').hidden = sessao?.perfil !== 'ADMIN';
  if (!sessao) {
    nav.innerHTML = `<button class="btn sec" id="btn-entrar">Entrar</button><button class="btn primario" id="btn-cadastrar" style="width:auto">Cadastrar</button>`;
    $('#btn-entrar').onclick = () => abrirAuth(false);
    $('#btn-cadastrar').onclick = () => abrirAuth(true);
    return;
  }
  nav.innerHTML = `<span class="saldo" title="Créditos virtuais">C$ ${brl(sessao.saldo)}</span>
    <span>${esc(sessao.nome)}</span><button class="btn sec" id="btn-sair">Sair</button>`;
  $('#btn-sair').onclick = () => { salvarSessao(null); selecao = null; trocarAba('jogos'); };
}

/* ---------- Jogos ---------- */
async function renderJogos() {
  const el = $('#conteudo');
  try { partidas = await api('/partidas'); } catch (e) { el.innerHTML = `<p class="vazio-grande">${esc(e.message)}</p>`; return; }
  if (!partidas.length) { el.innerHTML = '<p class="vazio-grande">Nenhuma partida com mercado aberto agora.</p>'; return; }
  let html = '', diaAtual = '';
  for (const p of partidas) {
    const d = new Date(p.dataHora);
    const dia = d.toLocaleDateString('pt-BR', { weekday: 'long', day: '2-digit', month: 'short' });
    if (dia !== diaAtual) { html += `<h3 class="dia">${esc(dia)}</h3>`; diaAtual = dia; }
    html += `<article class="jogo">
      <div><div class="times">${esc(p.timeMandante)} <span class="meta">x</span> ${esc(p.timeVisitante)}</div>
      <div class="meta">${d.toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' })} · ${esc(p.campeonatoNome)}</div></div>
      <div class="odds">${['CASA', 'EMPATE', 'FORA'].map((k) => {
        const odd = p['odd' + ROTULO[k]];
        const sel = selecao && selecao.partida.id === p.id && selecao.palpite === k;
        return `<button class="odd${sel ? ' sel' : ''}" data-id="${p.id}" data-palpite="${k}" aria-pressed="${!!sel}"
          aria-label="${esc(ROTULO[k])} odd ${odd}"><small>${k === 'EMPATE' ? 'X' : k === 'CASA' ? '1' : '2'}</small><b>${Number(odd).toFixed(2)}</b></button>`;
      }).join('')}</div></article>`;
  }
  el.innerHTML = html;
  el.querySelectorAll('.odd').forEach((b) => b.onclick = () => selecionar(Number(b.dataset.id), b.dataset.palpite));
}

function selecionar(id, palpite) {
  const p = partidas.find((x) => x.id === id);
  const mesma = selecao && selecao.partida.id === id && selecao.palpite === palpite;
  selecao = mesma ? null : { partida: p, palpite, odd: p['odd' + ROTULO[palpite]] };
  renderJogos();
  renderBilhete();
}

/* ---------- Bilhete ---------- */
function renderBilhete() {
  const el = $('#bilhete');
  if (!selecao) { el.innerHTML = '<h2>Bilhete</h2><p class="vazio">Clique em uma odd para montar sua aposta.</p>'; return; }
  const { partida: p, palpite, odd } = selecao;
  el.innerHTML = `<h2>Bilhete</h2>
    <div class="selecao">
      <div class="meta">${esc(p.timeMandante)} x ${esc(p.timeVisitante)}</div>
      <div class="linha"><span>${esc(palpite === 'EMPATE' ? 'Empate' : palpite === 'CASA' ? p.timeMandante : p.timeVisitante)}</span><span class="valor">${Number(odd).toFixed(2)}</span></div>
    </div>
    <label>Valor (C$) <input id="valor" type="number" min="1" max="10000" step="0.01" value="10"></label>
    <div class="atalhos">${[10, 50, 100, 500].map((v) => `<button type="button" data-v="${v}">+${v}</button>`).join('')}</div>
    <div class="linha"><span>Retorno potencial</span><span class="valor" id="retorno"></span></div>
    <button class="btn primario" id="btn-apostar">${sessao ? 'Apostar' : 'Entrar para apostar'}</button>
    <button class="btn link" id="btn-limpar">Limpar</button>`;
  const input = $('#valor');
  const calc = () => { $('#retorno').textContent = 'C$ ' + brl((Number(input.value) || 0) * odd); };
  input.oninput = calc; calc();
  el.querySelectorAll('[data-v]').forEach((b) => b.onclick = () => { input.value = ((Number(input.value) || 0) + Number(b.dataset.v)).toFixed(2); calc(); });
  $('#btn-limpar').onclick = () => { selecao = null; renderBilhete(); renderJogos(); };
  $('#btn-apostar').onclick = apostar;
}

async function apostar() {
  if (!sessao) return abrirAuth(false);
  const btn = $('#btn-apostar');
  btn.disabled = true;
  try {
    await api(`/apostas?${credenciais()}`, {
      method: 'POST',
      body: JSON.stringify({ partidaId: selecao.partida.id, palpite: selecao.palpite, valor: Number($('#valor').value) }),
    });
    toast('Aposta registrada! Boa sorte.');
    selecao = null;
    await atualizarUsuario();
    renderNav(); renderBilhete(); renderJogos();
  } catch (e) {
    toast(e.message);
    btn.disabled = false;
  }
}

/* ---------- Minhas apostas ---------- */
async function renderApostas() {
  const el = $('#conteudo');
  if (!sessao) { el.innerHTML = '<p class="vazio-grande">Entre na sua conta para ver suas apostas.</p>'; return; }
  try {
    const lista = await api(`/apostas?${credenciais()}`);
    if (!lista.length) { el.innerHTML = '<p class="vazio-grande">Você ainda não fez apostas.</p>'; return; }
    el.innerHTML = lista.map((a) => `<article class="aposta">
      <div class="linha"><strong>${esc(a.partida)}</strong><span class="tag ${esc(a.status)}">${esc(a.status)}</span></div>
      <div class="linha meta"><span>${esc(ROTULO[a.palpite])} @ ${Number(a.odd).toFixed(2)}${a.placar ? ' · Placar ' + esc(a.placar) : ''}</span>
      <span>${new Date(a.criadaEm).toLocaleString('pt-BR', { dateStyle: 'short', timeStyle: 'short' })}</span></div>
      <div class="linha"><span>Valor C$ ${brl(a.valor)}</span><span class="valor">${a.status === 'PERDIDA' ? 'C$ 0,00' : a.status === 'ANULADA' ? 'Devolvido' : 'Retorno C$ ' + brl(a.retornoPotencial)}</span></div>
    </article>`).join('');
  } catch (e) { el.innerHTML = `<p class="vazio-grande">${esc(e.message)}</p>`; }
}

/* ---------- Admin ---------- */
async function renderAdmin() {
  const el = $('#conteudo');
  partidas = await api('/partidas').catch(() => []);
  if (!partidas.length) { el.innerHTML = '<p class="vazio-grande">Nenhuma partida em aberto.</p>'; return; }
  el.innerHTML = '<p class="meta">Registrar placar final encerra o mercado e liquida todas as apostas da partida.</p>' +
    partidas.map((p) => `<div class="jogo admin-jogo" data-id="${p.id}">
      <span class="times">${esc(p.timeMandante)} x ${esc(p.timeVisitante)}</span>
      <input type="number" min="0" value="0" aria-label="Gols ${esc(p.timeMandante)}"> <span>x</span>
      <input type="number" min="0" value="0" aria-label="Gols ${esc(p.timeVisitante)}">
      <button class="btn sec">Encerrar</button></div>`).join('');
  el.querySelectorAll('.admin-jogo').forEach((row) => {
    row.querySelector('button').onclick = async () => {
      const [gm, gv] = [...row.querySelectorAll('input')].map((i) => Number(i.value));
      try {
        await api(`/partidas/${row.dataset.id}/resultado?adminId=${sessao.id}&codigoAcesso=${encodeURIComponent(sessao.codigo)}`,
          { method: 'POST', body: JSON.stringify({ golsMandante: gm, golsVisitante: gv }) });
        toast('Resultado registrado e apostas liquidadas.');
        if (selecao?.partida.id === Number(row.dataset.id)) { selecao = null; renderBilhete(); }
        await atualizarUsuario(); renderNav(); renderAdmin();
      } catch (e) { toast(e.message); }
    };
  });
}

/* ---------- Auth ---------- */
let modoCadastro = false;
function abrirAuth(cadastro) {
  modoCadastro = cadastro;
  $('#modal-titulo').textContent = cadastro ? 'Criar conta' : 'Entrar';
  $('#btn-auth').textContent = cadastro ? 'Criar conta' : 'Entrar';
  $('#alternar-auth').textContent = cadastro ? 'Já tenho conta' : 'Não tem conta? Cadastre-se e ganhe 1.000 créditos';
  $('#campos-cadastro').hidden = !cadastro;
  $('#campos-cadastro').querySelectorAll('input').forEach((i) => { i.required = cadastro; });
  $('#erro-auth').textContent = '';
  if (!$('#modal').open) $('#modal').showModal();
}
$('#alternar-auth').onclick = () => abrirAuth(!modoCadastro);
$('#fechar-modal').onclick = () => $('#modal').close();
$('#form-auth').onsubmit = async (ev) => {
  ev.preventDefault();
  const f = Object.fromEntries(new FormData(ev.target));
  try {
    if (modoCadastro) {
      await api('/usuarios', { method: 'POST', body: JSON.stringify({ nome: f.nome, email: f.email, username: f.username, endereco: f.endereco, codigo: f.codigo }) });
    }
    const u = await api('/usuarios/login', { method: 'POST', body: JSON.stringify({ email: f.email, codigo: f.codigo }) });
    salvarSessao({ id: u.id, nome: u.nome, saldo: u.saldo, perfil: u.perfil, codigo: f.codigo });
    $('#modal').close();
    ev.target.reset();
    toast(`Bem-vindo, ${u.nome}!`);
    renderNav(); renderBilhete(); render();
  } catch (e) { $('#erro-auth').textContent = e.message; }
};

/* ---------- Navegacao ---------- */
function trocarAba(nova) {
  aba = nova;
  document.querySelectorAll('.aba').forEach((b) => b.classList.toggle('ativa', b.dataset.aba === aba));
  renderNav();
  render();
}
function render() {
  return aba === 'apostas' ? renderApostas() : aba === 'admin' ? renderAdmin() : renderJogos();
}
document.querySelectorAll('.aba').forEach((b) => b.onclick = () => trocarAba(b.dataset.aba));

(async () => {
  await atualizarUsuario();
  renderNav(); renderBilhete(); render();
  // saldo e apostas atualizam sozinhos quando partidas sao liquidadas
  setInterval(async () => { if (sessao) { await atualizarUsuario(); renderNav(); } if (aba === 'apostas') renderApostas(); }, 30000);
})();
