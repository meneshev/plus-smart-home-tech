package store.service;

import dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.dal.mapper.ProductMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import store.dal.repository.ProductRepository;
import util.exception.NotFoundException;
import util.exception.ValidationException;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {
    private final ProductRepository productRepository;

    @Override
    public Page<ProductDto> getProductsByCategory(ProductCategory category, Pageable pageable) {
        return productRepository.findProductsByProductCategory(category, pageable).map(ProductMapper::convertToDto);
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        checkProductExists(productDto.getProductId());
        return ProductMapper.convertToDto(
                productRepository.save(ProductMapper.toEntity(productDto))
        );
    }

    @Override
    public ProductDto createProduct(ProductDto productDto) {
        if (productDto.getProductId() != null) {
            throw new ValidationException("productId must be null");
        }
        return ProductMapper.convertToDto(
                productRepository.save(ProductMapper.toEntity(productDto))
        );
    }

    @Override
    public boolean removeProduct(String productId) {
        checkProductExists(productId);
        productRepository.removeProduct(UUID.fromString(productId));
        return productRepository.findById(UUID.fromString(productId)).get().getProductState().equals(ProductState.DEACTIVATE);
    }

    @Override
    public boolean updateProductQt(String quantityState, String productId) {
        checkProductExists(productId);

        UUID uuid = UUID.fromString(productId);
        QuantityState qState;
        try {
            qState = QuantityState.valueOf(quantityState);
        } catch (IllegalArgumentException e) {
            log.error("Incorrect quantity state: {}", quantityState);
            throw new ValidationException(String.format("Incorrect quantity state: %s", quantityState));
        }

        productRepository.setQuantityStateByProductId(qState, uuid);
        return productRepository.findById(uuid).get().getQuantityState().equals(qState);
    }

    @Override
    public ProductDto getProductById(String productId) {
        checkProductExists(productId);
        return ProductMapper.convertToDto(productRepository.findById(UUID.fromString(productId)).get());
    }

    private void checkProductExists(String productId) {
        if (productRepository.findById(UUID.fromString(productId)).isEmpty()) {
            log.error("Product with id:{} not found",  productId);
            throw new NotFoundException(String.format("Product with id:%s not found", productId));
        }
    }
}