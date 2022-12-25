package com.akn;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FileDataRepository extends JpaRepository<FileData, Long> {

	Optional<FileData> findByFileName(String fileName);

}
