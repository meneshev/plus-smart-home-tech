package store.service;

import dto.ProductCategory;
import dto.ProductDto;
import dto.SetProductQuantityStateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mapper.ProductMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.dal.repository.ProductRepository;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class StoreServiceImpl implements StoreService {
    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> getProductsByCategory(ProductCategory category, Pageable pageable) {
        return productRepository.findProductsByCategory(category, pageable).map(ProductMapper::convertToDto);
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) throws Exception {
        if (!productRepository.findById(UUID.fromString(productDto.getProductId())).isPresent()) {
            throw new Exception("Not found");  // TODO change exception
        }

        return ProductMapper.convertToDto(
                productRepository.save(ProductMapper.toEntity(productDto))
        );
    }

    @Override
    public ProductDto createProduct(ProductDto productDto) {
        return ProductMapper.convertToDto(
                productRepository.save(ProductMapper.toEntity(productDto))
        );
    }

    @Override
    public boolean removeProduct(String productId) {
        productRepository.deleteById(UUID.fromString(productId));
        return !productRepository.findById(UUID.fromString(productId)).isPresent();
    }

    @Override
    public boolean updateProductQt(SetProductQuantityStateRequest qtRequest) {
        return false;
    }

    @Override
    public ProductDto getProductById(String productId) {
        return null;
    }
}
