package com.evision.file.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.sql.rowset.spi.TransactionalWriter;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.evision.file.model.TransactionDto;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

@Service
public class FileService {

	public final Long FILE_MAX_SIZE = 209715200L; // 200 MB

    public String verifyFile(MultipartFile file,String checksum)throws IOException, NoSuchAlgorithmException {
        if(!isValidExtension(file.getOriginalFilename()))
            return "Invalid file extension";
        if(!isValidFileSize(file.getSize(),file.isEmpty()))
            return "invalid file size";
        if(!isValidChecksum(file.getInputStream(),checksum))
            return "invalid checksum";
        return "";
    }

    private boolean isValidChecksum(InputStream fis,String checksum) throws IOException, NoSuchAlgorithmException {
        String actualCheckSum = getFileChecksum(fis);
        System.out.println("actualchecksum: " + actualCheckSum);
        return checksum.equals(actualCheckSum);
    }

    private boolean isValidFileSize(long fileSize,boolean emptyFile){
        return (!emptyFile && fileSize > 0 && fileSize <= FILE_MAX_SIZE);
    }

    private boolean isValidExtension(String fileName) {
        return fileName.toLowerCase().endsWith(".csv");
    }



    public String getFileChecksum(InputStream fis) throws IOException, NoSuchAlgorithmException {
        //Use MD5 algorithm
        MessageDigest digest = MessageDigest.getInstance("MD5");

        //Get file input stream for reading the file content
        //FileInputStream fis = new FileInputStream(file);

        //Create byte array to read data in chunks
        byte[] byteArray = new byte[1024];
        int bytesCount = 0;

        //Read file data and update in message digest
        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        };

        //close the stream; We don't need it now.
        fis.close();

        //Get the hash's bytes
        byte[] bytes = digest.digest();

        //This bytes[] has bytes in decimal format;
        //Convert it to hexadecimal format
        StringBuilder sb = new StringBuilder();
        for(int i=0; i< bytes.length ;i++)
        {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        //return complete hash
        return sb.toString();
    }


    public void readFileChunks(InputStream inputStream) throws IOException {
        try (
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

                //OutputStream outputStream = new FileOutputStream(target);
        ) {
            byte[] buffer = new byte[4 * 1024];
            int read;
            while ((read = bufferedInputStream.read(buffer, 0, buffer.length)) != -1) {
                //outputStream.write(buffer, 0, read);
                System.out.println("buffer: "+new String(buffer));
            }
        }
    }

    public <T extends Object> CsvToBean<T> convertCsvFileToBeanList(MultipartFile file,String separator) throws IOException {

        File csvFile = convertMultiPartToFile(file);
        CsvToBean<T> beans = new CsvToBeanBuilder(new FileReader(csvFile))
                .withSkipLines(1)
                .withSeparator(separator.charAt(0))
                .withType(TransactionDto.class)
                .withThrowExceptions(false)
                .withIgnoreEmptyLine(true)
                .build();
                return beans;
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException{
        File convFile = new File( file.getOriginalFilename() );
        FileOutputStream fos = new FileOutputStream( convFile );
        fos.write( file.getBytes() );
        fos.close();
        return convFile;
    }
	
}
