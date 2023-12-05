package com.Kipfk.Library.images;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

public interface ImagesRepository extends JpaRepository<Images, Long> {

    @Transactional
    @Modifying
    @Query("update Images i set i.imageFile = :file, i.imageFileName = :fileName, i.imageFileContentType = :fileContentType where i.id =:imageId")
    void updateImageFileById(@Param("imageId") Long imageId, @Param("file") byte[] file, @Param("fileName") String fileName, @Param("fileContentType") String fileContentType);

    @Transactional
    void deleteAllById(Long imageId);
    Images findImagesById(Long imageId);
}

