package com.akn;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class AppController {

	@Autowired
	private FileDataRepository fileDataRepository;

	@PostMapping("/upload")
	public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile multipartFile) throws IOException {
		String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

		FileData fileData = FileData.builder().fileName(fileName).fileType(multipartFile.getContentType())
				.fileSize(multipartFile.getSize()).uploadDateTime(LocalDateTime.now())
				.fileContent(multipartFile.getBytes()).build();

		FileData savedFile = fileDataRepository.save(fileData);

		if (savedFile != null) {
			return ResponseEntity.ok("File saved successfully : " + fileName);
		}
		return null;

	}

	@GetMapping("/downloadFile")
	public void downloadFile(@RequestParam("fileName") String fileName, HttpServletResponse response) throws Exception {

		Optional<FileData> result = fileDataRepository.findByFileName(fileName);

		if (!result.isPresent()) {
			throw new Exception("Could not find file with name : " + fileName);
		}

		FileData file = result.get();

		response.setContentType("application/octet-stream");
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=" + file.getFileName();

		response.setHeader(headerKey, headerValue);

		ServletOutputStream outputStream = response.getOutputStream();

		outputStream.write(file.getFileContent());
		outputStream.close();
	}

}
