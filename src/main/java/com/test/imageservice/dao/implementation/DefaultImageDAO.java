package com.test.imageservice.dao.implementation;

import com.test.imageservice.dao.ImageDAO;
import com.test.imageservice.models.Image;
import com.test.imageservice.models.dto.ImageSearchDTO;
import com.test.imageservice.presentation.exception.InputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

@Repository
public class DefaultImageDAO implements ImageDAO {

    @Autowired
    private EntityManager entityManager;

    public Page<Image> searchByAllProperties(ImageSearchDTO imageSearchDTO, Pageable pageable)
            throws InputException {

        List<String> tags = imageSearchDTO.getTags();
        boolean tagsPresented = tags != null;

        String stringQuery = buildQuery(imageSearchDTO, tagsPresented);
        int pageSize = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();
        Query query = entityManager
                .createQuery(stringQuery);

        List images;
        long count;

        if(tagsPresented){
            long amountOfTags = tags.size();
            count = query
                    .setParameter(1, tags)
                    .setParameter(2, amountOfTags)
                    .getResultList()
                    .size();
            query.setFirstResult(pageNumber * pageSize);
            query.setMaxResults(pageSize);
            images = query
                    .getResultList();
        }else{
            count = query
                    .getResultList()
                    .size();
            query.setFirstResult(pageNumber * pageSize);
            query.setMaxResults(pageSize);
            images = query.getResultList();
        }

        return new PageImpl<>(images, pageable, count);
    }

    private String buildQuery(ImageSearchDTO imageSearchDTO, boolean tagsPresented) throws InputException {

        String checkPropertiesString = buildPropertiesCheckString(imageSearchDTO, tagsPresented);

        if(checkPropertiesString.equals("") && !tagsPresented){
            throw new InputException("No properties or tags provided");
        }

        String query = "SELECT DISTINCT(image) FROM Image image JOIN image.tags tag WHERE ";

        if(tagsPresented){
            query += " tag.tagName IN (?1) ";
        }
        query += checkPropertiesString + "GROUP BY image.id ";

        if(tagsPresented){
            query += "HAVING COUNT(DISTINCT tag.id) = (?2)";
        }

        return query;
    }

    private String buildPropertiesCheckString(ImageSearchDTO imageSearchDTO, boolean tagsPresented){
        String propertiesCheckString = "";
        boolean notFirstProperty = false;

        String imageName = imageSearchDTO.getImageName();
        if(imageName != null ){
            if(tagsPresented){
                propertiesCheckString += " and";
            }
            propertiesCheckString += " image.imageName = '" + imageName + "' ";
            notFirstProperty = true;
        }
        String reference = imageSearchDTO.getReference();
        if(reference != null ){
            if(notFirstProperty || tagsPresented){
                propertiesCheckString += " and";
            }
            propertiesCheckString += " image.reference = '" + reference + "' ";
            notFirstProperty = true;
        }
        Long size = imageSearchDTO.getSize();
        if(size != null ){
            if(notFirstProperty || tagsPresented){
                propertiesCheckString += " and";
            }
            propertiesCheckString += " image.size = " + size + " ";
            notFirstProperty = true;
        }
        String contentType = imageSearchDTO.getContentType();
        if(contentType != null ){
            if(notFirstProperty || tagsPresented){
                propertiesCheckString += " and";
            }
            propertiesCheckString += " image.contentType = '" + contentType + "' ";
        }
        return propertiesCheckString;
    }
}