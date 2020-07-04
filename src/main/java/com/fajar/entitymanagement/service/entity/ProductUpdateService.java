package com.fajar.entitymanagement.service.entity;

import static com.fajar.entitymanagement.service.EntityService.ORIGINAL_PREFFIX;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.entitymanagement.dto.WebResponse;
import com.fajar.entitymanagement.entity.BaseEntity;
import com.fajar.entitymanagement.entity.Product;
import com.fajar.entitymanagement.repository.ProductRepository;
import com.fajar.entitymanagement.util.CollectionUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductUpdateService extends BaseEntityUpdateService {

	@Autowired
	private ProductRepository productRepository;

	/**
	 * add & update product
	 * 
	 * @param product
	 * @param newRecord
	 * @return
	 */
	@Override
	public WebResponse saveEntity(BaseEntity baseEntity, boolean newRecord,
			EntityUpdateInterceptor entityUpdateInterceptor) {

		Product product = (Product) copyNewElement(baseEntity, newRecord);

		String imageData = product.getImageUrl();
		if (imageData != null && !imageData.equals("")) {
			log.info("product image will be updated");

			String[] base64Images = imageData.split("~");
			if (base64Images != null && base64Images.length > 0) {
				String[] imageUrls = new String[base64Images.length];
				for (int i = 0; i < base64Images.length; i++) {
					String base64Image = base64Images[i];
					if (base64Image == null || base64Image.equals(""))
						continue;
					try {
						boolean updated = true;
						String imageName = null;
						if (base64Image.startsWith(ORIGINAL_PREFFIX)) {
							String[] raw = base64Image.split("}");
							if (raw.length > 1) {
								base64Image = raw[1];
							} else {
								imageName = raw[0].replace(ORIGINAL_PREFFIX, "");
								updated = false;
							}
						}
						if (updated) {
							imageName = fileService.writeImage(baseEntity.getClass().getSimpleName(), base64Image);
						}
						if (null != imageName)
							imageUrls[i] = (imageName);

					} catch (IOException e) {

						product.setImageUrl(null);
						e.printStackTrace();
					}
				}

				List<String> validUrls = removeNullItemFromArray(imageUrls);
				String[] arrayOfString = CollectionUtil.toArrayOfString(validUrls);

				CollectionUtil.printArray(arrayOfString);

				String imageUrl = String.join("~", arrayOfString);
				product.setImageUrl(imageUrl);

			}

		} else {
			log.info("Product image wont be updated");
			if (!newRecord) {
				Optional<Product> dbProduct = productRepository.findById(product.getId());
				if (dbProduct.isPresent()) {
					product.setImageUrl(dbProduct.get().getImageUrl());
				}
			}
		}

		Product newProduct = entityRepository.save(product);

//		if(newRecord) {
//			productInventoryService.addNewProduct(newProduct);
//		}

		return WebResponse.builder().entity(newProduct).build();
	}
}
