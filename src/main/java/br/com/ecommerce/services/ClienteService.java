package br.com.ecommerce.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.ecommerce.domain.Cidade;
import br.com.ecommerce.domain.Cliente;
import br.com.ecommerce.domain.Endereco;
import br.com.ecommerce.domain.enums.TipoCliente;
import br.com.ecommerce.dto.ClienteDTO;
import br.com.ecommerce.dto.ClienteNewDTO;
import br.com.ecommerce.repositories.ClienteRepository;
import br.com.ecommerce.repositories.EnderecoRepository;
import br.com.ecommerce.services.exception.DataIntegrityException;
import br.com.ecommerce.services.exception.ObjectNotFoundException;

@Service
public class ClienteService {

	@Autowired
	private ClienteRepository clienteRepository;

	@Autowired
	private EnderecoRepository enderecoRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	public Cliente find(Integer id) {
		Optional<Cliente> cliente = clienteRepository.findById(id);

		return cliente.orElseThrow(() -> new ObjectNotFoundException("Objeto não encontrado! "
				+ "Id: " + id + ", Tipo: " + Cliente.class.getName()));
	}

	public List<Cliente> findAll() {
		return clienteRepository.findAll();
	}

	@Transactional
	public Cliente insert(Cliente cliente) {
		cliente.setId(null);

		cliente = clienteRepository.save(cliente);

		enderecoRepository.saveAll(cliente.getEnderecos());

		return cliente;
	}

	public Cliente update(Cliente cliente) {
		Cliente novoCliente = find(cliente.getId());

		updateData(novoCliente, cliente);

		return clienteRepository.save(novoCliente);
	}

	public void delete(Integer id) {
		find(id);

		try {
			clienteRepository.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityException("Não é possível excluir porque há pedidos relacionados");
		}
	}

	public Page<Cliente> findPage(Integer page, Integer linesPerPage, String orderBy, String direction) {
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);

		return clienteRepository.findAll(pageRequest);
	}

	public Cliente fromDTO(ClienteDTO clienteDto) {
		return new Cliente(clienteDto.getId(), clienteDto.getNome(), clienteDto.getEmail(), null, null, null);
	}

	public Cliente fromDTO(ClienteNewDTO clienteNewDto) {
		Cliente cliente = new Cliente(null, clienteNewDto.getNome(), clienteNewDto.getEmail(),
				clienteNewDto.getCpfOuCnpj(), TipoCliente.toEnum(clienteNewDto.getTipoCliente()),
				bCryptPasswordEncoder.encode(clienteNewDto.getSenha()));

		Cidade cidade = new Cidade(clienteNewDto.getCidadeId(), null, null);

		Endereco endereco = new Endereco(null, clienteNewDto.getLogradouro(), clienteNewDto.getNumero(),
				clienteNewDto.getComplemento(), clienteNewDto.getBairro(), clienteNewDto.getCep(),
				cliente, cidade);

		cliente.getEnderecos().add(endereco);
		cliente.getTelefones().add(clienteNewDto.getTelefone1());
		if (clienteNewDto.getTelefone2() != null) {
			cliente.getTelefones().add(clienteNewDto.getTelefone2());
		}
		if (clienteNewDto.getTelefone3() != null) {
			cliente.getTelefones().add(clienteNewDto.getTelefone3());
		}

		return cliente;
	}

	private void updateData(Cliente novoCliente, Cliente cliente) {
		novoCliente.setNome(cliente.getNome());
		novoCliente.setEmail(cliente.getEmail());
	}
}
