package br.com.ecommerce.services;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.ecommerce.domain.ItemPedido;
import br.com.ecommerce.domain.PagamentoComBoleto;
import br.com.ecommerce.domain.Pedido;
import br.com.ecommerce.domain.enums.EstadoPagamento;
import br.com.ecommerce.repositories.ItemPedidoRepository;
import br.com.ecommerce.repositories.PagamentoRepository;
import br.com.ecommerce.repositories.PedidoRepository;
import br.com.ecommerce.services.exception.ObjectNotFoundException;

@Service
public class PedidoService {

	@Autowired
	private PedidoRepository pedidoRepository;

	@Autowired
	private PagamentoRepository pagamentoRepository;

	@Autowired
	private ItemPedidoRepository itemPedidoRepository;

	@Autowired
	private BoletoService boletoService;

	@Autowired
	private ProdutoService produtoService;

	public Pedido find(Integer id) {
		Optional<Pedido> categoria = pedidoRepository.findById(id);

		return categoria.orElseThrow(() -> new ObjectNotFoundException("Objeto não encontrado! "
				+ "Id: " + id + ", Tipo: " + Pedido.class.getName()));
	}

	@Transactional
	public Pedido insert(Pedido pedido) {
		pedido.setId(null);
		pedido.setInstante(new Date());
		pedido.getPagamento().setEstado(EstadoPagamento.PENDENTE);
		pedido.getPagamento().setPedido(pedido);

		if (pedido.getPagamento() instanceof PagamentoComBoleto) {
			PagamentoComBoleto pagamento = (PagamentoComBoleto) pedido.getPagamento();
			boletoService.preencherPagamentoComBoleto(pagamento, pedido.getInstante());
		}

		pedido = pedidoRepository.save(pedido);
		pagamentoRepository.save(pedido.getPagamento());

		for (ItemPedido itemPedido : pedido.getItens()) {
			itemPedido.setDesconto(0.0);
			itemPedido.setPreco(produtoService.find(itemPedido.getProduto().getId()).getPreco());
			itemPedido.setPedido(pedido);
		}

		itemPedidoRepository.saveAll(pedido.getItens());

		return pedido;
	}
}
