package br.com.ecommerce.resources;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.com.ecommerce.domain.Categoria;
import br.com.ecommerce.dto.CategoriaDTO;
import br.com.ecommerce.services.CategoriaService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping(value="/categorias")
public class CategoriaResource {

	@Autowired
	private CategoriaService categoriaService;

	@ApiOperation(value="Busca por id")
	@RequestMapping(value="/{id}", method=RequestMethod.GET)
	public ResponseEntity<Categoria> find(@PathVariable Integer id) {

		Categoria categoria = categoriaService.find(id);

		return ResponseEntity.ok().body(categoria);
	}

	@ApiOperation(value="Retorna todas categorias")
	@RequestMapping(method=RequestMethod.GET)
	public ResponseEntity<List<CategoriaDTO>> findAll() {

		List<Categoria> listaCategoria = categoriaService.findAll();
		List<CategoriaDTO> listaCategoriaDTO = listaCategoria.stream().map(categoria -> new CategoriaDTO(categoria)).collect(Collectors.toList());

		return ResponseEntity.ok().body(listaCategoriaDTO);
	}

	@ApiOperation(value="Insere uma categoria")
	@PreAuthorize("hasAnyRole('ADMIN')")
	@RequestMapping(method=RequestMethod.POST)
	public ResponseEntity<Void> insert(@Valid @RequestBody CategoriaDTO categoriaDTO) {

		Categoria categoria = categoriaService.fromDTO(categoriaDTO);

		categoria = categoriaService.insert(categoria);

		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().
				path("/{id}").buildAndExpand(categoria.getId()).toUri();

		return ResponseEntity.created(uri).build();
	}

	@ApiOperation(value="Atualiza categoria por id")
	@PreAuthorize("hasAnyRole('ADMIN')")
	@RequestMapping(value="/{id}", method=RequestMethod.PUT)
	public ResponseEntity<Void> update(@PathVariable Integer id,
			@Valid @RequestBody CategoriaDTO categoriaDTO) {

		Categoria categoria = categoriaService.fromDTO(categoriaDTO);

		categoria.setId(id);
		categoria = categoriaService.update(categoria);

		return ResponseEntity.noContent().build();
	}

	@ApiOperation(value="Deleta categoria por id")
	@ApiResponses(value = {
			@ApiResponse(code=400, message="Não é possível excluir uma categoria que possui produtos"),
			@ApiResponse(code=404, message="Código inexistente") })
	@PreAuthorize("hasAnyRole('ADMIN')")
	@RequestMapping(value="/{id}", method=RequestMethod.DELETE)
	public ResponseEntity<Void> delete(@PathVariable Integer id) {

		categoriaService.delete(id);

		return ResponseEntity.noContent().build();
	}

	@ApiOperation(value="Retorna todas as categorias com paginação")
	@RequestMapping(value="/page", method=RequestMethod.GET)
	public ResponseEntity<Page<CategoriaDTO>> findPage(
			@RequestParam(value="page", defaultValue="0") Integer page,
			@RequestParam(value="linesPerPage", defaultValue="24") Integer linesPerPage,
			@RequestParam(value="orderBy", defaultValue="nome") String orderBy,
			@RequestParam(value="direction", defaultValue="ASC") String direction) {

		Page<Categoria> listaCategoria = categoriaService.findPage(page, linesPerPage, orderBy, direction);
		Page<CategoriaDTO> listaCategoriaDTO = listaCategoria.map(categoria -> new CategoriaDTO(categoria));

		return ResponseEntity.ok().body(listaCategoriaDTO);
	}

}
