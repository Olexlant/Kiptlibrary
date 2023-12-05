package com.Kipfk.Library.images;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ImagesService {
    private final ImagesRepository imagesRepository;

    public ImagesService(ImagesRepository imagesRepository) {
        this.imagesRepository = imagesRepository;
    }

    public Images addImageFile(MultipartFile image) throws IOException {
        Images newImageFile = new Images();
        newImageFile.setImageFile(image.getBytes());
        newImageFile.setImageFileContentType(image.getContentType());
        newImageFile.setImageFileName(image.getOriginalFilename());
        imagesRepository.save(newImageFile);
        return newImageFile;
    }
    public Images addQrCodeImageFile(byte[] qrCodeImage) throws IOException {
        Images newImageFile = new Images();
        newImageFile.setImageFile(qrCodeImage);
        newImageFile.setImageFileName("QrImage");
        imagesRepository.save(newImageFile);
        return newImageFile;
    }
    public void updateImageFileById(MultipartFile image, Long imageId) throws IOException {
        imagesRepository.updateImageFileById(imageId, image.getBytes(), image.getOriginalFilename(), image.getContentType());
    }
    public Images getImageById(Long imageId){
        return imagesRepository.findImagesById(imageId);
    }
    public void deleteImageById(Long id){
        imagesRepository.deleteAllById(id);
    }
}
