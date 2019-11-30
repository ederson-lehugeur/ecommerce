package br.com.ecommerce.resources;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.com.ecommerce.domain.Pedido;
import br.com.ecommerce.services.PedidoService;

@RestController
@RequestMapping(value="/pedidos")
public class PedidoResource {

	@Autowired
	private PedidoService pedidoService;

	@RequestMapping(value="/{id}", method=RequestMethod.GET)
	public ResponseEntity<Pedido> find(@PathVariable Integer id) {

		Pedido categoria = pedidoService.find(id);

		return ResponseEntity.ok().body(categoria);
	}

	@Transactional
	@RequestMapping(method=RequestMethod.POST)
	public ResponseEntity<Void> insert(@Valid @RequestBody Pedido pedido) {

		pedido = pedidoService.insert(pedido);

		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().
				path("/{id}").buildAndExpand(pedido.getId()).toUri();

		return ResponseEntity.created(uri).build();
	}

}
