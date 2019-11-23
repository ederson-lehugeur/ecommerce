package br.com.ecommerce.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.ecommerce.domain.Pedido;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

}
