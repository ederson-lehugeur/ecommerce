package br.com.ecommerce.resources;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.ecommerce.domain.Cidade;
import br.com.ecommerce.domain.Estado;
import br.com.ecommerce.dto.CidadeDTO;
import br.com.ecommerce.dto.EstadoDTO;
import br.com.ecommerce.services.CidadeService;
import br.com.ecommerce.services.EstadoService;

@RestController
@RequestMapping(value="/estados")
public class EstadoResource {

	@Autowired
	private EstadoService estadoService;

	@Autowired
	private CidadeService cidadeService;

	@RequestMapping(method=RequestMethod.GET)
	public ResponseEntity<List<EstadoDTO>> findAll() {
		List<Estado> listaEstados = estadoService.findAll();
		List<EstadoDTO> listaEstadosDto = listaEstados.stream().map(estado -> new EstadoDTO(estado)).collect(Collectors.toList());

		return ResponseEntity.ok().body(listaEstadosDto);
	}

	@RequestMapping(value="/{estadoId}/cidades", method=RequestMethod.GET)
	public ResponseEntity<List<CidadeDTO>> findCidades(@PathVariable Integer estadoId) {
		List<Cidade> listaCidades = cidadeService.findByEstado(estadoId);
		List<CidadeDTO> listaCidadesDto = listaCidades.stream().map(cidade -> new CidadeDTO(cidade)).collect(Collectors.toList());

		return ResponseEntity.ok().body(listaCidadesDto);
	}
}
