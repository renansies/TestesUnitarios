package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;

public class LocacaoServiceTest {
	
	private LocacaoService service;
	private List<Filme> filmes;
	
	@Rule
	public ErrorCollector errors = new ErrorCollector();
	
	@Rule
	public ExpectedException exceptions = ExpectedException.none();
	
	@Before
	public void setup() {
		service = new LocacaoService();
		filmes = new ArrayList<Filme>();
	}
	
	@Test
	public void testAlugarFilme() throws Exception {
		
		//cenario
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 2, 5.0);
		filmes.add(filme);
		
		//acao
		Locacao locacao = service.alugarFilme(usuario, filmes);
			
		//verificacao
		errors.checkThat(locacao.getValor(), is(equalTo(5.0)));
		errors.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
		errors.checkThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));
	}
	
	@Test(expected=FilmeSemEstoqueException.class)
	public void testAlugarFilmeSemEstoque() throws Exception{
		
		//cenario
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 0, 5.0);
		filmes.add(filme);
		
		//acao
		service.alugarFilme(usuario, filmes);
	}
	
	@Test
	public void testAlugarFilmeSemUsuario() throws FilmeSemEstoqueException{
		//cenario
		Filme filme = new Filme("Filme 1", 2, 5.0);
		filmes.add(filme);
		
		//acao
		try {
			service.alugarFilme(null, filmes);
			Assert.fail();
		} catch (LocadoraException e) {
			assertThat(e.getMessage(), is("Usuario vazio"));
		}
	}
	
	@Test
	public void testAlugarFilmeSemFilme() throws FilmeSemEstoqueException, LocadoraException{
		//cenario
		Usuario usuario = new Usuario("Usuario 1");
		
		exceptions.expect(LocadoraException.class);
		exceptions.expectMessage("Não pode haver locação sem filmes");
		
		//acao
		service.alugarFilme(usuario, filmes);
	}
	
}
