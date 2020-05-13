package com.hackday.timeline.image.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hackday.timeline.image.domain.Image;
import com.hackday.timeline.image.repository.ImageRepository;
import com.hackday.timeline.utils.s3.S3Service;

@Service
public class ImageServiceImpl implements ImageService {

	private ImageRepository imageRepository;

	private S3Service s3Service;

	@Autowired
	public ImageServiceImpl(ImageRepository imageRepository, S3Service s3Service){
		this.imageRepository = imageRepository;
		this.s3Service = s3Service;
	}

	@Override
	public Image saveImage(String filePath, String fileName) {
		Image image = new Image(filePath, fileName);
		return imageRepository.save(image);
	}

	@Override
	public void deleteImage(Long id) {
		s3Service.deleteFile(imageRepository.findOneById(id));
		imageRepository.deleteById(id);
	}

}
