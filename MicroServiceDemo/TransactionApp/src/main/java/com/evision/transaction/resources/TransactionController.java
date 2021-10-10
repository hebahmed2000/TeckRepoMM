package com.evision.transaction.resources;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.evision.transaction.models.dto.ResponseDto;
import com.evision.transaction.models.dto.TransactionDto;
import com.evision.transaction.service.TransactionService;
import com.evision.transaction.converter.MultipartInputStreamFileResource;
import com.evision.transaction.models.dto.CsvTransactionParsingResult;

@RestController
@RequestMapping("/api/v1/transaction")
public class TransactionController {
	
	
	private final RestTemplate restTemplate;
	private final TransactionService transactionService;
	
	@Autowired
	public TransactionController(TransactionService transactionService, RestTemplate restTemplate) {
		this.transactionService = transactionService;
		this.restTemplate = restTemplate;
	}
	
	@GetMapping("/test")
	public ResponseEntity<String> test() {
		ResponseEntity<String> fileResult = restTemplate.getForEntity("http://localhost:8082/api/v1/file/test/", String.class);
		
		return fileResult;
	}
	
	@PostMapping("/parseData")
	public ResponseEntity<ResponseDto> parseData(@RequestParam("file") MultipartFile file,
            @RequestParam(value = "checksum", defaultValue = "") String checksum,
            @RequestParam(value = "separator", defaultValue = ",", required = false) String separator){
		
		ResponseDto responseDto = new ResponseDto();

        try {
            fillResponseDTO(responseDto, file, checksum, separator);
            if (!responseDto.isVerified()) {
                responseDto.setResponseCode("002");
                responseDto.setResponseDesc(String.format("Verification errors: %s", responseDto.getVerificationErrors()));
                return new ResponseEntity<>(responseDto, HttpStatus.EXPECTATION_FAILED);
            }
            if (!responseDto.isValid()) {
                responseDto.setResponseCode("003");
                responseDto.setResponseDesc(String.format("File is verified but has validation errors, {Number of valid records: %s} -  {number of invalid records: %s} - {validation errors: %s}", responseDto.getValidTransactions(), responseDto.getInvalidTransactions(), responseDto.getValidationErrors()));
                return new ResponseEntity<>(responseDto, HttpStatus.EXPECTATION_FAILED);
            } else {
                responseDto.setResponseCode("001");
                responseDto.setResponseDesc(String.format("File is verified and valid - {Total number of records: %s} ", responseDto.getValidTransactions()));
                return new ResponseEntity<>(responseDto, HttpStatus.OK);
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            responseDto.setResponseCode("004");
            responseDto.setResponseDesc(String.format("Errors occured during file processing - {exceptions: %s}", e.getMessage()));
            return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
        }
		
	}
	
	private void fillResponseDTO(ResponseDto responseDto, MultipartFile file, String checksum, String separator) throws IOException, NoSuchAlgorithmException {
		
		LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
	   
//		if(!separator.isEmpty())
//			map.add("separator", separator);
			
		if (!file.isEmpty()) {
            map.add("file", new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));
            map.add("checksum", checksum);
            map.add("separator", separator);
        } else {
        	responseDto.setVerificationErrors("File is empty");
        	return;
        }
		
		HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        String url = "http://FILE-APP-SERVICE/api/v1/file/verify";

        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);
        CsvTransactionParsingResult result = restTemplate.postForObject(url, requestEntity, CsvTransactionParsingResult.class);
		
		responseDto.setVerificationErrors(result.getVerificationStatus());
			
		
        List<TransactionDto> beansList = result.getTrxList();
        StringBuilder sb = new StringBuilder();
        
        result.getErrorsList().stream().forEach((exception) -> {
            sb.append(exception);
        });
        
        responseDto.setValidationErrors(sb.toString());
        responseDto.setValidTransactions(beansList.size());
        responseDto.setInvalidTransactions(result.getErrorsList().size());
        responseDto.setWithdrawTransactions(transactionService.countWithdrawTransactions(beansList));
        responseDto.setDepositTransactions(transactionService.countDepositTransactions(beansList));

    }
}
