package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.utils.DataUtils.adicionarDias;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoService {
	
	public Locacao alugarFilme(Usuario usuario, List<Filme> filmes) throws LocadoraException, FilmeSemEstoqueException {
		
		LocacaoDAO dao = null;
		double precoLocacao = 0;
		
		if(usuario == null){
			throw new LocadoraException("Usuario vazio");
		}
		
		if(filmes == null || filmes.isEmpty()){
			throw new LocadoraException("Não pode haver locação sem filmes");
		}
		for (int i = 0; i < filmes.size(); i++) {
			if(filmes.get(i).getEstoque() == 0){
				throw new FilmeSemEstoqueException();
			}
			switch(i){
				case 2:
					precoLocacao += filmes.get(i).getPrecoLocacao() * 0.75;
					break;
				case 3:
					precoLocacao += filmes.get(i).getPrecoLocacao() * 0.5;
					break;
				case 4:
					precoLocacao += filmes.get(i).getPrecoLocacao() * 0.25;
					break;
				case 5:
					break;
				default:
					precoLocacao += filmes.get(i).getPrecoLocacao();
			}
		}
		
		
		Locacao locacao = new Locacao();
		locacao.setFilmes(filmes);
		locacao.setUsuario(usuario);
		locacao.setDataLocacao(new Date());
		

		//Entrega no dia seguinte
		Date dataEntrega = new Date();
		dataEntrega = adicionarDias(dataEntrega, 1);
		
		
		if(DataUtils.verificarDiaSemana(dataEntrega, Calendar.SUNDAY))
			dataEntrega = adicionarDias(dataEntrega, 1);
		
		locacao.setDataRetorno(dataEntrega);
		
		locacao.setValor(precoLocacao);
		
		//Salvando a locacao...	
		dao.salvar(locacao);
		
		return locacao;
	}
}