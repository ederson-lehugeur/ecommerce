package br.com.ecommerce.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import br.com.ecommerce.domain.Categoria;
import br.com.ecommerce.dto.CategoriaDTO;
import br.com.ecommerce.repositories.CategoriaRepository;
import br.com.ecommerce.services.exception.DataIntegrityException;
import br.com.ecommerce.services.exception.ObjectNotFoundException;

@Service
public class CategoriaService {

	@Autowired
	private CategoriaRepository categoriaRepository;

	public Categoria find(Integer id) {
		Optional<Categoria> categoria = categoriaRepository.findById(id);

		return categoria.orElseThrow(() -> new ObjectNotFoundException("Objeto não encontrado! "
				+ "Id: " + id + ", Tipo: " + Categoria.class.getName()));
	}

	public List<Categoria> findAll() {
		return categoriaRepository.findAll();
	}

	public Categoria insert(Categoria categoria) {
		categoria.setId(null);

		return categoriaRepository.save(categoria);
	}

	public Categoria update(Categoria categoria) {
		Categoria novaCategoria = find(categoria.getId());

		updateData(novaCategoria, categoria);

		return categoriaRepository.save(novaCategoria);
	}

	public void delete(Integer id) {
		find(id);

		try {
			categoriaRepository.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityException("Não é possível excluir uma categoria que possui produtos.");
		}
	}

	public Page<Categoria> findPage(Integer page, Integer linesPerPage, String orderBy, String direction) {
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);

		return categoriaRepository.findAll(pageRequest);
	}

	public Categoria fromDTO(CategoriaDTO categoriaDTO) {
		return new Categoria(categoriaDTO.getId(), categoriaDTO.getNome());
	}

	private void updateData(Categoria novaCategoria, Categoria categoria) {
		novaCategoria.setNome(categoria.getNome());
	}

}
