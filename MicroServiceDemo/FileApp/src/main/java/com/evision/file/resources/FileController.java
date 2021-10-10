package com.evision.file.resources;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.evision.file.model.CsvTransactionParsingResult;
import com.evision.file.model.TransactionDto;
import com.evision.file.service.FileService;
import com.opencsv.bean.CsvToBean;

@RestController
@RequestMapping("/api/v1/file")
public class FileController {
	
	
	private final FileService fileService;
	
	@Autowired
	public FileController(FileService fileService) {
		this.fileService = fileService;
	}
	
	@GetMapping("/test")
	public String test() {
		return "File services are alive.";
	}
	
	@PostMapping("/checksum")
    public ResponseEntity<String> processTransactions(@RequestParam("file") MultipartFile file) {
        try {
            String actualCheckSum = fileService.getFileChecksum(file.getInputStream());
            return new ResponseEntity<>(actualCheckSum, HttpStatus.OK);
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }
	
	@PostMapping("/verify")
	public CsvTransactionParsingResult verifyAndParseFile(
			@RequestParam("file") MultipartFile file,
            @RequestParam("checksum") String checksum,
            @RequestParam(value = "separator", defaultValue = ",", required = false) String separator){
		
		CsvTransactionParsingResult returnObject = new CsvTransactionParsingResult();
		try {
			returnObject.setVerificationStatus(fileService.verifyFile(file, checksum));
			if(!returnObject.getVerificationStatus().isEmpty()) {
				return returnObject;
			}
			
			CsvToBean<TransactionDto> beansList = fileService.convertCsvFileToBeanList(file, separator);
			returnObject.setTrxList(beansList.parse());
			beansList.getCapturedExceptions().stream().forEach((exception) -> {
	            returnObject.getErrorsList().add(String.format("Inconsistent data: {record number: %s, error: %s}"
	                    , exception.getLineNumber(), exception.getCause()));
	        });
			
		} catch (NoSuchAlgorithmException | IOException e) {
			e.printStackTrace();
		} 
		
		return returnObject;
	}
}
