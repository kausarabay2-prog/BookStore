package Book.bitlab.BookStore.service;

import Book.bitlab.BookStore.Entity.Category;
import Book.bitlab.BookStore.dto.CategoryDTO;
import Book.bitlab.BookStore.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    private CategoryDTO toDto(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    private Category find(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Category not found: " + id));
    }

    private String cleanName(CategoryDTO dto) {
        if (dto == null || dto.getName() == null || dto.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category name is required");
        }
        return dto.getName().trim();
    }

    @Transactional(readOnly = true)
    public List<CategoryDTO> getAll() {
        return categoryRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public CategoryDTO getById(Long id) {
        return toDto(find(id));
    }

    public CategoryDTO create(CategoryDTO dto) {
        String name = cleanName(dto);

        if (categoryRepository.findByNameIgnoreCase(name).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Category already exists");
        }

        return toDto(categoryRepository.save(Category.builder().name(name).build()));
    }

    public CategoryDTO update(Long id, CategoryDTO dto) {
        Category category = find(id);
        category.setName(cleanName(dto));
        return toDto(categoryRepository.save(category));
    }

    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found: " + id);
        }
        categoryRepository.deleteById(id);
    }
}